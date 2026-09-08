# rabbitmq.tf

# 1. Exchange Principal de Pedidos (Topic)
resource "rabbitmq_exchange" "order_events_exchange" {
  name  = "order.events"
  vhost = "/"
  settings {
    type    = "topic"
    durable = true
  }

  depends_on = [docker_container.rabbitmq]
}

# 2. Filas dos Microsserviços Consumidores

# Fila para o Inventory Service (atualização de insumos)
resource "rabbitmq_queue" "inventory_order_created_queue" {
  name  = "inventory.order-created.queue"
  vhost = "/"
  settings {
    durable = true
  }

  depends_on = [docker_container.rabbitmq]
}

# Fila para o Payment Service (processar cobrança Pix/Cartão)
resource "rabbitmq_queue" "payment_order_created_queue" {
  name  = "payment.order-created.queue"
  vhost = "/"
  settings {
    durable = true
  }

  depends_on = [docker_container.rabbitmq]
}

# Fila para o Notification Service (atualizar telas via WebSocket)
resource "rabbitmq_queue" "notification_order_status_queue" {
  name  = "notification.order-status.queue"
  vhost = "/"
  settings {
    durable = true
  }

  depends_on = [docker_container.rabbitmq]
}

# 3. Bindings (Roteamento de Eventos da Exchange para as Filas)

# Conecta eventos de criação de pedido à fila do estoque
resource "rabbitmq_binding" "bind_inventory" {
  source           = rabbitmq_exchange.order_events_exchange.name
  vhost            = "/"
  destination      = rabbitmq_queue.inventory_order_created_queue.name
  destination_type = "queue"
  routing_key      = "order.created"
}

# Conecta eventos de criação de pedido à fila de pagamentos
resource "rabbitmq_binding" "bind_payment" {
  source           = rabbitmq_exchange.order_events_exchange.name
  vhost            = "/"
  destination      = rabbitmq_queue.payment_order_created_queue.name
  destination_type = "queue"
  routing_key      = "order.created"
}

# Conecta todos os eventos de status da ordem à fila de notificações
resource "rabbitmq_binding" "bind_notification" {
  source           = rabbitmq_exchange.order_events_exchange.name
  vhost            = "/"
  destination      = rabbitmq_queue.notification_order_status_queue.name
  destination_type = "queue"
  routing_key      = "order.#"
}