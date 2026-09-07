package com.estudos.orderplatform.observability;

import org.slf4j.MDC;

/**
 * Coloca trace/span/orderId no MDC para o LogstashEncoder e o log forwarding do New Relic.
 */
public final class RequestTraceMdc {

    private RequestTraceMdc() {
    }

    public static void put(String traceId, String spanId) {
        putValue("traceId", traceId);
        putValue("trace.id", traceId);
        putValue("spanId", spanId);
        putValue("span.id", spanId);
    }

    public static void putOrderId(Long orderId) {
        if (orderId != null) {
            MDC.put("orderId", String.valueOf(orderId));
        }
    }

    public static void clear() {
        MDC.remove("traceId");
        MDC.remove("trace.id");
        MDC.remove("spanId");
        MDC.remove("span.id");
        MDC.remove("orderId");
    }

    public static void putValue(String key, String value) {
        if (value != null && !value.isBlank()) {
            MDC.put(key, value);
        }
    }
}
