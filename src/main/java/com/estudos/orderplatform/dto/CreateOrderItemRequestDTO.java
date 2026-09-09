package com.estudos.orderplatform.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateOrderItemRequestDTO(
    @NotNull(message = "O ID do produto é obrigatório")
    Long productId,

    @NotNull(message = "A quantidade é obrigatória")
    Integer quantity,

    @NotNull(message = "O preço é obrigatório")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero")
    BigDecimal price
) {}