# Email Adapter (Desafio Viasoft)

API em Spring Boot que recebe um e-mail em um formato único e o adapta para o formato de um provedor (AWS ou OCI), escolhido por configuração. Cada tentativa de envio é registrada no MongoDB.

Este projeto foi feito como solução do desafio técnico da Viasoft. As integrações com AWS SES e OCI Email Delivery **não são reais**: como pede o desafio, o "envio" consiste em converter os dados para o DTO do provedor, validar, serializar em JSON e registrar no log da aplicação. Nenhum e-mail é enviado de fato e nenhuma credencial de nuvem é usada.

## Tecnologias

- Java 17
- Spring Boot 3.4 (Web, Validation, Data MongoDB)
- MongoDB 8
- springdoc-openapi (Swagger UI)
- JUnit 5, Mockito e MockMvc
- Docker e Docker Compose

## Como funciona

1. O cliente envia um `POST /emails` com os dados do e-mail.
2. A estratégia configurada (AWS ou OCI) converte os dados para o DTO do provedor.
3. O DTO é validado com as regras daquele provedor.
4. Se estiver válido, o DTO é serializado em JSON e registrado no log (é aqui que entraria a chamada real ao provedor).
5. O resultado (`SUCCESS` ou `FAIL`) é gravado na coleção `email-logs`.

### Decisões de design

**Strategy.** O envio é definido pela interface `EmailSendingStrategy`. A classe abstrata `AbstractEmailSendingStrategy` concentra o fluxo comum (validar, serializar, registrar) e cada provedor implementa só o que muda: o mapeamento para o próprio DTO. Para adicionar um provedor novo basta criar a classe, um valor no enum `EmailProvider` e um `case` em `EmailStrategyConfig`. Como o `switch` cobre o enum inteiro, o compilador acusa se algum provedor ficar sem implementação.

**Seleção por configuração.** A propriedade `mail.integracao` é lida como enum (`MailProperties`). O Spring cria só a estratégia escolhida e a injeta no controller. Se o valor estiver ausente ou for inválido, a aplicação não sobe e mostra quais valores são aceitos:

```
Failed to bind properties under 'mail.integracao' to ...EmailProvider:

    Property: mail.integracao
    Value: "XYZ"

Action:

Update your application's configuration. The following values are valid:

    AWS
    OCI
```

**Um DTO por provedor.** AWS e OCI usam nomes de campos e limites diferentes (por exemplo, o e-mail do destinatário aceita até 45 caracteres na AWS e 40 na OCI, e o corpo se chama `content` na AWS e `body` na OCI). Ter um DTO para cada um deixa essas regras explícitas nas anotações de validação, em vez de espalhar `if` pelo código. Os erros de validação são devolvidos com os nomes de campo da requisição, independente do provedor.

**Log no MongoDB não interfere no envio.** Se o envio deu certo e só a gravação do log falhou, a API responde normalmente e o erro fica registrado no log da aplicação.

## Configuração

`src/main/resources/application.properties`:

```properties
spring.application.name=Email Adapter
spring.data.mongodb.uri=${MONGODB_URI:mongodb://localhost:27017/email-adapter}
mail.integracao=${MAIL_INTEGRACAO:OCI}
```

| Variável de ambiente | Padrão | Descrição |
|---|---|---|
| `MONGODB_URI` | `mongodb://localhost:27017/email-adapter` | Conexão com o MongoDB |
| `MAIL_INTEGRACAO` | `OCI` | Provedor usado: `AWS` ou `OCI` |

A coleção `email-logs` é criada automaticamente pelo MongoDB no primeiro registro.

## Como executar

### Com Docker Compose

Sobe o MongoDB e a aplicação:

```sh
docker compose up --build
```

Para usar a AWS em vez da OCI:

```sh
MAIL_INTEGRACAO=AWS docker compose up --build
```

A API fica em `http://localhost:8080` e o Swagger em `http://localhost:8080/swagger-ui.html`.

### Localmente

Com um MongoDB rodando em `localhost:27017` (pode ser só o do compose, com `docker compose up mongodb`):

```sh
./mvnw spring-boot:run
```

## Testes

```sh
./mvnw test
```

Os testes cobrem a seleção da estratégia pela propriedade, as regras de validação de cada provedor, as respostas de erro do controller (via MockMvc) e o cenário em que o envio funciona mas a gravação do log falha.

## Endpoint

### POST /emails

```json
{
  "recipientEmail": "user@example.com",
  "recipientName": "User Name",
  "senderEmail": "sender@example.com",
  "subject": "Email Test",
  "body": "This is a message"
}
```

| Status | Quando |
|---|---|
| `204 No Content` | E-mail processado com sucesso |
| `400 Bad Request` | Dados inválidos para o provedor ou JSON malformado |
| `503 Service Unavailable` | Falha na comunicação com o provedor |
| `500 Internal Server Error` | Erro inesperado |

Os erros seguem o formato `ProblemDetail` (RFC 9457). Exemplo de resposta 400:

```json
{
  "type": "about:blank",
  "title": "Invalid email data",
  "status": 400,
  "detail": "Validation failed for OCI email data",
  "instance": "/emails",
  "errors": [
    { "field": "recipientEmail", "message": "Invalid email format" },
    { "field": "subject", "message": "Subject is required" }
  ]
}
```

Respostas 500 não expõem mensagem de exceção nem stack trace; os detalhes ficam só no log da aplicação.

## Validações por provedor

| Campo da requisição | AWS (`EmailAwsDTO`) | OCI (`EmailOciDTO`) |
|---|---|---|
| `recipientEmail` | obrigatório, e-mail, até 45 | obrigatório, e-mail, até 40 |
| `recipientName` | obrigatório, até 60 | obrigatório, até 50 |
| `senderEmail` | obrigatório, e-mail, até 45 | obrigatório, e-mail, até 40 |
| `subject` | obrigatório, até 120 | obrigatório, até 100 |
| `body` | obrigatório, até 256 | obrigatório, até 250 |

## Logs no MongoDB

Cada tentativa gera um documento na coleção `email-logs`:

```json
{
  "recipient": "user@example.com",
  "sender": "sender@example.com",
  "subject": "Email Test",
  "status": "SUCCESS",
  "timestamp": "2025-01-29T12:34:56"
}
```

`status` pode ser `SUCCESS` ou `FAIL`.

## Estrutura

```
src/main/java/com/renanabade/email_adapter
├── config         configuração da estratégia e propriedades
├── controller     endpoint REST
├── domain         DTOs, enums e documento do MongoDB
├── exception      exceções e tratamento global de erros
├── repositories   acesso ao MongoDB
├── services       estratégia base e implementações AWS e OCI
└── util           gravação dos logs de envio
```
