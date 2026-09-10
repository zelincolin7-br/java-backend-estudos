package com.estudos.orderplatform.service;

import com.estudos.orderplatform.config.RabbitMQConfig;
import com.estudos.orderplatform.domain.Order;
import com.estudos.orderplatform.domain.OrderItem;
import com.estudos.orderplatform.domain.OrderStatus;
import com.estudos.orderplatform.domain.Product;
import com.estudos.orderplatform.dto.CreateOrderRequestDTO;
import com.estudos.orderplatform.dto.OrderAuditRequestDTO;
import com.estudos.orderplatform.dto.OrderResponseDTO;
import com.estudos.orderplatform.exception.ResourceNotFoundException;
import com.estudos.orderplatform.repository.OrderRepository;
import com.estudos.orderplatform.repository.ProductRepository;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    @Trace(dispatcher = true)
    public OrderResponseDTO createOrder(CreateOrderRequestDTO dto) {
        log.info("Iniciando a criação do pedido para o cliente '{}' com {} item(ns)...", 
            dto.customerId(), dto.items().size());

        Order order = new Order();

        // Mapeia os DTOs de itens para a entidade OrderItem e associa à Order
        dto.items().forEach(itemDto -> {
            Product product = productRepository.findById(itemDto.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com ID: " + itemDto.productId()));

            OrderItem item = new OrderItem(product, order, itemDto.quantity(), itemDto.price());
            order.addItem(item);
        });

        Order savedOrder = orderRepository.save(order);

        log.info("Pedido criado com sucesso no PostgreSQL com ID: {} | Cliente: {} | Total: R$ {}", 
            savedOrder.getId(), dto.customerId(), savedOrder.getTotal());

        // Mapeia os itens do pedido para uma lista de Map com os detalhes dos produtos
        List<Map<String, Object>> itemPayloads = savedOrder.getItems().stream()
            .map(item -> Map.<String, Object>of(
                "productId", item.getProduct().getId(),
                "productName", item.getProduct().getName(),
                "quantity", item.getQuantity(),
                "price", item.getPrice()            
            ))
            .toList();    

        // Métricas e Atributos Customizados no New Relic
        NewRelic.addCustomParameter("order.id", savedOrder.getId());
        NewRelic.addCustomParameter("order.customerId", dto.customerId());
        NewRelic.addCustomParameter("order.totalAmount", savedOrder.getTotal().doubleValue());
        NewRelic.addCustomParameter("order.itemsCount", savedOrder.getItems().size());

        // Evento para o RabbitMQ (consumido pelo MongoDB Audit Service)
        OrderAuditRequestDTO event = new OrderAuditRequestDTO(
            savedOrder.getId(),
            "ORDER_CREATED",
            "NONE",
            savedOrder.getStatus().name(),
            Map.of(
                "customerId", dto.customerId(),
                "totalAmount", savedOrder.getTotal(),
                "itemsCount", savedOrder.getItems().size(),
                "items", itemPayloads
            )
        );

        rabbitTemplate.convertAndSend(
            RabbitMQConfig.ORDER_EVENTS_EXCHANGE,
            RabbitMQConfig.ORDER_CREATED_ROUTING_KEY,
            event
        );

        log.info("Evento ORDER_CREATED disparado para a exchange '{}' referente ao Pedido ID: {}", 
            RabbitMQConfig.ORDER_EVENTS_EXCHANGE, savedOrder.getId());

        return OrderResponseDTO.fromEntity(savedOrder);
    }

    @Transactional
    @Trace(dispatcher = true)
    public OrderResponseDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        log.info("Iniciando a alteração de status do Pedido ID: {} para {}...", orderId, newStatus);

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado com ID: " + orderId));

        OrderStatus previousStatus = order.getStatus();

        // Evita atualizações desnecessárias se o status já for o mesmo
        if (previousStatus == newStatus) {
            log.warn("O pedido ID: {} já se encontra no status {}", orderId, newStatus);
            return OrderResponseDTO.fromEntity(order);
        }

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);

        // Métricas e Atributos Customizados no New Relic
        NewRelic.addCustomParameter("order.id", updatedOrder.getId());
        NewRelic.addCustomParameter("order.previousStatus", previousStatus.name());
        NewRelic.addCustomParameter("order.newStatus", newStatus.name());

        // Evento genérico de auditoria padronizado para o RabbitMQ/MongoDB
        OrderAuditRequestDTO event = new OrderAuditRequestDTO(
            updatedOrder.getId(),
            "ORDER_STATUS_CHANGED",
            previousStatus.name(),
            newStatus.name(),
            Map.of(
                "updatedBy", "KDS_PANEL",
                "totalAmount", updatedOrder.getTotal()
            )
        );

        // Certifique-se de usar a constante da sua RabbitMQConfig ou a routing key adequada
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.ORDER_EVENTS_EXCHANGE,
            "order.status.updated", // Ou RabbitMQConfig.ORDER_STATUS_UPDATED_ROUTING_KEY se declarada
            event
        );

        log.info("Status do Pedido ID: {} alterado com sucesso de {} para {} e evento ORDER_STATUS_CHANGED enviado!", 
            orderId, previousStatus.name(), newStatus.name());

        return OrderResponseDTO.fromEntity(updatedOrder);
    }
}