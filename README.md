# Notification Gateway

Serviço centralizado de notificações construído com **Spring Boot 3** e **Java 21**. Permite cadastrar **templates de e-mail versionados**, enviar mensagens (imediatas ou **agendadas**) com **substituição de variáveis**, tudo protegido por autenticação **JWT** e isolado por **grupo** (departamento/área da empresa).

---

## ✨ Funcionalidades

- 🔐 **Autenticação JWT** com login por e-mail/senha e senhas criptografadas (BCrypt).
- 📝 **Templates versionados** — cada template pode ter múltiplas versões (assunto + corpo), em `TEXT` ou `HTML`.
- 👥 **Isolamento por grupo** — templates são visíveis apenas para usuários do mesmo grupo (`groupName`).
- 📧 **Envio de e-mail via SMTP** (imediato).
- ⏰ **Agendamento** — mensagens com `scheduledAt` futuro são enviadas automaticamente por um scheduler que roda a cada minuto.
- 🔧 **Variáveis dinâmicas** — placeholders no formato `{{nome}}` no assunto e corpo são substituídos no momento do envio.
- ✅ **Validação de requisições** com Bean Validation e tratamento global de erros.
- 📚 **Documentação interativa** via Swagger / OpenAPI.

---

## 🛠️ Stack

| Camada           | Tecnologia                                    |
|------------------|-----------------------------------------------|
| Linguagem        | Java 21                                        |
| Framework        | Spring Boot 3.5                                 |
| Persistência     | Spring Data JPA / Hibernate                     |
| Banco (dev)      | H2 em memória                                   |
| Banco (prod)     | PostgreSQL                                       |
| Segurança        | Spring Security + JWT (jjwt)                     |
| E-mail           | Spring Mail (SMTP)                               |
| Docs             | springdoc-openapi (Swagger UI)                  |
| Build            | Maven (wrapper incluído)                         |
| Utilitários      | Lombok                                           |

---

## 🏗️ Arquitetura

Arquitetura em camadas clássica do Spring:

```
controller  →  service  →  repository  →  banco
                  │
                  ├── mapper      (entidade ⇄ DTO)
                  ├── provider    (envio de e-mail via SMTP)
                  └── scheduler   (processa agendamentos)
```

### Modelo de domínio

```
User ──(groupName)──┐
                    │
Template ───────────┘   1 ──── N   TemplateVersion   1 ──── N   EmailMessage
(escopo por grupo)                 (assunto + corpo +           (destinatário, status,
                                    contentType, versão)         agendamento)
```

- **User** — usuário do sistema, pertence a um *grupo* e tem um *role* (`ROLE_USER` / `ROLE_ADMIN`).
- **Template** — modelo de notificação, único por (`groupName`, `name`).
- **TemplateVersion** — versão concreta de um template (assunto, corpo, tipo de conteúdo).
- **EmailMessage** — uma mensagem a ser enviada a partir de uma versão de template, com status (`PENDING`, `SCHEDULED`, `SENT`, `FAILED`).

---

## 🚀 Como rodar

### Pré-requisitos
- JDK 21+
- (Opcional, para perfil `prod`) PostgreSQL acessível

> O projeto **não define um perfil ativo padrão** — é necessário informar qual perfil usar.

### Ambiente de desenvolvimento (H2 em memória)

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

A aplicação sobe em `http://localhost:8080`.

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **Console H2:** http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:notificationdb`, user `sa`, sem senha)

### Ambiente de produção (PostgreSQL)

Ajuste as credenciais em `src/main/resources/application-prod.properties` e rode:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

### Build do JAR

```bash
./mvnw clean package
java -jar target/gateway-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

---

## 🔑 Autenticação

Ao subir, um usuário **admin** é criado automaticamente (`DataSeeder`):

| Campo  | Valor              |
|--------|--------------------|
| E-mail | `teste@admin.com`  |
| Senha  | `teste123`         |
| Grupo  | `TI`               |
| Role   | `ROLE_ADMIN`       |

