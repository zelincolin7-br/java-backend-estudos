package com.estudos.orderplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequestDto(
        @NotBlank(message = "O identificador do produto é obrigatório")
        @Schema(
                description = "ID numérico (ex: 1) ou SKU (ex: PROD-1001) do produto já cadastrado",
                example = "1"
        )
        String productId,

        @NotNull(message = "A quantidade é obrigatória")
        @Positive(message = "A quantidade deve ser maior que zero")
        @Schema(description = "Quantidade desejada neste pedido (não é estoque do produto)", example = "2")
        Integer quantity
) {
    public OrderItemRequestDto(Long productId, Integer quantity) {
        this(productId == null ? null : String.valueOf(productId), quantity);
    }
}
