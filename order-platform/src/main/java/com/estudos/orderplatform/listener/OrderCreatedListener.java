package com.estudos.orderplatform.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.estudos.orderplatform.config.RabbitMQConfig;
import com.estudos.orderplatform.event.OrderCreatedEvent;
import com.estudos.orderplatform.observability.NewRelicRabbitTrace;
import com.estudos.orderplatform.observability.RequestTraceMdc;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.TraceMetadata;

@Component
public class OrderCreatedListener {

    private static final Logger log = LoggerFactory.getLogger(OrderCreatedListener.class);

    @RabbitListener(queues = RabbitMQConfig.ORDER_CREATED_QUEUE)
    @Trace(dispatcher = true)
    public void onOrderCreated(OrderCreatedEvent event, Message rawMessage) {
        NewRelicRabbitTrace.acceptTraceHeaders(rawMessage);

        TraceMetadata traceMetadata = NewRelic.getAgent().getTraceMetadata();
        String traceId = traceMetadata.getTraceId();
        String spanId = traceMetadata.getSpanId();

        try {
            RequestTraceMdc.put(traceId, spanId);
            RequestTraceMdc.putOrderId(event.orderId());

            if (traceId != null && !traceId.isBlank()) {
                NewRelic.addCustomParameter("rabbit.consumer.traceId", traceId);
            }
            if (spanId != null && !spanId.isBlank()) {
                NewRelic.addCustomParameter("rabbit.consumer.spanId", spanId);
            }
            if (event.orderId() != null) {
                NewRelic.addCustomParameter("order.id", event.orderId());
            }

            log.info(
                    "📩 [MENSAGEM RECEBIDA DA FILA] Processando evento de pedido criado. orderId={}, traceId={}, spanId={}",
                    event.orderId(),
                    traceId,
                    spanId);

            log.info("Evento de pedido processado com sucesso. orderId={}", event.orderId());
        } catch (RuntimeException exception) {
            log.error("Erro ao processar evento de pedido criado. orderId={}", event.orderId(), exception);
            NewRelic.noticeError(exception);
            throw exception;
        } finally {
            RequestTraceMdc.clear();
        }
    }
}
