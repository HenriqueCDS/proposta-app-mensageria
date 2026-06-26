# Módulo Análise de Crédito

Microsserviço responsável por analisar propostas de crédito recebidas via mensageria, aplicar um conjunto de critérios de pontuação e publicar o resultado para os demais serviços.

## Visão Geral

O módulo consome propostas pendentes do RabbitMQ, executa uma análise baseada no padrão **Strategy** com múltiplos critérios de pontuação e, ao final, publica o resultado (aprovado/negado) em um exchange de conclusão.

## Tecnologias

| Tecnologia | Versão |
|---|---|
| Java | 17 |
| Spring Boot | 4.1.0 |
| Spring AMQP | — |
| Lombok | — |
| Jackson | — |

## Arquitetura Interna

### Padrão Strategy — Cálculo de Pontuação

A análise de crédito é composta por 5 estratégias independentes, cada uma implementando a interface `CalculoPonto` e executada em ordem via `@Order`:

| Ordem | Estratégia | Critério | Pontos |
|---|---|---|---|
| 1 | `NomeNegativadoImpl` | Nome não negativado | +100 |
| 2 | `PontuacaoScoreImpl` | Score entre 201–2000 | +150, +180 ou +220 |
| 3 | `RendaMaiorValorSolicitado` | Renda > valor solicitado | +100 |
| 4 | `OutrosEmprestimosEmAndamento` | Sem outros empréstimos ativos | +80 |
| 5 | `PrazoPagamentoInferiorDezAnos` | Prazo < 120 meses | +80 |

**Critério de aprovação:** pontuação total **> 350 pontos**.

### Fluxo de Processamento

```
[Queue: proposta-pendente.ms-analise-credito]
           |
           v
  PropostaEmAnaliseListener
           |
           v
  AnaliseCreditoService
    - Executa todas as estratégias
    - Soma pontuação
    - Define aprovado = true/false
           |
           v
  NotificacaoRabbitService
           |
           v
[Exchange: proposta-concluida.ex]
```

## Integração via RabbitMQ

| Direção | Exchange / Queue | Finalidade |
|---|---|---|
| Entrada | `proposta-pendente.ms-analise-credito` | Recebe propostas para analisar |
| Saída | `proposta-concluida.ex` | Publica resultado da análise |

## Configuração

```properties
spring.application.name=modulo-analisecredito

spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

rabbitmq.queue.proposta.pendente=proposta-pendente.ms-analise-credito
rabbitmq.exchange.proposta.concluida=proposta-concluida.ex
```

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

**Pré-requisitos:** RabbitMQ rodando em `localhost:5672`

```bash
./mvnw spring-boot:run
```

O serviço não expõe porta HTTP — opera exclusivamente via mensageria.
