package com.estudos.orderplatform.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderCreatedEvent(
        Long orderId,
        Long customerId,
        BigDecimal totalValue,
        String status
    ) {}