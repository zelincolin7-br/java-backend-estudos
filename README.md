# 📦 Order Platform (`order-platform`)

Serviço do ecossistema de pedidos focado no processamento assíncrono de eventos de criação de pedidos, garantia de resiliência com políticas de retry/DLQ e rastreabilidade ponta a ponta com New Relic.

---

## 🛠️ Tecnologias e Ferramentas

- **Java 21** & **Spring Boot 3.4**
- **Spring AMQP (RabbitMQ)** — Mensageria e processamento de eventos
- **PostgreSQL** — Banco de dados relacional
- **Terraform** & **Docker** — Provisionamento da infraestrutura local como código (IaC)
- **Testcontainers** — Testes de integração com containers reais Docker
- **Awaitility** — Asserções para testes assíncronos
- **New Relic Agent & Logs** — Observabilidade, rastreamento distribuído e métricas de erro
- **SonarQube** — Análise estática de código e qualidade de software
- **Maven** — Gerenciador de dependências e build

---

## 🏗️ Arquitetura de Mensageria (RabbitMQ)

A aplicação utiliza o padrão de **Dead Letter Exchange (DLX)** para garantir que mensagens com falha no processamento não sejam perdidas e possam ser analisadas ou reprocessadas posteriormente.

### Topologia de Filas e Exchanges

| Recurso | Nome / Identificador | Tipo / Descrição |
| :--- | :--- | :--- |
| **Exchange Principal** | `order.events` | `TopicExchange` para eventos da plataforma |
| **Fila de Inventário** | `inventory.order-created.queue` | Armazena eventos de criação para o inventário |
| **Fila de Pagamento** | `payment.order-created.queue` | Armazena eventos de criação para pagamentos |
| **Fila de Notificação** | `notification.order-status.queue` | Armazena atualizações de status de pedidos |
| **Exchange de DLQ (DLX)** | `order.v1.events.dlx` | `DirectExchange` para direcionamento de erros |
| **Fila de DLQ** | `order.created.dlq` | Armazena mensagens após esgotar tentativas |

---

## 🔄 Política de Resiliência e Fluxo de Erros

1. **Tentativa de Consumo**: O `@RabbitListener` intercepta o evento `OrderCreatedEvent`.
2. **Retries em Memória**: Em caso de exceção no listener ou falha na desserialização do JSON:
   - Tentativa original + 2 retries (total de 3 tentativas).
   - Backoff exponencial: 1s na 1ª tentativa, dobrando para 2s na 2ª tentativa (máximo de 10s).
3. **Esgotamento de Tentativas**: Se todas as tentativas falharem:
   - O Spring envia um `NACK` com `requeue=false`.
   - O broker RabbitMQ transfere a mensagem automaticamente para a DLX (`order.v1.events.dlx`).
   - A mensagem é armazenada na fila de dead-letter (`order.created.dlq`).

---

## 🚀 Como Executar o Projeto

### 📋 Pré-requisitos
- **Java 21** (JDK 21 ou superior)
- **Maven 3.8+**
- **Docker** e **Docker Compose** (ou Docker Engine em execução)
- **Terraform 1.5+** (para provisionar os containers e recursos locais)

---

### 🐳 1. Subindo a Infraestrutura Local (Terraform + Docker)

A infraestrutura necessária para o funcionamento local (PostgreSQL, RabbitMQ com Management e SonarQube) é provisionada e gerenciada via Terraform.

1. Acesse o diretório do Terraform na raiz do projeto:
   ```bash
   cd terraform