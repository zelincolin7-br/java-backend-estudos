package com.estudos.orderplatform.dto;

import com.estudos.orderplatform.domain.Order;
import com.estudos.orderplatform.domain.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponseDTO(
    Long id,
    OrderStatus status,
    BigDecimal total,
    Instant createdAt,
    List<OrderItemResponseDTO> items
) {
    public static OrderResponseDTO fromEntity(Order order) {
        List<OrderItemResponseDTO> itemDTOs = order.getItems().stream()
            .map(OrderItemResponseDTO::fromEntity)
            .toList();

        return new OrderResponseDTO(
            order.getId(),
            order.getStatus(),
            order.getTotal(),
            order.getCreatedAt(),
            itemDTOs
        );
    }
}