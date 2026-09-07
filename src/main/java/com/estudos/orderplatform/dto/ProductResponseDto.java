package com.estudos.orderplatform.dto;

import com.estudos.orderplatform.domain.Product;

public record ProductResponseDto(
    Long id,
    String sku,
    String name,
    Double price
) {
    public ProductResponseDto(Product product) {
        this(
            product.getId(),
            product.getSku(),
            product.getName(),
            product.getPrice()
        );
    }
}