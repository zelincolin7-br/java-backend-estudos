# Plano de Estudos — Java Backend Pleno

> **Objetivo:** Dominar o ecossistema Java moderno para vagas de backend tradicional (pleno).
> **Projeto âncora:** `order-platform` (Spring Boot 3 + Maven + Java 17)
> **Ritmo:** 6 horas/dia · 6 dias/semana · 12 semanas (~432h)
> **Workspace:** `c:\Users\linco\www\estudos\order-platform`

---

## Metodologia de estudo

O plano é **híbrido** — não é 100% em cima do projeto.

| Modo | Quando usar | Proporção |
|------|-------------|-----------|
| **Laboratório** | Java puro (lambda, Stream, Optional, record) | Semana 1 (~50%) |
| **Projeto âncora** | Spring, API, testes, mensageria, DevOps | Semana 2+ (~80%) |
| **Revisão** | Consolidar + simular entrevista | Todo dia (30 min) |

### Fluxo do Java moderno

1. **Semana 1** — exercícios isolados em `java-labs` (sem Spring)
2. **Semana 2** — aplicar no `order-platform` (DTOs como `record`, Stream nos services)
3. **Semana 3+** — quase tudo no projeto; lab só quando travar em algo específico

---

## Rotina diária (6 horas)

| Bloco | Duração | Atividade |
|-------|---------|-----------|
| Teoria | 1h | Documentação oficial + 1 artigo/vídeo |
| Prática | 4h | Lab ou projeto `order-platform` |
| Revisão | 30 min | Anotações do dia |
| Entrevista | 30 min | Flashcards + 2 perguntas simuladas |

### Sugestão de blocos

- **Bloco 1 (2h):** Teoria + exercícios guiados
- *Pausa 15 min*
- **Bloco 2 (2h):** Projeto âncora
- *Pausa 15 min*
- **Bloco 3 (1h30):** Continuação do projeto ou lab
- **Bloco 4 (30 min):** Revisão + perguntas de entrevista

**1 dia de descanso por semana** (revisão leve, sem código pesado).

---

## Stack tecnológica

- [ ] Java 17 (ou 21)
- [ ] Spring Boot 3.x
- [ ] Maven
- [ ] PostgreSQL
- [ ] Redis
- [ ] RabbitMQ
- [ ] Kafka
- [ ] Docker + Docker Compose
- [ ] Prometheus + Grafana
- [ ] ELK/OpenSearch + Kibana
- [ ] JUnit 5 + Mockito + Testcontainers
- [ ] GitHub Actions (CI/CD)
- [ ] WebFlux (essencial para entrevista — semana 12)

---

## Projeto âncora — Order Platform

Sistema de pedidos que integra todas as tecnologias do plano.

### Funcionalidades finais

- [ ] API REST para criar/consultar pedidos e produtos
- [ ] PostgreSQL como banco principal
- [ ] Redis para cache e idempotência
- [ ] RabbitMQ para processamento assíncrono (e-mail, estoque)
- [ ] Kafka para eventos de domínio (`order.created`, `payment.approved`)
- [ ] Micrometer + Prometheus + Grafana (métricas)
- [ ] Logs estruturados + Kibana
- [ ] Docker Compose sobe todo o ecossistema
- [ ] Pipeline CI/CD (build, test, imagem Docker)
- [ ] Testes unitários + integração (Testcontainers)

### Estrutura de pacotes (alvo)

```
order-platform/
├── domain/          # entidades JPA
├── dto/             # records (request/response)
├── service/         # regras de negócio
├── controller/      # REST
├── repository/      # Spring Data
├── messaging/       # Rabbit + Kafka
├── config/          # Redis, segurança, etc.
└── docker/          # Compose
```

---

## Cronograma — 12 semanas

| Semana | Foco | Entregável |
|--------|------|------------|
| 1 | Java moderno: lambda, Stream, Optional, record | Lab completo + DTOs no projeto |
| 2 | Spring Boot: camadas, JPA, validação, exceções | CRUD de Product |
| 3 | API REST: paginação, filtros, OpenAPI | CRUD de Order + Swagger |
| 4 | Regras de negócio, transações, profiles | Fluxo criar pedido |
| 5 | Testes: JUnit 5, Mockito, MockMvc, Testcontainers | Suite nos serviços críticos |
| 6 | Docker + Compose + Redis | `docker compose up` + cache |
| 7 | RabbitMQ: filas, retry, DLQ | Notificação assíncrona |
| 8 | RabbitMQ avançado + idempotência (Redis) | Consumidor resiliente |
| 9 | Kafka: produtor/consumidor, partições | Tópico `order.events` |
| 10 | Kafka + integração entre serviços | Fluxo evento ponta a ponta |
| 11 | Observabilidade: logs, Prometheus, Grafana, Kibana | Dashboard + correlationId |
| 12 | CI/CD + WebFlux essencial + revisão | Pipeline verde + simulado |

