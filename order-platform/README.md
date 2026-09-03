# 📦 Order Platform (`order-platform`)

Serviço do ecossistema de pedidos focado no processamento assíncrono de eventos de criação de pedidos, garantia de resiliência com políticas de retry/DLQ e rastreabilidade ponta a ponta com New Relic.

---

## 🛠️ Tecnologias e Ferramentas

- **Java 21** & **Spring Boot 3.4**
- **Spring AMQP (RabbitMQ)** — Mensageria e processamento de eventos
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
| **Exchange Principal** | `order.v1.events` | `DirectExchange` para eventos de pedidos |
| **Fila Principal** | `order.created.queue` | Armazena eventos de pedidos a serem processados |
| **Routing Key Principal** | `order.created` | Chave de roteamento para novos pedidos |
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

## 🧪 Estratégia de Testes e Integração

A aplicação conta com uma suíte de testes automatizados focada na validação do comportamento real da infraestrutura.

### Testes de Integração com Testcontainers (`OrderListenerDlqTest`)

Em vez de simular o broker via Mocks, os testes de integração sobem um container Docker real do RabbitMQ (`rabbitmq:3.12-management`) de forma efêmera durante a execução da suíte JUnit.

- **Isolamento de Ambiente**: Porta e host são sorteados dinamicamente via `@DynamicPropertySource`.
- **Validação de DLQ**: Garante que o fluxo de retries seja totalmente executado e que a mensagem chegue à DLQ sem alterar código de produção.
- **Validação Assíncrona**: Utilização da biblioteca **Awaitility** para aguardar a confirmação de roteamento na DLQ sem uso de pausas estáticas (`Thread.sleep`).

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
- **Java 21** instalado
- **Docker** em execução (necessário para os testes de integração com Testcontainers)

### Executar os Testes Automatizados
```bash
mvn clean test
