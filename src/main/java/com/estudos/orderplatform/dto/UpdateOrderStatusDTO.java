package com.estudos.orderplatform.dto;

import com.estudos.orderplatform.domain.OrderStatus; // ajuste o pacote da sua Enum se necessário
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusDTO(
    @NotNull(message = "O status não pode ser nulo")
    OrderStatus status
) {}