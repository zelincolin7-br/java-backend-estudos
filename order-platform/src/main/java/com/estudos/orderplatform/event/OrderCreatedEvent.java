package com.estudos.orderplatform.event;

import java.time.LocalDateTime;

public record OrderCreatedEvent(
        Long orderId,
        Double total,
        LocalDateTime createdAt
) {
}
