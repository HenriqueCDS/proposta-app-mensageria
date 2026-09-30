# modulo-gateway

API Gateway (Spring Cloud Gateway / WebFlux) que fica entre o front-end e o `modulo-pedido`.

```
Angular (4200) ──► modulo-gateway (8080) ──► modulo-pedido (8081, interno)
```

## Rotas

| Caminho | Destino |
|---|---|
| `/proposta/**` | `PEDIDO_URL` (REST) |
| `/ws/**` | `PEDIDO_URL` (WebSocket/SockJS) |

## Configuração (`.env`)

| Variável | Padrão | Descrição |
|---|---|---|
| `PEDIDO_URL` | `http://localhost:8081` | URL do `modulo-pedido` |
| `CORS_ALLOWED_ORIGINS` | `http://localhost,http://localhost:4200` | Origens permitidas |

## Executar localmente

```bash
./mvnw spring-boot:run
```

Health check: `GET http://localhost:8080/actuator/health`

## Próximos passos
- Autenticação JWT (filtro global + `POST /auth/login`)
- Rate limiting e correlation-id
