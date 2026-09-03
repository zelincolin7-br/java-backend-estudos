package com.estudos.orderplatform.listener;

import com.estudos.orderplatform.config.RabbitMQConfig;
import com.estudos.orderplatform.event.OrderCreatedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Testcontainers
@Import({RabbitMQConfig.class, OrderListenerDlqTest.TestRabbitConfig.class})
class OrderListenerDlqTest {

    static {
        System.setProperty("api.version", "1.40");
    }

    @Container
    static RabbitMQContainer rabbit = new RabbitMQContainer("rabbitmq:3.12-management");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbit::getHost);
        registry.add("spring.rabbitmq.port", rabbit::getAmqpPort);
        registry.add("spring.rabbitmq.listener.simple.default-requeue-rejected", () -> "false");
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @TestConfiguration
    static class TestRabbitConfig {

        // 1. DLQ Infrastructure
        @Bean
        public Queue dlqQueue() {
            return QueueBuilder.durable("order.created.dlq").build();
        }

        @Bean
        public DirectExchange dlqExchange() {
            return new DirectExchange("order.v1.events.dlx");
        }

        @Bean
        public Binding dlqBinding(Queue dlqQueue, DirectExchange dlqExchange) {
            return BindingBuilder.bind(dlqQueue).to(dlqExchange).with("order.created.dlq");
        }

        // 2. Main Infrastructure (Garante que a fila principal saiba pra onde mandar o erro)
        @Bean
        public DirectExchange mainExchange() {
            return new DirectExchange("order.v1.events");
        }

        @Bean
        public Queue mainQueue() {
            return QueueBuilder.durable("order.created.queue")
                    .deadLetterExchange("order.v1.events.dlx")
                    .deadLetterRoutingKey("order.created.dlq")
                    .build();
        }

        @Bean
        public Binding mainBinding(Queue mainQueue, DirectExchange mainExchange) {
            return BindingBuilder.bind(mainQueue).to(mainExchange).with("order.created");
        }

        // 3. Força a criação das filas no RabbitMQ assim que o contexto subir
        @Bean
        public RabbitAdmin rabbitAdmin(org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory) {
            RabbitAdmin admin = new RabbitAdmin(connectionFactory);
            admin.setAutoStartup(true);
            return admin;
        }
    }

    @Test
    void deveEnviarMensagemParaDlqQuandoOcorrerErroDeDesserializacao() {
        String payloadInvalidoJson = "{\"orderId\": \"TEXTO_INVALIDO\"}";
    
        System.out.println("🚀 Enviando payload inválido para a exchange...");
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EVENTS_EXCHANGE,
                RabbitMQConfig.ORDER_CREATED_ROUTING_KEY,
                payloadInvalidoJson
        );
    
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() -> {
            // Consome a mensagem diretamente da DLQ no teste
            Object mensagemDlq = rabbitTemplate.receiveAndConvert(RabbitMQConfig.ORDER_CREATED_DLQ);
    
            System.out.println("📩 Mensagem recuperada da DLQ: " + mensagemDlq);
    
            // Valida que o conteúdo não é nulo
            assertNotNull(mensagemDlq, "A mensagem deveria estar presente na DLQ!");
        });
    }
}