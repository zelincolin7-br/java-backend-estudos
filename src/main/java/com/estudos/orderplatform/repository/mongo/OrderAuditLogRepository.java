package com.estudos.orderplatform.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.estudos.orderplatform.audit.document.OrderAuditLog;

import java.util.List;

@Repository
public interface OrderAuditLogRepository extends MongoRepository<OrderAuditLog, String> {

    // Busca todo o histórico de eventos de um pedido ordenado por data
    List<OrderAuditLog> findByOrderIdOrderByCreatedAtDesc(Long orderId);
}