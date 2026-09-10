package com.estudos.orderplatform.event;

import com.estudos.orderplatform.domain.OrderStatus;
import java.time.LocalDateTime;

public record OrderStatusUpdatedEvent(
    Long orderId,
    OrderStatus previousStatus,
    OrderStatus newStatus,
    LocalDateTime updatedAt
) {}