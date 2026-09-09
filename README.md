# 📦 Order Platform (`order-platform`)

Serviço do ecossistema de pedidos focado em arquitetura poliglota (PostgreSQL + MongoDB), processamento assíncrono de eventos de criação/auditoria de pedidos, garantia de resiliência com políticas de retry/DLQ e rastreabilidade ponta a ponta com New Relic.

---

## 🛠️ Tecnologias e Ferramentas

- **Java 21** & **Spring Boot 3.4**
- **Spring AMQP (RabbitMQ)** — Mensageria e processamento de eventos
- **Spring Data MongoDB** — Persistence de logs e auditoria no MongoDB
- **PostgreSQL** — Banco de dados relacional (dados transacionais)
- **MongoDB 7.0** — Banco NoSQL (logs de auditoria e histórico de eventos)
- **Terraform** & **Docker** — Provisionamento da infraestrutura local como código (IaC)
- **Testcontainers** — Testes de integração com containers reais Docker
- **Awaitility** — Asserções para testes assíncronos
- **New Relic Agent & Logs** — Observabilidade, rastreamento distribuído e métricas de erro
- **SonarQube** — Análise estática de código e qualidade de software
- **Maven** — Gerenciador de dependências e build

---

## 🏗️ Arquitetura de Mensageria (RabbitMQ)

A aplicação utiliza o padrão de **Dead Letter Exchange (DLX)** para garantir resiliência no processamento e uma fila dedicada de **Auditoria** para persistir de forma assíncrona o histórico dos pedidos no MongoDB.

### Topologia de Filas e Exchanges

| Recurso | Nome / Identificador | Tipo / Descrição |
| :--- | :--- | :--- |
| **Exchange Principal** | `order.events` | `TopicExchange` para eventos da plataforma |
| **Fila de Auditoria** | `order.audit.queue` | Armazena eventos e logs de histórico para o **MongoDB** (`order.#`) |
| **Fila de Notificação** | `order.created.notification.queue` | Armazena eventos de criação para notificações (`order.created`) |
| **Fila de Inventário** | `inventory.order-created.queue` | Armazena eventos de criação para o inventário |
| **Fila de Pagamento** | `payment.order-created.queue` | Armazena eventos de criação para pagamentos |
| **Exchange de DLQ (DLX)** | `order.events.dlx` | `DirectExchange` para direcionamento de erros |
| **Fila de DLQ** | `order.created.notification.dlq` | Armazena mensagens da notificação após esgotar tentativas |

---

## 🔄 Política de Resiliência e Fluxo de Erros

1. **Tentativa de Consumo**: O `@RabbitListener` intercepta o evento na fila (`order.audit.queue` ou `order.created.notification.queue`).
2. **Retries em Memória**: Em caso de exceção no listener ou falha na desserialização do JSON:
   - Tentativa original + 2 retries (total de 3 tentativas).
   - Backoff exponencial: 1s na 1ª tentativa, dobrando para 2s na 2ª tentativa (máximo de 10s).
3. **Esgotamento de Tentativas**: Se todas as tentativas falharem:
   - O Spring envia um `NACK` com `requeue=false`.
   - O broker RabbitMQ transfere a mensagem automaticamente para a DLX (`order.events.dlx`).
   - A mensagem é armazenada na fila de dead-letter (`order.created.notification.dlq`).

---

## 🧪 Como Testar a Mensageria e Auditoria (RabbitMQ ➔ MongoDB)

Para simular e validar o fluxo assíncrono de eventos salvando no MongoDB local via interface web do RabbitMQ:

### 1. Publicar Mensagem no RabbitMQ Management

1. Acesse o painel do RabbitMQ em **`http://localhost:15672`** (Usuário/Senha: `guest`/`guest`).
2. Vá até a aba **Exchanges** e selecione a exchange **`order.events`**.
3. Expanda o painel **Publish message** e preencha com as configurações:
   - **Routing key:** `order.created`
   - **Headers:**
     - **Name:** `__TypeId__`
     - **Value:** `com.estudos.orderplatform.dto.OrderAuditRequestDTO` *(Informa ao conversor Jackson do Spring AMQP qual classe Java instanciar no consumo)*
   - **Properties:**
     - **Name:** `content_type`
     - **Value:** `application/json`
   - **Payload:**
     ```json
     {
       "orderId": 2002,
       "eventType": "ORDER_CREATED",
       "previousStatus": "NONE",
       "newStatus": "PENDING",
       "payload": {
         "channel": "MOBILE_APP",
         "totalAmount": 480.90,
         "itemsCount": 3
       }
     }
     ```
4. Clique em **Publish message**.

### 2. Validar a Persistência no MongoDB

1. Abra o **MongoDB Compass** e conecte-se via URI:
   ```text
   mongodb://admin:adminpass@localhost:27017/order_platform?authSource=admin