### 1. Fazer login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"teste@admin.com","password":"teste123"}'
# → { "token": "eyJhbG..." }
```

O token JWT é válido por **24 horas**.

### 2. Usar o token

Inclua o header em todas as rotas protegidas:

```
Authorization: Bearer <token>
```

> **Cadastro de usuários:** apenas e-mails dos domínios permitidos são aceitos no registro (`POST /api/users`).

---

## 📡 Endpoints

Rotas públicas: `POST /api/auth/login`, `POST /api/users`, Swagger e console H2. As demais exigem JWT.

### Auth
| Método | Rota              | Descrição                                  |
|--------|-------------------|--------------------------------------------|
| POST   | `/api/auth/login` | Autentica e retorna o token JWT            |
| GET    | `/api/auth/me`    | Retorna os dados do usuário autenticado    |

### Usuários
| Método | Rota              | Descrição                          |
|--------|-------------------|------------------------------------|
| POST   | `/api/users`      | Cadastra um usuário (público)      |
| GET    | `/api/users`      | Lista usuários                     |
| GET    | `/api/users/{id}` | Busca usuário por ID               |

### Templates (escopados pelo grupo do usuário autenticado)
| Método | Rota                  | Descrição                                  |
|--------|-----------------------|--------------------------------------------|
| GET    | `/api/templates`      | Lista templates do grupo do usuário        |
| GET    | `/api/templates/{id}` | Busca template por ID                      |
| POST   | `/api/templates`      | Cria template (grupo atribuído pelo login) |

### Versões de Template
| Método | Rota                                         | Descrição                       |
|--------|----------------------------------------------|---------------------------------|
| GET    | `/api/templates/{templateId}/versions`       | Lista versões de um template    |
| GET    | `/api/templates/{templateId}/versions/{id}`  | Busca versão por ID             |
| POST   | `/api/templates/{templateId}/versions`       | Cria uma nova versão            |

### Mensagens de E-mail
| Método | Rota                       | Descrição                                          |
|--------|----------------------------|----------------------------------------------------|
| GET    | `/api/email-messages`      | Lista mensagens                                    |
| GET    | `/api/email-messages/{id}` | Busca mensagem por ID                              |
| POST   | `/api/email-messages`      | Cria e envia/agenda uma mensagem                   |

---

## 📨 Enviando uma mensagem

Exemplo de corpo para `POST /api/email-messages`:

```json
{
  "templateVersionId": 1,
  "toEmail": "destinatario@gmail.com",
  "ccEmails": "copia@gmail.com",
  "scheduledAt": "2026-06-10T09:00:00",
  "variables": {
    "nome": "Maria",
    "pedido": "12345"
  }
}
```

Comportamento:

- **`scheduledAt` no futuro** → a mensagem é salva com status `SCHEDULED` e enviada automaticamente pelo `NotificationScheduler` (executa a cada 60s) quando a data chegar.
- **`scheduledAt` ausente ou no passado** → envio **imediato** via SMTP, status `SENT`.

### Variáveis

No assunto/corpo da versão do template, use placeholders `{{chave}}`:

```
Assunto: Olá {{nome}}, seu pedido {{pedido}} foi confirmado!
```

Os valores enviados em `variables` substituem os placeholders no momento do envio.

---

## ⚙️ Configuração

Os arquivos de configuração ficam em `src/main/resources/`:

- `application.properties` — configurações comuns (porta, SMTP, Swagger).
- `application-dev.properties` — banco H2 em memória, console habilitado.
- `application-prod.properties` — PostgreSQL.

### SMTP

O envio de e-mail é feito via SMTP, configurado em `application.properties`:

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<seu-email>
spring.mail.password=<sua-senha-de-app>
```

> ⚠️ **Segurança:** atualmente há credenciais (SMTP, senhas de banco e do seed) versionadas em texto puro no repositório. Para uso real, mova esses valores para **variáveis de ambiente** ou um gerenciador de segredos e **revogue** quaisquer credenciais já commitadas.

---

## 🧪 Testes

```bash
./mvnw test
```

---

## 📂 Estrutura do projeto

```
src/main/java/com/notification/gateway/
├── config/        # SecurityConfig, DataSeeder (CORS, JWT filter chain, seed do admin)
├── controller/    # Endpoints REST
├── dto/           # Request/Response DTOs
├── exception/     # Exceções e handler global
├── mapper/        # Conversão entidade ⇄ DTO
├── model/         # Entidades JPA + enums
├── provider/      # EmailProvider / SmtpEmailProvider
├── repository/    # Spring Data repositories
├── scheduler/     # NotificationScheduler (envio de agendados)
├── security/      # JwtService, filtro JWT, UserDetailsService
└── GatewayApplication.java
```
