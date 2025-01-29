# Email Adapter (Desafio Viasoft)

Email Adapter é uma aplicação baseada em Spring Boot para envio de e-mails via AWS ou OCI. A integração é definida dinamicamente através de uma configuração no arquivo `application.properties`.

## Tecnologias Utilizadas

- Java 17
- Spring Boot
- MongoDB 8.0.4
- AWS SES / OCI Email Delivery

## Configuração

Antes de iniciar a aplicação, configure as variáveis no `application.properties`:

```properties
spring.application.name=Email Adapter
spring.data.mongodb.uri=mongodb://localhost:27017/email-adapter
mail.integracao=OCI  # Pode ser 'AWS' ou 'OCI'
```

### Configuração do MongoDB

A aplicação utiliza o MongoDB para armazenar logs dos e-mails enviados. Certifique-se de que a coleção `email-logs` existe no banco de dados `email-adapter`. Caso contrário, crie-a manualmente:

```sh
use email-adapter
db.createCollection("email-logs")
```

## Estrutura do Projeto

- **Controller:** Gerencia as requisições HTTP.
- **DTOs:** Estruturas de dados para envio de e-mails.
- **Service:** Implementa a lógica de negócio e o padrão Strategy para integração dinâmica.
- **Exceptions:** Tratamento de erros específicos para envio de e-mails.
- **Logger:** Registra logs de sucesso e falha no MongoDB.

## Validação dos Campos

Os DTOs utilizados na API possuem validações para garantir a integridade dos dados antes do envio do e-mail. Cada serviço pode ter um DTO diferente devido às exigências específicas da AWS ou OCI.

### EmailAwsDTO

```java
public record EmailAwsDTO(
        @NotBlank(message = "Recipient's email is required")
        @Email(message = "Invalid email format")
        @Size(max = 45, message = "Recipient's email must be at most 45 characters")
        String recipient,

        @NotBlank(message = "Recipient's name is required")
        @Size(max = 60, message = "Recipient's name must be at most 60 characters")
        String recipientName,

        @NotBlank(message = "Sender's email is required")
        @Email(message = "Invalid email format")
        @Size(max = 45, message = "Sender's email must be at most 45 characters")
        String sender,

        @NotBlank(message = "Subject is required")
        @Size(max = 120, message = "Subject must be at most 120 characters")
        String subject,

        @NotBlank(message = "Content is required")
        @Size(max = 256, message = "Content must be at most 256 characters")
        String content
) {}
```

### EmailOciDTO

```java
public record EmailOciDTO(
        @NotBlank(message = "Recipient's email is required")
        @Email(message = "Invalid email format")
        @Size(max = 40, message = "Recipient's email must be at most 40 characters")
        String recipientEmail,

        @NotBlank(message = "Recipient's name is required")
        @Size(max = 50, message = "Recipient's name must be at most 50 characters")
        String recipientName,

        @NotBlank(message = "Sender's email is required")
        @Email(message = "Invalid email format")
        @Size(max = 40, message = "Sender's email must be at most 40 characters")
        String senderEmail,

        @NotBlank(message = "Subject is required")
        @Size(max = 100, message = "Subject must be at most 100 characters")
        String subject,

        @NotBlank(message = "Body is required")
        @Size(max = 250, message = "Body must be at most 250 characters")
        String body
) {}
```

Essas validações garantem que os dados enviados para os serviços de e-mail estejam no formato correto e dentro das restrições exigidas.

## Estrutura dos Logs de Erro

A aplicação registra logs de e-mails enviados com sucesso e falhas no MongoDB na coleção `email-logs`. Cada registro contém as seguintes informações:

```java
@Document(collection = "email-logs")
public class EmailLog {
    @Id
    private String id;
    private String recipient;
    private String sender;
    private String subject;
    private LogStatus status;
    private LocalDateTime timestamp;
}
```

Os logs podem ter dois status:

- `SUCCESS` → O e-mail foi enviado com sucesso.
- `FAIL` → O envio do e-mail falhou.

### Exemplo de Log de Sucesso

```json
{
  "recipient": "user@example.com",
  "sender": "sender@example.com",
  "subject": "Teste de E-mail",
  "status": "SUCCESS",
  "timestamp": "2025-01-29T12:34:56"
}
```

### Exemplo de Log de Falha

```json
{
  "recipient": "user@example.com",
  "sender": "sender@example.com",
  "subject": "Teste de E-mail",
  "status": "FAIL",
  "timestamp": "2025-01-29T12:35:10"
}
```

Os logs de erro ajudam a monitorar falhas no envio de e-mails e identificar possíveis problemas.

## Endpoints

### Envio de E-mail

**POST /emails**

**Request Body:**

```json
{
  "recipient": "user@example.com",
  "recipientName": "User Name",
  "sender": "sender@example.com",
  "subject": "Teste de E-mail",
  "content": "Este é um e-mail de teste."
}
```

**Possíveis Respostas:**

- `204 No Content` - E-mail enviado com sucesso.
- `400 Bad Request` - Erro na requisição.
- `503 Service Unavaiable` - Serviço Indisponível.
- `500 Internal Server Error` - Erro interno do servidor.

## Como Executar

1. Clone o repositório:
2. Configure o `application.properties`.
3. Certifique-se de que o MongoDB está rodando e a coleção `email-logs` existe.
4. Instale as dependências com o Maven
   ```sh
   mvn install
   ```
5. Execute a aplicação com:
   ```sh
   mvn spring-boot:run
   ```

## Testes Unitários

A aplicação conta com testes unitários para garantir a qualidade do código e a funcionalidade correta dos componentes principais. Para rodar os testes:

```sh
mvn test
```

