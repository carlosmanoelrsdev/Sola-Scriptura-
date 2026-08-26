# Sola-Scriptura-
Sola Scriptura é uma aplicação web moderna para leitura, estudo e reflexão da Bíblia. Oferece múltiplas traduções, registro de leituras, streak, acompanhamento de progresso e devocionais pessoais. Desenvolvida com Java, Spring Boot, Spring Security, React, TypeScript, PostgreSQL e Docker.

## Estrutura inicial

- `backend/`: API Spring Boot
- `frontend/`: aplicação React + Vite
- `docker-compose.yml`: orquestra PostgreSQL, backend e frontend
- `.env.example`: variáveis de ambiente base

## Execução local com Docker

1. Copie `.env.example` para `.env`
2. Execute `docker compose up --build`
