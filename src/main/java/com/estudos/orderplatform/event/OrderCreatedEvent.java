package com.estudos.orderplatform.event;

import java.math.BigDecimal;

public record OrderCreatedEvent(
        Long orderId,
        Long customerId,
        BigDecimal totalValue,
        String status
    ) {}