# Front-end — Cadastro Web

Aplicação Angular que fornece a interface gráfica para cadastro de propostas de crédito e acompanhamento do resultado em tempo real via WebSocket.

## Visão Geral

SPA (Single Page Application) que permite ao usuário preencher uma proposta de crédito, enviá-la ao backend e acompanhar o status de aprovação em tempo real, sem necessidade de recarregar a página.

## Tecnologias

| Tecnologia | Versão |
|---|---|
| Angular | 16.2.0 |
| Angular CLI | 16.2.6 |
| Angular Material | 16.2.13 |
| Bootstrap | 5.3.2 |
| RxJS | 7.8.0 |
| SockJS + StompJS | 1.6.1 / 2.3.3 |
| ngx-toastr | 18.0.0 |
| ng2-currency-mask | 13.0.3 |
| ngx-mask | 17.0.4 |
| TypeScript | 5.1.3 |

## Funcionalidades

- Formulário de cadastro de proposta com validação em tempo real
- Máscaras de input para CPF e valores monetários
- Listagem de todas as propostas cadastradas
- Atualização automática do status via **WebSocket** (sem refresh)
- Notificações toast ao enviar uma proposta

## Formulário de Proposta

| Campo | Validação |
|---|---|
| Nome | Obrigatório, mínimo 3 caracteres |
| Sobrenome | Obrigatório, mínimo 3 caracteres |
| Telefone | Obrigatório, mínimo 3 caracteres |
| CPF | Obrigatório, 11 dígitos (com máscara) |
| Renda | Obrigatório, campo monetário |
| Valor Solicitado | Obrigatório, campo monetário |
| Prazo de Pagamento | Padrão: 24 meses |

## Integração com o Backend

### REST API

| Método | URL | Ação |
|---|---|---|
| `POST` | `http://localhost:8080/proposta` | Cria nova proposta |
| `GET` | `http://localhost:8080/proposta` | Lista todas as propostas |

### WebSocket (Tempo Real)

| Conexão | Tópico | Finalidade |
|---|---|---|
| `http://localhost:8080/ws` | `/propostas` | Recebe atualização de status quando a análise é concluída |

O `WebSocketConnector` se conecta via **STOMP sobre SockJS** e realiza reconexão automática em caso de falha (retry a cada 3 segundos). Quando uma mensagem chega no tópico `/propostas`, a proposta correspondente na lista é atualizada automaticamente.

## Estrutura do Projeto

```
src/
  app/
    app.component.ts        # Componente principal (formulário + tabela)
    app.module.ts           # Módulo raiz com imports
    models/
      proposta.model.ts     # Interface do modelo de proposta
    services/
      cadastro.service.ts   # Chamadas HTTP ao backend
    ws/
      web-socket-connector.ts  # Cliente STOMP/SockJS
```

## Modelo de Dados

```typescript
interface Proposta {
  id: number;
  nome: string;
  sobrenome: string;
  telefone: string;
  cpf: string;
  renda: number;
  valorSolicitado: number;
  valorSolicitadoFmt: string;
  prazoPagamento: number;
  aprovada: boolean;
  observacao: string;
}
```

## Como Executar

### Desenvolvimento

```bash
npm install
npm start
```

Acesse em `http://localhost:4200`. Requer o `modulo-pedido` rodando em `localhost:8080`.

### Build de Produção

```bash
npm run build
```

### Docker

```bash
docker build -t cadastro-web .
docker run -p 80:80 cadastro-web
```

O Dockerfile realiza um build multi-stage: compila a aplicação com **Node 18.16** e serve o resultado estático com **NGINX Alpine**.

## Testes

```bash
# Testes unitários
npm test

# Testes end-to-end (requer configuração de plataforma e2e)
ng e2e
```
