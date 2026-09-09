package com.estudos.orderplatform.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequestDTO(
    @NotBlank(message = "O ID do cliente é obrigatório")
    String customerId,

    @NotEmpty(message = "O pedido deve conter ao menos um item")
    @Valid
    List<CreateOrderItemRequestDTO> items
) {}