# 📖 Sola Scriptura — Bíblia e Estudo Pessoal

**Aplicação web para leitura, acompanhamento e estudo pessoal da Bíblia**

O **Sola Scriptura** é uma aplicação web desenvolvida para proporcionar uma experiência simples e organizada de leitura e estudo da Bíblia, permitindo consultar diferentes traduções, registrar leituras, acompanhar o progresso, manter uma sequência de dias de leitura e criar devocionais pessoais.

---

## 🖥️ Sobre o Projeto

O **Sola Scriptura** foi desenvolvido como um projeto full stack, unindo uma API REST em Java com uma aplicação frontend em React.

A aplicação busca centralizar a experiência de leitura bíblica em um único ambiente, permitindo que o usuário consulte os livros e capítulos da Bíblia e, ao utilizar uma conta, acompanhe seu histórico e progresso de leitura.

### Objetivo

Criar uma aplicação funcional e organizada que ofereça:

* 📖 Leitura da Bíblia;
* 🔎 Pesquisa de versículos;
* 📚 Consulta de diferentes traduções;
* 📊 Acompanhamento do progresso de leitura;
* 🔥 Sequência de dias de leitura (streak);
* 📝 Registro de devocionais pessoais;
* 📅 Histórico de leituras;
* 👤 Gerenciamento da conta;
* 🔐 Autenticação e proteção de recursos privados.

---

## ✨ Funcionalidades

### 📖 Leitura da Bíblia

* Consulta de livros da Bíblia;
* Consulta de capítulos;
* Consulta de versículos;
* Navegação entre livros e capítulos;
* Suporte a diferentes traduções;
* Versículo aleatório;
* Pesquisa na Bíblia;
* Catálogo local de traduções disponíveis.

### 👤 Autenticação

* Cadastro de usuário;
* Login;
* Autenticação utilizando JWT;
* Controle de acesso a recursos protegidos;
* Consulta dos dados do usuário autenticado;
* Atualização do perfil;
* Exclusão da própria conta.

### 📊 Dashboard

Área destinada ao acompanhamento da rotina de leitura:

* Histórico de leituras;
* Progresso de leitura;
* Sequência de dias de leitura;
* Resumo das atividades;
* Informações organizadas em uma interface única.

### 📝 Devocionais

Permite registrar reflexões pessoais relacionadas à leitura bíblica:

* Criar devocional;
* Listar devocionais;
* Visualizar um devocional;
* Editar devocional;
* Excluir devocional;
* Adicionar referências bíblicas aos devocionais.

### 📅 Registro de Leituras

* Registro dos capítulos lidos;
* Histórico de leituras;
* Cálculo da sequência de dias;
* Cálculo do progresso;
* Associação das leituras ao usuário autenticado.

---

## 🛠️ Tecnologias Utilizadas

### Backend

| Tecnologia        | Uso                            |
| ----------------- | ------------------------------ |
| Java 21           | Linguagem principal            |
| Spring Boot 3.5.5 | Framework da aplicação         |
| Spring Web        | Construção da API REST         |
| Spring Data JPA   | Persistência de dados          |
| Spring Security   | Autenticação e autorização     |
| JWT               | Autenticação baseada em tokens |
| Bean Validation   | Validação dos dados            |
| PostgreSQL        | Banco de dados                 |
| Maven             | Gerenciamento de dependências  |
| Springdoc OpenAPI | Documentação da API            |
| JUnit             | Testes automatizados           |
| Mockito           | Testes e mocks                 |
| H2                | Banco utilizado nos testes     |

### Frontend

| Tecnologia      | Uso                                     |
| --------------- | --------------------------------------- |
| React           | Biblioteca para construção da interface |
| TypeScript      | Tipagem estática                        |
| Vite            | Build e desenvolvimento                 |
| Tailwind CSS    | Estilização                             |
| React Router    | Gerenciamento de rotas                  |
| TanStack Query  | Gerenciamento de requisições e cache    |
| React Hook Form | Gerenciamento de formulários            |
| Zod             | Validação de dados                      |
| Lucide React    | Ícones                                  |

### Infraestrutura

