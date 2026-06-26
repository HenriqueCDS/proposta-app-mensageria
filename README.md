# Proposta App — Arquitetura de Microsserviços

Plataforma de análise e gestão de propostas de crédito construída com arquitetura de microsserviços, comunicação assíncrona via RabbitMQ, persistência em PostgreSQL, notificações SMS via AWS SNS e interface Angular com atualização em tempo real por WebSocket.

---

## Módulos do Sistema

| Módulo | Tecnologia | Porta | Função |
|---|---|---|---|
| [modulo-pedido](./modulo-pedido/README.md) | Spring Boot + PostgreSQL | 8080 | API REST, orquestração do fluxo e persistência |
| [modulo-analisecredito](./modulo-analisecredito/README.md) | Spring Boot | — | Análise de crédito por pontuação (Strategy Pattern) |
| [modulo-notificacao](./modulo-notificacao/README.md) | Spring Boot + AWS SNS | — | Envio de SMS ao usuário nas etapas da proposta |
| [Front-end](./Front-end/cadastro-web-master/README.md) | Angular 16 | 4200 | Interface de cadastro com status em tempo real |

---

## Arquitetura Geral

```
┌──────────────────────────────────────────────────────┐
│              USUÁRIO (Navegador)                     │
│                                                      │
│   ┌──────────────────────────────────────────────┐   │
│   │      Angular Frontend  (porta 4200)          │   │
│   │  - Formulário de proposta                    │   │
│   │  - Listagem com status                       │   │
│   │  - WebSocket (atualização em tempo real)     │   │
│   └─────────────┬──────────────────┬─────────────┘   │
└─────────────────│──────────────────│─────────────────┘
                  │ HTTP REST        │ WebSocket STOMP
                  │ POST/GET         │ /ws → /propostas
                  ▼                  ▼
┌──────────────────────────────────────────────────────┐
│           modulo-pedido  (porta 8080)                │
│                                                      │
│  - Recebe proposta via REST                          │
│  - Persiste no PostgreSQL                            │
│  - Publica no RabbitMQ                               │
│  - Ouve resultado da análise                         │
│  - Retry automático a cada 10s (propostas falhas)    │
│  - Notifica frontend via WebSocket                   │
│                                                      │
│         ┌────────────────────┐                       │
│         │   PostgreSQL :5432  │                      │
│         │   database: proposta│                      │
│         └────────────────────┘                       │
└──────────┬────────────────────────────────┬──────────┘
           │ Publica                        │ Consome
           ▼                                ▼
┌──────────────────────────────────────────────────────┐
│                    RabbitMQ :5672                    │
│                                                      │
│   Exchange: proposta-pendente.ex  (fanout)           │
│   ├── Queue: proposta-pendente.ms-analise-credito    │
│   └── Queue: proposta-pendente.ms-notificacao        │
│                                                      │
│   Exchange: proposta-concluida.ex (fanout)           │
│   ├── Queue: proposta-concluida.ms-analise-credito   │
│   └── Queue: proposta-concluida.ms-notificacao       │
└──────────┬────────────────────────────────┬──────────┘
           │ Consome                        │ Consome
           ▼                                ▼
┌──────────────────────┐     ┌───────────────────────────┐
│ modulo-analisecredito│     │   modulo-notificacao      │
│                      │     │                           │
│  Strategy Pattern:   │     │  Templates de mensagem:   │
│  1. Nome negativado  │     │  - Proposta recebida      │
│  2. Score de crédito │     │  - Proposta aprovada      │
│  3. Renda vs valor   │     │  - Proposta negada        │
│  4. Outros emprést.  │     │                           │
│  5. Prazo < 10 anos  │     │  ┌────────────────────┐   │
│                      │     │  │    AWS SNS         │   │
│  > 350 pts = aprovado│     │  │  SMS ao usuário    │   │
│                      │     │  └────────────────────┘   │
│  Publica resultado   │     │                           │
│  em proposta-        │     │                           │
│  concluida.ex        │     │                           │
└──────────────────────┘     └───────────────────────────┘
```

---

## Fluxo Completo de uma Proposta

