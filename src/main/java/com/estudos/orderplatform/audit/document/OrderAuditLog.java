package com.estudos.orderplatform.audit.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "order_audit_logs")
public class OrderAuditLog {

    @Id
    private String id;

    @Indexed
    @Field("order_id")
    private Long orderId;

    @Field("event_type")
    private String eventType; // Ex: ORDER_CREATED, PAYMENT_APPROVED, STATUS_CHANGED

    @Field("previous_status")
    private String previousStatus;

    @Field("new_status")
    private String newStatus;

    @Field("payload")
    private Map<String, Object> payload; // Guarda qualquer dado extra flexível do evento

    @Field("created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public OrderAuditLog() {}

    public OrderAuditLog(Long orderId, String eventType, String previousStatus, String newStatus, Map<String, Object> payload) {
        this.orderId = orderId;
        this.eventType = eventType;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.payload = payload;
        this.createdAt = LocalDateTime.now();
    }

    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(String previousStatus) { this.previousStatus = previousStatus; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

    public Map<String, Object> getPayload() { return payload; }
    public void setPayload(Map<String, Object> payload) { this.payload = payload; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}