# Sola Scriptura

Sola Scriptura e uma aplicacao web para leitura, estudo e reflexao da Biblia, com multiplas traducoes, registro de leituras, streak, progresso e devocionais pessoais.

## Stack

- Backend: Java 21, Spring Boot, Spring Web, Spring Data JPA, Spring Security, JWT, Maven, PostgreSQL.
- Frontend: React, TypeScript, Vite, Tailwind CSS, React Router e TanStack Query.
- Infraestrutura: Docker, Docker Compose e PostgreSQL.

## Estrutura

- `backend/`: API Spring Boot.
- `frontend/`: aplicacao React + Vite.
- `docker-compose.yml`: orquestra PostgreSQL, backend e frontend.
- `.env.example`: variaveis de ambiente base.

## Execucao Local

1. Copie `.env.example` para `.env`.
2. Ajuste `JWT_SECRET`, `DATABASE_PASSWORD` e `BIBLIA_API_KEY`.
3. Execute:

```bash
docker compose up --build
```

URLs padrao:

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Variaveis

- `DATABASE_URL`: URL JDBC do PostgreSQL.
- `DATABASE_USERNAME`: usuario do banco.
- `DATABASE_PASSWORD`: senha do banco.
- `JWT_SECRET`: segredo usado para assinar JWTs.
- `JWT_EXPIRATION`: expiracao do token em milissegundos.
- `BIBLIA_API_URL`: URL base da BIBLIAAPI.
- `BIBLIA_API_KEY`: chave da BIBLIAAPI.
- `CORS_ALLOWED_ORIGINS`: origens permitidas pelo backend.
- `VITE_API_BASE_URL`: URL da API usada pelo frontend.

## Endpoints Principais

Publicos:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/bible/versions`
- `GET /api/bible/books`
- `GET /api/bible/versions/{version}/books/{book}/chapters/{chapter}`
- `GET /api/bible/versions/{version}/random`
- `GET /api/bible/versions/{version}/search`

Protegidos por Bearer Token:

- `GET /api/dashboard`
- `GET /api/users/me`
- `PUT /api/users/me`
- `DELETE /api/users/me`
- `POST /api/readings`
- `GET /api/readings/history`
- `GET /api/readings/streak`
- `GET /api/readings/progress`
- `POST /api/devotionals`
- `GET /api/devotionals`
- `GET /api/devotionals/{id}`
- `PUT /api/devotionals/{id}`
- `DELETE /api/devotionals/{id}`

## Seguranca

- Senhas sao armazenadas com BCrypt via `PasswordEncoder`.
- Autenticacao usa JWT em `Authorization: Bearer <token>`.
- O usuario nao define nem altera a propria role.
- Recursos pessoais usam o usuario autenticado pelo `SecurityContext`.
- O backend nao retorna senha ou hash de senha.
- Erros de autenticacao e autorizacao retornam JSON consistente, sem stack trace.
- `.env` real nao deve ser versionado.

## Testes

Backend:

```bash
cd backend
.\mvnw.cmd test
```

Frontend:

```bash
cd frontend
npm run build
```
