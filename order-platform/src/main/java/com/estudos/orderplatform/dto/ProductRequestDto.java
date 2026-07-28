package com.estudos.orderplatform.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductRequestDto(
        @Schema(description = "SKU único do produto", example = "PROD-1001")
        @NotBlank(message = "O SKU é obrigatório")
        String sku,

        @Schema(description = "Nome comercial do produto", example = "Teclado Mecânico RGB")
        @NotBlank(message = "O nome é obrigatório")
        String name,

        @Schema(description = "Preço unitário do produto", example = "250.00")
        @NotNull(message = "O preço é obrigatório")
        @Positive(message = "O preço deve ser maior que zero")
        Double price
) {}