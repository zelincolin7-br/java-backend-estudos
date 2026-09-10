package com.estudos.orderplatform.listener;

import com.estudos.orderplatform.config.RabbitMQConfig;
import com.estudos.orderplatform.dto.OrderAuditRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KitchenOrderListener {

    private final SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = RabbitMQConfig.KITCHEN_ORDER_CREATED_QUEUE)
    public void handleKitchenOrder(OrderAuditRequestDTO event) {
        log.info("🍳 [COZINHA] Novo pedido recebido da fila! Order ID: {}", event.orderId());

        // Notifica o painel web da cozinha em tempo real via WebSocket
        messagingTemplate.convertAndSend("/topic/kitchen-orders", event);

        log.info("📡 [WEBSOCKET] Pedido ID: {} enviado com sucesso para o painel da cozinha!", event.orderId());
    }
}