package com.estudos.orderplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequestDto(
    @NotNull( message = "O ID do produto é obrigatório")
    @Schema(description = "productId é único do produto", example = "PROD-1001")
    Long productId,

    @NotNull( message = "A quantidade é obrigatória")
    @Positive( message = "A quantidade deve ser maior que zero")
    @Schema(description = "quantity é a quantidade do produto", example = "01")    
    Integer quantity
){}