---

## Marcos do plano

| Marco | Semana |
|-------|--------|
| API REST completa com JPA | 4 |
| Testes + Docker rodando | 6 |
| Mensageria (Rabbit + Kafka) | 10 |
| Projeto pronto para portfólio | 12 |

---

## Semana 1 — Java moderno (detalhado)

### Dia 1 — Lambda e interfaces funcionais

- [ ] Estudar: `Function`, `Predicate`, `Consumer`, `Supplier`
- [ ] Lab: filtrar/transformar lista de pedidos com lambda
- [ ] Revisão: *"Qual a diferença entre lambda e classe anônima?"*

### Dia 2 — Stream API

- [ ] Estudar: `map`, `filter`, `reduce`, `collect`, `flatMap`
- [ ] Lab: calcular total de pedidos, agrupar por status
- [ ] Revisão: *"Quando Stream é pior que um for?"*

### Dia 3 — Optional

- [ ] Estudar: `of`, `ofNullable`, `orElse`, `orElseGet`, `map`, `flatMap`
- [ ] Lab: buscar produto sem usar `null`
- [ ] Revisão: *"Por que Optional não deve ser campo de entidade JPA?"*

### Dia 4 — Record e imutabilidade

- [ ] Estudar: `record`, compact constructor, validação
- [ ] Lab: criar `ProductDto`, `OrderItemDto` como records
- [ ] Revisão: *"Record vs Lombok @Value?"*

### Dia 5 — Java 17 extras

- [ ] Estudar: `var`, text blocks, pattern matching (`instanceof`)
- [ ] Lab: refatorar código dos dias anteriores
- [ ] Revisão: consolidar anotações da semana

### Dia 6 — Integração no projeto

- [ ] Criar pacotes `domain` e `dto` no `order-platform`
- [ ] Models com `record` para DTOs
- [ ] Service usando Stream + Optional
- [ ] Rodar `mvn test`

### Dia 7 — Descanso ativo

- [ ] Revisar anotações da semana
- [ ] Simular 10 perguntas de entrevista sobre Java 8/17

---

## Semana 2 — Spring Boot base

- [ ] Entender camadas: Controller → Service → Repository
- [ ] Criar entidade `Product` (JPA)
- [ ] CRUD REST completo
- [ ] Validação com `jakarta.validation`
- [ ] Exception Handler global (`@ControllerAdvice`)
- [ ] Configurar PostgreSQL local ou Docker
- [ ] Entregável: `GET/POST/PUT/DELETE /api/products`

---

## Semana 3 — API REST completa

- [ ] Entidade `Order` e `OrderItem`
- [ ] Paginação e ordenação (`Pageable`)
- [ ] Filtros de busca
- [ ] OpenAPI/Swagger UI
- [ ] Padronizar respostas de erro
- [ ] Entregável: API documentada no Swagger

---

## Semana 4 — Regras de negócio

- [ ] Fluxo: criar pedido com itens
- [ ] Validação de estoque (regra simples)
- [ ] Transações (`@Transactional`)
- [ ] Profiles Spring (`dev`, `test`, `prod`)
- [ ] Entregável: `POST /api/orders` funcional ponta a ponta

---

## Semana 5 — Testes

- [ ] Testes unitários: JUnit 5 + Mockito + AssertJ
- [ ] Testes de controller: MockMvc
- [ ] Testes de integração: `@SpringBootTest`
- [ ] Testcontainers: Postgres em container
- [ ] Meta: ~70% cobertura nos services críticos
- [ ] Pipeline local: `mvn verify` verde

---

## Semana 6 — Docker + Redis

- [ ] Dockerfile multi-stage para Java
- [ ] Docker Compose: app + PostgreSQL
- [ ] Redis: cache-aside para catálogo de produtos
- [ ] TTL e invalidação de cache
- [ ] Entregável: `docker compose up` sobe tudo

---

## Semanas 7–8 — RabbitMQ

### Semana 7

- [ ] Conceitos: exchange, queue, routing key, ACK
- [ ] Fila `order.notifications`
- [ ] Produtor após criar pedido
- [ ] Consumidor de notificação

