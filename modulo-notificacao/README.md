# Módulo Notificação

Microsserviço responsável por enviar notificações SMS aos usuários nas diferentes etapas do ciclo de vida de uma proposta de crédito, utilizando o **AWS SNS** como provedor de SMS.

## Visão Geral

O módulo escuta eventos de proposta via RabbitMQ e envia mensagens SMS personalizadas ao usuário informando o status da proposta (recebida, aprovada ou negada).

## Tecnologias

| Tecnologia | Versão |
|---|---|
| Java | 17 |
| Spring Boot | 4.1.0 |
| Spring AMQP | — |
| AWS SDK SNS | 1.12.641 |
| Lombok | — |
| Jackson | — |

## Integração via RabbitMQ

| Direção | Exchange / Queue | Finalidade |
|---|---|---|
| Entrada | `proposta-pendente.ms-notificacao` | Recebe proposta recém-criada |
| Entrada | `proposta-concluida.ms-notificacao` | Recebe resultado da análise de crédito |

O módulo está vinculado ao exchange fanout `proposta-pendente.ex`, recebendo automaticamente todas as propostas publicadas pelo módulo de pedido.

## Fluxo de Notificação

```
[Queue: proposta-pendente.ms-notificacao]
           |
           v
  PropostaPendenteListener
           |
           v
  NotificacaoSnsService
           |
           v
  AWS SNS → SMS ao usuário
```

## Templates de Mensagem

As mensagens são definidas em `MensagemConstante`:

| Evento | Mensagem |
|---|---|
| Proposta recebida | "Prezado(a) {nome}, sua proposta foi recebida por nossa equipe..." |
| Proposta aprovada | "Prezado(a) {nome}, sua proposta foi APROVADA..." |
| Proposta negada | "Prezado(a) {nome}, sua proposta foi NEGADA. Nosso sistema detectou..." |

## Configuração

```properties
spring.application.name=notificacao

spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=${RABBITMQ_USER}
spring.rabbitmq.password=${RABBITMQ_PASSWORD}

rabbitmq.queue.proposta.pendente=proposta-pendente.ms-notificacao
rabbitmq.proposta.pendente.exchange=proposta-pendente.ex

aws.accessKey=${AWS_ACCESS_KEY:}
aws.secretKey=${AWS_SECRET_KEY:}
aws.region=${AWS_REGION:us-east-1}
```

> **Atenção:** Nunca versione credenciais AWS. Defina `AWS_ACCESS_KEY`, `AWS_SECRET_KEY` e `AWS_REGION` no arquivo `.env` (ignorado pelo git) ou use AWS Parameter Store em produção.

## Configuração AWS SNS

A classe `AmazonSnsConfiguration` inicializa o cliente SNS com as credenciais configuradas e a região `us-east-1`. O serviço utiliza `PublishRequest` para enviar SMS diretamente ao número de telefone do usuário.

## Modelos de Domínio

### Proposta
```
id | valorSolicitado | prazoPagamento | aprovado | integrada | observacao | usuario
```

### Usuario
```
id | nome | sobrenome | cpf | telefone | renda | proposta
```

## Como Executar

**Pré-requisitos:**
- RabbitMQ rodando em `localhost:5672`
- Credenciais AWS válidas com permissão no SNS

```bash
./mvnw spring-boot:run
```

O serviço não expõe porta HTTP — opera exclusivamente via mensageria.
