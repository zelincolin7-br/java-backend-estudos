package com.estudos.orderplatform.dto;

import java.util.Map;

public record OrderAuditRequestDTO(
    Long orderId,
    String eventType,
    String previousStatus,
    String newStatus,
    Map<String, Object> payload
) {}