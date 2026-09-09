package com.estudos.orderplatform.dto;

import com.estudos.orderplatform.domain.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponseDTO(
    Long id,
    Long productId,
    String productName,
    Integer quantity,
    BigDecimal price,
    BigDecimal subTotal
) {
    public static OrderItemResponseDTO fromEntity(OrderItem item) {
        return new OrderItemResponseDTO(
            item.getId(),
            item.getProduct().getId(),
            item.getProduct().getName(),
            item.getQuantity(),
            item.getPrice(),
            item.getSubTotal()
        );
    }
}