package com.estudos.orderplatform.observability;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;

import com.newrelic.api.agent.ConcurrentHashMapHeaders;
import com.newrelic.api.agent.HeaderType;
import com.newrelic.api.agent.Headers;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.TransportType;

/**
 * Propaga Distributed Tracing do New Relic pelos headers AMQP.
 */
public final class NewRelicRabbitTrace {

    private static final Logger log = LoggerFactory.getLogger(NewRelicRabbitTrace.class);

    private NewRelicRabbitTrace() {
    }

    /**
     * Produtor: injeta headers do trace HTTP ativo na mensagem.
     */
    public static Message injectTraceHeaders(Message message) {
        try {
            Headers distributedTraceHeaders = ConcurrentHashMapHeaders.build(HeaderType.MESSAGE);

            NewRelic.getAgent()
                    .getTransaction()
                    .insertDistributedTraceHeaders(distributedTraceHeaders);

            for (String headerName : distributedTraceHeaders.getHeaderNames()) {
                String headerValue = distributedTraceHeaders.getHeader(headerName);
                if (headerValue != null && !headerValue.isBlank()) {
                    message.getMessageProperties().setHeader(headerName, headerValue);
                }
            }

            log.debug(
                    "Headers de Distributed Tracing adicionados à mensagem RabbitMQ: {}",
                    distributedTraceHeaders.getHeaderNames());
        } catch (Exception exception) {
            log.warn("Não foi possível adicionar os headers do New Relic à mensagem RabbitMQ.", exception);
        }

        return message;
    }

    /**
     * Consumidor: informa ao New Relic que esta transação continua o trace recebido.
     */
    public static void acceptTraceHeaders(Message message) {
        if (message == null || message.getMessageProperties() == null) {
            log.warn("Mensagem RabbitMQ sem propriedades. Não foi possível recuperar o contexto de tracing.");
            return;
        }

        Map<String, Object> rabbitHeaders = message.getMessageProperties().getHeaders();
        if (rabbitHeaders == null || rabbitHeaders.isEmpty()) {
            log.warn("Mensagem RabbitMQ recebida sem headers. Um novo trace será criado no consumidor.");
            return;
        }

        Map<String, String> convertedHeaders = new LinkedHashMap<>();
        rabbitHeaders.forEach((headerName, headerValue) -> {
            String convertedValue = convertHeaderValue(headerValue);
            if (convertedValue != null && !convertedValue.isBlank()) {
                convertedHeaders.put(headerName, convertedValue);
            }
        });

        if (convertedHeaders.isEmpty()) {
            log.warn("Nenhum header RabbitMQ pôde ser convertido para o contexto do New Relic.");
            return;
        }

        try {
            Headers distributedTraceHeaders = ConcurrentHashMapHeaders.buildFromFlatMap(
                    HeaderType.MESSAGE,
                    convertedHeaders);

            NewRelic.getAgent()
                    .getTransaction()
                    .acceptDistributedTraceHeaders(TransportType.AMQP, distributedTraceHeaders);

            log.debug(
                    "Contexto de Distributed Tracing aceito no consumidor. Headers: {}",
                    distributedTraceHeaders.getHeaderNames());
        } catch (Exception exception) {
            log.warn(
                    "Não foi possível aceitar os headers de Distributed Tracing da mensagem RabbitMQ.",
                    exception);
        }
    }

    private static String convertHeaderValue(Object headerValue) {
        if (headerValue == null) {
            return null;
        }
        if (headerValue instanceof byte[] bytes) {
            return new String(bytes, StandardCharsets.UTF_8);
        }
        return String.valueOf(headerValue);
    }
}
