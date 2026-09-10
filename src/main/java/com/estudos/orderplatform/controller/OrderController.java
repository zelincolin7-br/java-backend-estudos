package com.estudos.orderplatform.controller;

import com.estudos.orderplatform.dto.CreateOrderRequestDTO;
import com.estudos.orderplatform.dto.OrderResponseDTO;
import com.estudos.orderplatform.dto.UpdateOrderStatusDTO;
import com.estudos.orderplatform.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder(@Valid @RequestBody CreateOrderRequestDTO dto) {
        log.info("Recebida requisição POST /v1/orders para o cliente: {}", dto.customerId());
        OrderResponseDTO response = orderService.createOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<OrderResponseDTO> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusDTO dto) {
        log.info("Recebida requisição PATCH /v1/orders/{}/status para: {}", id, dto.status());
        OrderResponseDTO response = orderService.updateOrderStatus(id, dto.status());
        return ResponseEntity.ok(response);
    }
}