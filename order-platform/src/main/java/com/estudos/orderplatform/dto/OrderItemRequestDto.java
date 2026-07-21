package com.estudos.orderplatform.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequestDto(
    @NotNull( message = "O ID do produto é obrigatório")
    Long productId,

    @NotNull( message = "A quantidade é obrigatória")
    @Positive( message = "A quantidade deve ser maior que zero")
    Integer quantity
){}

