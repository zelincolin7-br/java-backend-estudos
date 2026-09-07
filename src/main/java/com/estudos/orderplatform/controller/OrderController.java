package com.estudos.orderplatform.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.estudos.orderplatform.service.OrderService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestParam Long customerId, @RequestParam BigDecimal amount) {
        orderService.createOrder(customerId, amount);
        return ResponseEntity.ok("Pedido processado com sucesso!");
    }
}