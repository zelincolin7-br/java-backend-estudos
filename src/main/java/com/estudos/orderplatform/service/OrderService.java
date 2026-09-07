package com.estudos.orderplatform.service;

import com.estudos.orderplatform.event.OrderCreatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderService {

    private final RabbitTemplate rabbitTemplate;
    
    @Value("${app.rabbitmq.exchange:order.events}")
    private String exchange;

    @Value("${app.rabbitmq.routing-key-created:order.created}")
    private String routingKeyCreated;

    public OrderService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void createOrder(Long customerId, BigDecimal totalValue) {
        // 1. Lógica para salvar a entidade no banco 'order_db' (Ex: orderRepository.save(...))
        Long mockOrderId = System.currentTimeMillis(); // Simulação do ID gerado pelo banco
        String status = "CRIADO";

        // 2. Criação do evento
        OrderCreatedEvent event = new OrderCreatedEvent(mockOrderId, customerId, totalValue, status);

        // 3. Disparo do evento para o RabbitMQ
        rabbitTemplate.convertAndSend(exchange, routingKeyCreated, event);
        
        System.out.println("Pedido " + mockOrderId + " registrado e evento enviado para o RabbitMQ!");
    }
}