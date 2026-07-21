package com.estudos.orderplatform.dto;

import com.estudos.orderplatform.domain.OrderItem;

public record OrderItemResponseDto(
    Long id,
    Long productId,
    String productName,
    Double price,
    Integer quantity,
    Double subTotal
) {
    public OrderItemResponseDto(OrderItem item) {
        this(
            item.getId(),
            item.getProduct().getId(),
            item.getProduct().getName(),
            item.getPrice(),
            item.getQuantity(),
            item.getSubTotal()
        );
    }
}