package com.estudos.orderplatform.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record OrderRequestDto(
    @NotEmpty( message = "O pedido deve conter pelo menos um Item")
    @Valid
    List<OrderItemRequestDto> items
){}