| Tecnologia     | Uso                         |
| -------------- | --------------------------- |
| Docker         | Containerização             |
| Docker Compose | Orquestração dos serviços   |
| PostgreSQL 16  | Banco de dados em container |

---

## 🏗️ Estrutura do Projeto

```text
Sola-Scriptura/
│
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── br/com/carlos/solascriptura/
│   │   │   │       ├── config/
│   │   │   │       ├── controller/
│   │   │   │       ├── dto/
│   │   │   │       ├── entities/
│   │   │   │       ├── exceptions/
│   │   │   │       ├── providers/
│   │   │   │       ├── repository/
│   │   │   │       ├── security/
│   │   │   │       └── usecases/
│   │   │   │
│   │   │   └── resources/
│   │   │       ├── bible/
│   │   │       └── application.properties
│   │   │
│   │   └── test/
│   │       ├── controller/
│   │       └── usecases/
│   │
│   ├── Dockerfile
│   ├── pom.xml
│   └── mvnw
│
├── frontend/
│   ├── src/
│   │   ├── assets/
│   │   ├── components/
│   │   ├── hooks/
│   │   ├── layouts/
│   │   ├── lib/
│   │   ├── pages/
│   │   ├── routes/
│   │   ├── services/
│   │   └── types/
│   │
│   ├── public/
│   ├── Dockerfile
│   ├── package.json
│   └── vite.config.ts
│
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

---

## 🔐 Arquitetura do Backend

O backend foi organizado separando responsabilidades por domínio.

### Principais módulos

* **Controller** — recebe as requisições HTTP e expõe os endpoints da API.
* **DTO** — objetos utilizados para entrada e saída de dados.
* **Entities** — representam as entidades persistidas no banco.
* **Repository** — acesso aos dados utilizando Spring Data JPA.
* **UseCases** — concentram as regras de negócio.
* **Providers** — integração e fornecimento dos dados bíblicos.
* **Security** — autenticação e validação dos tokens JWT.
* **Exceptions** — tratamento centralizado de erros.
* **Config** — configurações de segurança, CORS e OpenAPI.

A aplicação utiliza o usuário autenticado através do `SecurityContext`, evitando que recursos pessoais dependam de um ID informado diretamente pelo cliente.

---

## 📚 Dados Bíblicos

O projeto possui uma camada de abstração para o fornecimento dos dados bíblicos através da interface `BibleProvider`.

A implementação atual possui suporte a dados locais, permitindo que determinadas traduções sejam carregadas diretamente dos arquivos disponibilizados no backend.

```text
providers/
└── bible/
    ├── BibleProvider.java
    ├── BibliaApiProvider.java
    ├── OfflineBibleData.java
    ├── OfflineBibleLoader.java
    ├── OfflineBookData.java
    ├── OfflineChapterData.java
    └── OfflineVerseData.java
```

As traduções disponíveis no catálogo local incluem:

* ACF
* ARA
* ARC
* AS21
* JFAA
* KJA
* KJF
* NAA
* NBV
* NTLH
* NVT
* TB

Essa abordagem permite separar a aplicação da origem dos dados bíblicos e facilita futuras alterações no provedor.

---

## 🔗 Principais Endpoints

### Públicos

```text
POST /api/auth/register
POST /api/auth/login

GET /api/bible/versions
GET /api/bible/books
GET /api/bible/versions/{version}/books/{book}/chapters/{chapter}
GET /api/bible/versions/{version}/random
GET /api/bible/versions/{version}/search
```

### Autenticados

```text
GET    /api/dashboard

GET    /api/users/me
PUT    /api/users/me
DELETE /api/users/me

POST /api/readings
GET  /api/readings/history
GET  /api/readings/streak
GET  /api/readings/progress

