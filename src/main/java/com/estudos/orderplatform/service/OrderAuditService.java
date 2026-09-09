package com.estudos.orderplatform.service;


import com.estudos.orderplatform.audit.document.OrderAuditLog;
import com.estudos.orderplatform.dto.OrderAuditRequestDTO;
import com.estudos.orderplatform.repository.mongo.OrderAuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderAuditService {

    private final OrderAuditLogRepository auditRepository;

    public OrderAuditService(OrderAuditLogRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    // Registra um novo evento de auditoria no Mongo
    public OrderAuditLog logOrderEvent(OrderAuditRequestDTO request) {
        OrderAuditLog log = new OrderAuditLog(
            request.orderId(),
            request.eventType(),
            request.previousStatus(),
            request.newStatus(),
            request.payload()
        );
        return auditRepository.save(log);
    }

    // Consulta todo o histórico de auditoria de um pedido
    public List<OrderAuditLog> getAuditHistoryByOrderId(Long orderId) {
        return auditRepository.findByOrderIdOrderByCreatedAtDesc(orderId);
    }
}