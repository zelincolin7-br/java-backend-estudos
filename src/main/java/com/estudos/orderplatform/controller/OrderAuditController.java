package com.estudos.orderplatform.controller;

import com.estudos.orderplatform.audit.document.OrderAuditLog;
import com.estudos.orderplatform.dto.OrderAuditRequestDTO;
import com.estudos.orderplatform.service.OrderAuditService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders/audit")
public class OrderAuditController {

    private final OrderAuditService auditService;

    public OrderAuditController(OrderAuditService auditService) {
        this.auditService = auditService;
    }

    // Endpoint para registrar um evento manualmente / testar a escrita no MongoDB
    @PostMapping
    public ResponseEntity<OrderAuditLog> createAuditLog(@RequestBody OrderAuditRequestDTO request) {
        OrderAuditLog savedLog = auditService.logOrderEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedLog);
    }

    // Endpoint para buscar o histórico de auditoria de um pedido
    @GetMapping("/{orderId}")
    public ResponseEntity<List<OrderAuditLog>> getAuditHistory(@PathVariable Long orderId) {
        List<OrderAuditLog> history = auditService.getAuditHistoryByOrderId(orderId);
        return ResponseEntity.ok(history);
    }

    public ResponseEntity<List<OrderAuditLog>> getActiveOrders() {
        List<OrderAuditLog> orders = auditService.findActiveOrders();
        return ResponseEntity.ok(orders);
    }
}