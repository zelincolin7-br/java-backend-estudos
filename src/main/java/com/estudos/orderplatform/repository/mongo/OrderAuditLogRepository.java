package com.estudos.orderplatform.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.estudos.orderplatform.audit.document.OrderAuditLog;
import com.estudos.orderplatform.domain.OrderStatus;

import java.util.List;

@Repository
public interface OrderAuditLogRepository extends MongoRepository<OrderAuditLog, String> {

    // Busca todo o histórico de eventos de um pedido ordenado por data
    List<OrderAuditLog> findByOrderIdOrderByCreatedAtDesc(Long orderId);

     // Busca apenas os pedidos que importam para o fluxo ativo da cozinha
     List<OrderAuditLog> findByStatusIn(List<OrderStatus> statuses);
}