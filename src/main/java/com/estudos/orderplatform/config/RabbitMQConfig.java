package com.estudos.orderplatform.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ORDER_EVENTS_EXCHANGE = "order.events";
    public static final String ORDER_CREATED_QUEUE = "order.created.notification.queue";
    public static final String ORDER_CREATED_ROUTING_KEY = "order.created";

    // Módulo de Auditoria (MongoDB)
    public static final String ORDER_AUDIT_QUEUE = "order.audit.queue";
    public static final String ORDER_AUDIT_ROUTING_KEY = "order.#"; // Captura qualquer evento que comece com order.

    // Módulo da Cozinha (WebSocket)
    public static final String KITCHEN_ORDER_CREATED_QUEUE = "kitchen.order-created.queue";
    public static final String KITCHEN_ORDER_CREATED_ROUTING_KEY = "order.created.#";

    // Constantes para a Dead Letter Queue (DLQ)
    public static final String ORDER_EVENTS_DLX = "order.events.dlx";
    public static final String ORDER_CREATED_DLQ = "order.created.notification.dlq";
    public static final String ORDER_CREATED_DLQ_ROUTING_KEY = "order.created.dlq";

    @Bean
    public TopicExchange orderEventsExchange() {
        return new TopicExchange(
                ORDER_EVENTS_EXCHANGE,
                true,
                false
        );
    }

    // 1. Fila Principal configurada apontando para a Dead Letter Exchange
    @Bean
    public Queue orderCreatedQueue() {
        return QueueBuilder.durable(ORDER_CREATED_QUEUE)
                .deadLetterExchange(ORDER_EVENTS_DLX)
                .deadLetterRoutingKey(ORDER_CREATED_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding orderCreatedBinding(
            Queue orderCreatedQueue,
            TopicExchange orderEventsExchange
    ) {
        return BindingBuilder
                .bind(orderCreatedQueue)
                .to(orderEventsExchange)
                .with(ORDER_CREATED_ROUTING_KEY);
    }

    // --- Fila e Binding para Auditoria no Mongo ---
    @Bean
    public Queue orderAuditQueue() {
        return QueueBuilder.durable(ORDER_AUDIT_QUEUE)
                .deadLetterExchange(ORDER_EVENTS_DLX)
                .deadLetterRoutingKey(ORDER_CREATED_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding orderAuditBinding(
            Queue orderAuditQueue,
            TopicExchange orderEventsExchange
    ) {
        return BindingBuilder
                .bind(orderAuditQueue)
                .to(orderEventsExchange)
                .with(ORDER_AUDIT_ROUTING_KEY);
    }

    // --- NOVA CONFIGURAÇÃO: Fila e Binding para a Cozinha ---
    @Bean
    public Queue kitchenOrderCreatedQueue() {
        return QueueBuilder.durable(KITCHEN_ORDER_CREATED_QUEUE)
                .deadLetterExchange(ORDER_EVENTS_DLX)
                .deadLetterRoutingKey(ORDER_CREATED_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding kitchenOrderCreatedBinding(
            Queue kitchenOrderCreatedQueue,
            TopicExchange orderEventsExchange
    ) {
        return BindingBuilder
                .bind(kitchenOrderCreatedQueue)
                .to(orderEventsExchange)
                .with(KITCHEN_ORDER_CREATED_ROUTING_KEY);
    }
    // --------------------------------------------------------

    // 2. Fila de Dead Letter (DLQ)
    @Bean
    public Queue orderCreatedDlq() {
        return QueueBuilder.durable(ORDER_CREATED_DLQ).build();
    }

    // 3. Exchange de Dead Letter (DLX)
    @Bean
    public DirectExchange orderEventsDlx() {
        return new DirectExchange(ORDER_EVENTS_DLX, true, false);
    }

    // 4. Binding ligando a DLX à DLQ
    @Bean
    public Binding orderCreatedDlqBinding(
            Queue orderCreatedDlq,
            DirectExchange orderEventsDlx
    ) {
        return BindingBuilder
                .bind(orderCreatedDlq)
                .to(orderEventsDlx)
                .with(ORDER_CREATED_DLQ_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setObservationEnabled(false);
        return rabbitTemplate;
    }

    // 5. Configuração para encaminhar automaticamente mensagens com falha para a DLQ
    @Bean
    public RepublishMessageRecoverer republishMessageRecoverer(RabbitTemplate rabbitTemplate) {
        return new RepublishMessageRecoverer(rabbitTemplate, ORDER_EVENTS_DLX, ORDER_CREATED_DLQ_ROUTING_KEY);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setObservationEnabled(false);
        return factory;
    }
}