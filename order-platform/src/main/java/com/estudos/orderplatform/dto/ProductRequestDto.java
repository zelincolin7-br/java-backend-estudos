package com.estudos.orderplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProductRequestDto (
    @NotBlank(message = " O SKU é obrigatório")
    String sku,

    @NotBlank(message = "O nome é obrigatório")
    String name,

    @NotNull( message = " O preço é obrigatório")
    @Positive(message = "O preço deve ser maior que zero")
    Double price
){}