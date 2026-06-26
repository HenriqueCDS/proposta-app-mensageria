# Módulo Pedido

Microsserviço central da plataforma de propostas de crédito. Expõe a API REST para criação e consulta de propostas, persiste os dados em PostgreSQL, orquestra o fluxo de mensagens via RabbitMQ e fornece atualizações em tempo real ao frontend via WebSocket.

## Visão Geral

É o ponto de entrada da aplicação. Ao receber uma proposta, o módulo a persiste, publica o evento para os demais microsserviços e fica aguardando o resultado da análise de crédito para atualizar o registro e notificar o frontend.

## Tecnologias

| Tecnologia | Versão |
|---|---|
| Java | 17 |
| Spring Boot | 4.1.0 |
| Spring Data JPA | — |
| Spring WebMVC | — |
| Spring WebSocket | — |
| Spring AMQP | — |
| PostgreSQL | — |
| MapStruct | 1.5.5 |
| Lombok | — |
| Jackson | — |

## API REST

Base URL: `http://localhost:8080`

| Método | Endpoint | Descrição | Status de Retorno |
|---|---|---|---|
| `POST` | `/proposta` | Cria uma nova proposta | `201 Created` |
| `GET` | `/proposta` | Lista todas as propostas | `200 OK` |

### Exemplo de Request — POST /proposta

```json
{
  "nome": "João",
  "sobrenome": "Silva",
  "cpf": "12345678901",
  "telefone": "11999999999",
  "renda": 5000.00,
  "valorSolicitado": 10000.00,
  "prazoPagamento": 24
}
```

### Exemplo de Response — GET /proposta

```json
[
  {
    "id": 1,
    "valorSolicitadoFmt": "R$ 10.000,00",
    "prazoPagamento": 24,
    "aprovado": true,
    "observacao": "Proposta aprovada com 430 pontos",
    "usuario": { ... }
  }
]
```

## WebSocket

| Endpoint | Tópico | Finalidade |
|---|---|---|
| `ws://localhost:8080/ws` | `/propostas` | Notifica o frontend quando o resultado da análise é concluído |

## Integração via RabbitMQ

### Topologia (Fanout)

```
proposta-pendente.ex (fanout)
  ├── proposta-pendente.ms-analise-credito
  └── proposta-pendente.ms-notificacao

proposta-concluida.ex (fanout)
  ├── proposta-concluida.ms-analise-credito  ← consumida por este módulo
  └── proposta-concluida.ms-notificacao
```

| Direção | Exchange / Queue | Finalidade |
|---|---|---|
| Saída | `proposta-pendente.ex` | Publica proposta recém-criada |
| Entrada | `proposta-concluida.ms-analise-credito` | Recebe resultado da análise de crédito |

## Fluxo de Dados

```
POST /proposta
      |
      v
 PropostaService.criar()
   - Salva no PostgreSQL (integrada = true)
   - Publica em proposta-pendente.ex
   - Em caso de falha no RabbitMQ: integrada = false
      |
      v
[RabbitMQ: proposta-pendente.ex]
      |
      v (resultado após análise)
[Queue: proposta-concluida.ms-analise-credito]
      |
      v
 PropostaConcluidaListener
   - Atualiza aprovado/observacao no PostgreSQL
   - Envia atualização via WebSocket /propostas
```

### Retry de Integração

`PropostaSemIntegracao` é um `@Scheduled` que roda **a cada 10 segundos** e tenta republicar no RabbitMQ todas as propostas com `integrada = false`, garantindo a entrega eventual das mensagens.

## Banco de Dados

- **SGBD:** PostgreSQL
- **Database:** `proposta`
- **Porta:** 5432
- **Estratégia DDL:** `create-drop` (recria o schema a cada inicialização)

### Modelo de Dados

```
proposta
  id (PK, auto-increment)
  valor_solicitado
  prazo_pagamento
  aprovado
  integrada
  observacao
  usuario_id (FK → usuario)

usuario
  id (PK, auto-increment)
  nome
  sobrenome
  cpf
  telefone
  renda
```

## Mapeamento com MapStruct

| Mapper | De | Para |
|---|---|---|
| `PropostaMapper` | `PropostaRequestDto` | `Proposta` (entidade JPA) |
| `PropostaMapper` | `Proposta` | `PropostaResponseDto` (com formatação de moeda) |

## Configuração CORS

Permite requisições de `http://localhost/` com todos os métodos HTTP.

## Configuração

```properties
spring.application.name=proposta.app
server.port=8080

spring.datasource.url=jdbc:postgresql://localhost:5432/proposta
spring.datasource.username=postgres
spring.datasource.password=silva007
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

rabbitmq.propostapendente.exchange=proposta-pendente.ex
rabbitmq.propostaconcluida.exchange=proposta-concluida.ex
rabbit.queue.proposta.concluida=proposta-concluida.ms-analise-credito
```

## Como Executar

**Pré-requisitos:**
- PostgreSQL rodando em `localhost:5432` com database `proposta` criado
- RabbitMQ rodando em `localhost:5672`

```bash
# Criar banco de dados (se não existir)
psql -U postgres -c "CREATE DATABASE proposta;"

# Iniciar o serviço
./mvnw spring-boot:run
```

A API estará disponível em `http://localhost:8080`.
