package com.estudos.orderplatform.service;

import com.estudos.orderplatform.audit.document.OrderAuditLog;
import com.estudos.orderplatform.domain.Order;
import com.estudos.orderplatform.domain.OrderStatus;
import com.estudos.orderplatform.dto.OrderAuditRequestDTO;
import com.estudos.orderplatform.dto.OrderResponseDTO;
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

    // Consulta todos os pedidos ativos
    public List<OrderAuditLog> findActiveOrders() {
        List<OrderStatus> activeStatuses = List.of(
            OrderStatus.PAID,
            OrderStatus.PREPARING,
            OrderStatus.READY
        );

        List<OrderAuditLog> orders = auditRepository.findByStatusIn(activeStatuses);
        return orders;
    }
}