### Semana 8

- [ ] Retry e Dead Letter Queue (DLQ)
- [ ] Idempotência no consumidor (Redis)
- [ ] Testes com RabbitMQ em container
- [ ] Entregável: fluxo assíncrono resiliente

### RabbitMQ vs Kafka (referência)

| Tema | RabbitMQ | Kafka |
|------|----------|-------|
| Modelo | Fila/tópico, entrega ao consumidor | Log distribuído, replay |
| Uso típico | Tarefas assíncronas, filas de trabalho | Event streaming, integração |
| Garantias | ACK, retry, DLQ | Partições, offset, idempotência |
| Erro comum | Perder mensagem sem DLQ | Consumidor lento sem estratégia |

---

## Semanas 9–10 — Kafka

### Semana 9

- [ ] Conceitos: tópico, partição, offset, consumer group
- [ ] Produtor: publicar `order.created`
- [ ] Consumidor: processar eventos

### Semana 10

- [ ] Múltiplos consumidores no mesmo grupo
- [ ] Integração ponta a ponta com a API
- [ ] Idempotência com chave no Redis
- [ ] Entregável: fluxo evento completo

---

## Semana 11 — Observabilidade

- [ ] Logs estruturados (JSON) com `orderId` e `traceId`
- [ ] Micrometer: counters, timers, histogramas
- [ ] Prometheus: scrape de métricas
- [ ] Grafana: dashboard (latência p95, throughput, erros)
- [ ] Kibana/ELK: busca e correlação de logs
- [ ] Alerta simples: taxa de erro > 5% por 5 min
- [ ] Entregável: dashboard operacional

---

## Semana 12 — CI/CD + Reativo + Revisão

- [ ] GitHub Actions: build → test → Docker image
- [ ] WebFlux: 1 endpoint reativo (consulta de pedidos)
- [ ] Entender quando usar reativo vs bloqueante
- [ ] Revisão geral do projeto
- [ ] Simulado de entrevista (30+ perguntas)
- [ ] Entregável: pipeline verde + projeto no GitHub

---

## Checklist de entrevista

Ao final do plano, devo conseguir explicar com exemplo do meu projeto:

- [ ] Diferença entre RabbitMQ e Kafka
- [ ] Como garantir idempotência em consumidores
- [ ] Como medir latência p95 e criar alerta
- [ ] Como escrever teste de integração com Testcontainers
- [ ] Como versionar API sem quebrar clientes
- [ ] Como containerizar app Java (multi-stage build)
- [ ] Trade-offs de cache Redis (consistência vs performance)
- [ ] Estratégia de retry + DLQ
- [ ] O que colocar em logs para troubleshooting
- [ ] Como estruturar pipeline CI/CD
- [ ] Lambda vs classe anônima
- [ ] Quando usar Stream vs loop
- [ ] Record vs classe tradicional

---

## Regra de ouro (por tecnologia)

Para cada ferramenta, saber responder:

1. **Por que usar?**
2. **Quando NÃO usar?**
3. **Como debugar quando falha?**
4. **Como testar?**

---

## Cuidados com 6h/dia

- [ ] Qualidade > velocidade — 6h focadas, não 8h cansadas
- [ ] Prática sempre maior que teoria (proporção ~1:4)
- [ ] Não pular testes — evitam retrabalho
- [ ] Checkpoint na semana 6 — reavaliar ritmo
- [ ] Prioridade: Spring + testes + Docker antes de Kafka se atrasar

---

## Comandos úteis

```bash
cd c:\Users\linco\www\estudos\order-platform
mvn test
mvn spring-boot:run
docker compose up -d
```

---

## Links de referência

- [Spring Boot Docs](https://docs.spring.io/spring-boot/reference/index.html)
- [Java 17 Features](https://docs.oracle.com/en/java/javase/17/)
- [Testcontainers](https://java.testcontainers.org/)
- [RabbitMQ Tutorials](https://www.rabbitmq.com/tutorials)
- [Kafka Documentation](https://kafka.apache.org/documentation/)
- [Prometheus](https://prometheus.io/docs/introduction/overview/)
- [Baeldung](https://www.baeldung.com/)

---

## Diário de estudos

| Data | Semana/Dia | Horas | O que estudei | Dúvidas | Próximo passo |
|------|------------|-------|---------------|---------|---------------|
| | | | | | |
| | | | | | |
| | | | | | |

---

## Notas e aprendizados

_Espaço livre para anotações durante o plano._