```
1. Usuário preenche formulário no Angular e envia

2. Angular → POST /proposta → modulo-pedido
   - modulo-pedido salva no PostgreSQL (integrada = true)
   - Publica em proposta-pendente.ex

3. RabbitMQ entrega para dois consumidores simultaneamente:
   a) modulo-analisecredito (queue: proposta-pendente.ms-analise-credito)
   b) modulo-notificacao    (queue: proposta-pendente.ms-notificacao)
      → SMS: "Sua proposta foi recebida"

4. modulo-analisecredito executa análise:
   - Aplica 5 critérios de pontuação (Strategy Pattern)
   - Define aprovado = true/false com observação
   - Publica em proposta-concluida.ex

5. RabbitMQ entrega resultado para dois consumidores:
   a) modulo-pedido (queue: proposta-concluida.ms-analise-credito)
      → Atualiza registro no PostgreSQL
      → Envia mensagem via WebSocket /propostas
   b) modulo-notificacao (queue: proposta-concluida.ms-notificacao)
      → SMS: "Proposta aprovada/negada"

6. Angular recebe mensagem WebSocket
   → Atualiza status da proposta na tabela sem refresh
```

---

## Critérios de Análise de Crédito

A pontuação mínima para aprovação é **350 pontos**. Os critérios são:

| Critério | Condição | Pontos |
|---|---|---|
| Nome não negativado | Lista negra consultada | +100 |
| Score de crédito | 201–1000 pts | +150 |
| Score de crédito | 1001–2000 pts | +180 |
| Score de crédito | > 2000 pts | +220 |
| Renda vs valor solicitado | Renda > valor solicitado | +100 |
| Outros empréstimos | Sem empréstimos em andamento | +80 |
| Prazo de pagamento | Prazo < 120 meses (10 anos) | +80 |

---

## Infraestrutura Necessária

| Serviço | Versão | Porta |
|---|---|---|
| RabbitMQ | 3.x+ | 5672 |
| PostgreSQL | 14+ | 5432 |
| AWS SNS | — | (managed) |

### Inicialização com Docker (sugestão)

```bash
# RabbitMQ com painel de gerenciamento
docker run -d --name rabbitmq \
  -p 5672:5672 -p 15672:15672 \
  rabbitmq:3-management

# PostgreSQL
docker run -d --name postgres \
  -e POSTGRES_PASSWORD=silva007 \
  -e POSTGRES_DB=proposta \
  -p 5432:5432 \
  postgres:14
```

---

## Como Executar o Projeto Completo

Execute cada módulo em um terminal separado, **na ordem abaixo**:

```bash
# 1. Infra
docker start rabbitmq postgres

# 2. API principal (depende do banco e RabbitMQ)
cd modulo-pedido
./mvnw spring-boot:run

# 3. Análise de crédito (depende do RabbitMQ)
cd modulo-analisecredito
./mvnw spring-boot:run

# 4. Notificação (depende do RabbitMQ e AWS SNS)
cd modulo-notificacao
./mvnw spring-boot:run

# 5. Frontend
cd Front-end/cadastro-web-master
npm install && npm start
```

Acesse a aplicação em `http://localhost:4200`.

---

## Decisões de Arquitetura

### Comunicação Assíncrona (RabbitMQ Fanout)
Exchanges do tipo fanout permitem que um único evento seja entregue a múltiplos consumidores (análise de crédito e notificação) sem acoplamento entre eles. Adicionar um novo módulo exige apenas criar uma nova fila vinculada ao exchange existente.

### Resiliência — Retry de Integração
O `modulo-pedido` persiste a flag `integrada = false` quando a publicação no RabbitMQ falha. Um agendador verifica a cada 10 segundos e republica as mensagens pendentes, garantindo entrega eventual sem perda de dados.

### Extensibilidade — Strategy Pattern
Os critérios de análise de crédito são implementados como estratégias independentes. Adicionar ou remover um critério não afeta os demais — basta criar uma nova classe implementando `CalculoPonto` com a anotação `@Order` adequada.

### Tempo Real — WebSocket STOMP
O frontend não faz polling para saber o resultado da análise. O `modulo-pedido` publica no tópico `/propostas` ao receber o resultado, e o Angular atualiza a UI instantaneamente via STOMP sobre SockJS.