POST   /api/devotionals
GET    /api/devotionals
GET    /api/devotionals/{id}
PUT    /api/devotionals/{id}
DELETE /api/devotionals/{id}
```

Os endpoints protegidos utilizam autenticação através de:

```text
Authorization: Bearer <token>
```

---

## 🔒 Segurança

O projeto utiliza mecanismos de segurança para proteger os recursos privados da aplicação.

Principais medidas:

* Autenticação baseada em JWT;
* Senhas armazenadas utilizando BCrypt;
* Controle de acesso através do Spring Security;
* Recursos pessoais vinculados ao usuário autenticado;
* Usuário não pode definir ou alterar sua própria role;
* Senhas e hashes não são retornados pela API;
* Tratamento centralizado de erros;
* Configuração de CORS;
* Segredos armazenados através de variáveis de ambiente;
* Arquivo `.env` não deve ser versionado.

---

## 🚀 Instalação

### Pré-requisitos

Antes de executar o projeto, tenha instalado:

* Java 21 ou superior;
* Node.js;
* npm;
* Docker;
* Docker Compose;
* Git.

---

### 1. Clone o repositório

```bash
git clone <https://github.com/carlosmanoelrsdev/Sola-Scriptura->
cd Sola-Scriptura
```

---

### 2. Configure as variáveis de ambiente

Copie o arquivo `.env.example`:

```bash
cp .env.example .env
```

No Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

Configure as variáveis necessárias:

```env
DATABASE_URL=jdbc:postgresql://postgres:5432/solascriptura
DATABASE_USERNAME=seu_usuario
DATABASE_PASSWORD=sua_senha

JWT_SECRET=seu_segredo
JWT_EXPIRATION=86400000

BIBLIA_API_URL=sua_url
BIBLIA_API_KEY=sua_chave

CORS_ALLOWED_ORIGINS=http://localhost:5173

VITE_API_BASE_URL=http://localhost:8080
```

> **Importante:** nunca publique o arquivo `.env` contendo credenciais reais.

---

## 🐳 Executando com Docker

Com as variáveis configuradas, execute:

```bash
docker compose up --build
```

O Docker Compose irá iniciar:

* PostgreSQL;
* Backend Spring Boot;
* Frontend React/Vite.

### URLs padrão

**Frontend**

```text
http://localhost:5173
```

**Backend**

```text
http://localhost:8080
```

**Swagger UI**

```text
http://localhost:8080/swagger-ui.html
```

**OpenAPI**

```text
http://localhost:8080/v3/api-docs
```

---

## 🧪 Testes

### Backend

No diretório `backend`:

```bash
cd backend
```

Windows:

```powershell
.\mvnw.cmd test
```

Linux/macOS:

```bash
./mvnw test
```

Os testes incluem cenários relacionados a:

* Autenticação;
* Cadastro de usuários;
* Dashboard;
* Leituras;
* Devocionais;
* Inicialização da aplicação.

### Frontend

Para verificar o build da aplicação:

```bash
cd frontend
npm install
npm run build
```

Para executar o frontend em desenvolvimento:

```bash
npm run dev
```

---

## 📌 Destaques Técnicos

* Arquitetura full stack separando frontend e backend;
* API REST desenvolvida com Spring Boot;
* Separação das regras de negócio através de Use Cases;
* Autenticação stateless utilizando JWT;
* Persistência com PostgreSQL e JPA;
* Integração com dados bíblicos através de Provider;
* Suporte a dados bíblicos offline;
* Validação de entrada com Bean Validation e Zod;
* Gerenciamento de estado e cache das requisições com TanStack Query;
* Documentação da API utilizando OpenAPI/Swagger;
* Testes automatizados no backend;
* Containerização utilizando Docker;
* Configuração por variáveis de ambiente.

---

## 🔮 Melhorias Futuras

Algumas funcionalidades podem ser adicionadas em futuras versões:

* Planos de leitura personalizados;
* Metas de leitura;
* Mais traduções;
* Melhorias na pesquisa bíblica;
* Exportação dos devocionais;
* Estatísticas mais detalhadas de leitura;
* Destaques e marcações de versículos;
* Anotações durante a leitura;
* Compartilhamento de referências;
* Melhorias na experiência mobile;
* Testes automatizados adicionais no frontend e backend.

---

## 📄 Licença

Este projeto está disponível sob a licença definida no arquivo `LICENSE`.

---

## 👨‍💻 Autor

**Carlos Manoel**

Desenvolvedor em formação, com foco em desenvolvimento backend, Java, Spring Boot e construção de aplicações web.

---

⭐ Se este projeto foi útil ou interessante para você, considere deixar uma estrela no repositório.
