package com.estudos.orderplatform.dto;

import java.time.Instant;
import java.util.List;

import com.estudos.orderplatform.domain.Order;
import com.estudos.orderplatform.domain.OrderStatus;
public record OrderResponseDto(
    Long id,
    Instant createAt,
    OrderStatus status,
    Double total,
    List<OrderItemResponseDto> items
) {
    public OrderResponseDto(Order order) {
        this(
            order.getId(),
            order.getCreatedAt(),
            order.getStatus(),
            order.getTotal(),
            order.getItems().stream().map(OrderItemResponseDto::new).toList()
        );
    }
}
