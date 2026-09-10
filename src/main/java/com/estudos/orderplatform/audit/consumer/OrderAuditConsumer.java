package com.estudos.orderplatform.audit.consumer;

import com.estudos.orderplatform.audit.document.OrderAuditLog;
import com.estudos.orderplatform.config.RabbitMQConfig;
import com.estudos.orderplatform.dto.OrderAuditRequestDTO;
import com.estudos.orderplatform.service.OrderAuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderAuditConsumer {

    private final OrderAuditService auditService;

    @RabbitListener(queues = RabbitMQConfig.ORDER_AUDIT_QUEUE)
    public void consumeOrderAuditEvent(OrderAuditRequestDTO event) {
        log.info("Mensagem recebida do RabbitMQ para o Pedido ID: {} | Evento: {}", 
            event.orderId(), event.eventType());

        OrderAuditLog savedLog = auditService.logOrderEvent(event);

        log.info("Evento '{}' salvo com sucesso no MongoDB. ID gerado: {}", 
            event.eventType(), savedLog.getId());
    }
}