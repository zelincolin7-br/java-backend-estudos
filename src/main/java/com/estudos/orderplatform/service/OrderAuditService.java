package com.estudos.orderplatform.service;

import com.estudos.orderplatform.audit.document.OrderAuditLog;
import com.estudos.orderplatform.dto.OrderAuditRequestDTO;
import com.estudos.orderplatform.repository.mongo.OrderAuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderAuditService {

    private final OrderAuditLogRepository auditRepository;

    // Registra um novo evento de auditoria no Mongo
    public OrderAuditLog logOrderEvent(OrderAuditRequestDTO request) {
        OrderAuditLog log = OrderAuditLog.builder()
            .orderId(request.orderId())
            .eventType(request.eventType())
            .previousStatus(request.previousStatus())
            .newStatus(request.newStatus())
            .payload(request.payload())
            .createdAt(Instant.now())
            .build();

        return auditRepository.save(log);
    }

    // Consulta todo o histórico de auditoria de um pedido
    public List<OrderAuditLog> getAuditHistoryByOrderId(Long orderId) {
        return auditRepository.findByOrderIdOrderByCreatedAtDesc(orderId);
    }
}