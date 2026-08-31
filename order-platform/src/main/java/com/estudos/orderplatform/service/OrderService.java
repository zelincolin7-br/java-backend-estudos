package com.estudos.orderplatform.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estudos.orderplatform.config.RabbitMQConfig;
import com.estudos.orderplatform.domain.Order;
import com.estudos.orderplatform.domain.OrderItem;
import com.estudos.orderplatform.domain.Product;
import com.estudos.orderplatform.dto.OrderItemRequestDto;
import com.estudos.orderplatform.dto.OrderRequestDto;
import com.estudos.orderplatform.dto.OrderResponseDto;
import com.estudos.orderplatform.event.OrderCreatedEvent;
import com.estudos.orderplatform.exception.ResourceNotFoundException;
import com.estudos.orderplatform.observability.NewRelicRabbitTrace;
import com.estudos.orderplatform.observability.RequestTraceMdc;
import com.estudos.orderplatform.repository.OrderRepository;
import com.estudos.orderplatform.repository.ProductRepository;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.TraceMetadata;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final RabbitTemplate rabbitTemplate;

    public OrderService(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            RabbitTemplate rabbitTemplate) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public OrderResponseDto save(OrderRequestDto requestDto) {
        TraceMetadata traceMetadata = NewRelic.getAgent().getTraceMetadata();
        String currentTraceId = traceMetadata.getTraceId();
        String currentSpanId = traceMetadata.getSpanId();

        RequestTraceMdc.put(currentTraceId, currentSpanId);

        try {
            if (currentTraceId != null && !currentTraceId.isBlank()) {
                NewRelic.addCustomParameter("order.producer.traceId", currentTraceId);
            }

            int totalItems = (requestDto.items() != null) ? requestDto.items().size() : 0;

            log.info(
                    "Iniciando criação de pedido com {} itens. traceId={}, spanId={}",
                    totalItems,
                    currentTraceId,
                    currentSpanId);

            Order order = new Order();

            for (OrderItemRequestDto itemDto : requestDto.items()) {
                Product product = findProduct(itemDto.productId());

                OrderItem item = new OrderItem(product, order, itemDto.quantity(), product.getPrice());
                order.addItem(item);
            }

            Order savedOrder = orderRepository.save(order);
            RequestTraceMdc.putOrderId(savedOrder.getId());
            NewRelic.addCustomParameter("order.id", savedOrder.getId());

            log.info(
                    "Pedido criado com sucesso! Order ID: {}, Valor Total: {}",
                    savedOrder.getId(),
                    savedOrder.getTotal());

            OrderCreatedEvent event = new OrderCreatedEvent(
                    savedOrder.getId(),
                    savedOrder.getTotal(),
                    LocalDateTime.now());

            log.info(
                    "Publicando evento OrderCreatedEvent para a exchange '{}' com routing key '{}'. traceId={}",
                    RabbitMQConfig.ORDER_EVENTS_EXCHANGE,
                    RabbitMQConfig.ORDER_CREATED_ROUTING_KEY,
                    currentTraceId);

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_EVENTS_EXCHANGE,
                    RabbitMQConfig.ORDER_CREATED_ROUTING_KEY,
                    event,
                    NewRelicRabbitTrace::injectTraceHeaders);

            log.info("Evento OrderCreatedEvent enviado com sucesso. orderId={}", savedOrder.getId());

            return new OrderResponseDto(savedOrder);
        } finally {
            RequestTraceMdc.clear();
        }
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable).map(OrderResponseDto::new);
    }

    @Transactional(readOnly = true)
    public OrderResponseDto findById(Long id) {
        log.debug("Buscando pedido por ID: {}", id);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Pedido não encontrado para o ID: {}", id);
                    return new ResourceNotFoundException("Pedido não encontrado com o ID: " + id);
                });

        return new OrderResponseDto(order);
    }

    private Product findProduct(String productRef) {
        if (productRef == null || productRef.isBlank()) {
            throw new ResourceNotFoundException("Produto não informado");
        }

        String ref = productRef.trim();
        if (ref.chars().allMatch(Character::isDigit)) {
            Long id = Long.parseLong(ref);
            return productRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Produto não encontrado com o ID: " + id));
        }

        return productRepository.findBySku(ref)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produto não encontrado com o SKU: " + ref));
    }
}
