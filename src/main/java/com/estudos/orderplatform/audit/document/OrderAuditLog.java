package com.estudos.orderplatform.audit.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "order_audit_logs")
public class OrderAuditLog {

    @Id
    private String id;

    @Indexed
    @Field("order_id")
    private Long orderId;

    @Field("event_type")
    private String eventType;

    @Field("previous_status")
    private String previousStatus;

    @Field("new_status")
    private String newStatus;

    @Field("payload")
    private Map<String, Object> payload;

    @Builder.Default
    @Field("created_at")
    private Instant createdAt = Instant.now();
}