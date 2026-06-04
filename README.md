# 💰 FinTrack — Controle Financeiro Pessoal

> **Projeto acadêmico de Sistemas de Informação — Equipe: Rony Hélio, Rangel Matos e Guilherme**

Aplicativo mobile de controle financeiro pessoal desenvolvido como Prova de Conceito (PoC) acadêmica. Permite registrar receitas e despesas, categorizar gastos, filtrar transações e visualizar um dashboard financeiro completo.

---

## 📋 Índice

- [Stack Tecnológica](#-stack-tecnológica)
- [Arquitetura](#-arquitetura)
- [Pré-requisitos](#-pré-requisitos)
- [Como Rodar o Backend](#-como-rodar-o-backend)
- [Como Rodar o Frontend](#-como-rodar-o-frontend)
- [Endpoints da API](#-endpoints-da-api)
- [Funcionalidades](#-funcionalidades)
- [Estrutura do Projeto](#-estrutura-do-projeto)

---

## 🛠 Stack Tecnológica

| Camada     | Tecnologia                          | Versão   |
|------------|--------------------------------------|----------|
| Backend    | Java + Spring Boot                   | 4.0.6    |
| Banco      | PostgreSQL                           | 15+      |
| ORM        | Spring Data JPA + Hibernate          | —        |
| Auth       | JWT (jjwt)                           | 0.12.6   |
| Docs       | Swagger / OpenAPI (springdoc)        | 2.8.9    |
| Frontend   | React Native + Expo                  | 56.0     |
| Linguagem  | TypeScript (strict mode)             | 6.0      |
| Estilo     | NativeWind v4 (TailwindCSS)          | 4.2      |
| Navegação  | React Navigation                     | 7.x      |
| HTTP       | Axios                                | 1.16     |
| Container  | Docker + Docker Compose              | —        |

---

## 🏗 Arquitetura

```
┌──────────────┐       HTTP/JSON        ┌──────────────────┐       JDBC        ┌────────────┐
│  React Native │ ──────────────────────▶ │  Spring Boot API │ ────────────────▶ │ PostgreSQL │
│  (Expo + TS)  │ ◀────────────────────── │  (REST + JWT)    │ ◀──────────────── │            │
└──────────────┘       Bearer Token      └──────────────────┘                   └────────────┘
```

- **Stateless REST API**: Autenticação via JWT, sem sessão no servidor
- **Tenant Isolation**: Todo acesso a dados validado pelo userId extraído do JWT
- **3 camadas**: Controller → Service → Repository

---

## ✅ Pré-requisitos

Para rodar via **Docker** (recomendado), você só precisa de:
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) instalado e **aberto** (ícone da baleia 🐳 visível na barra de tarefas)

Para rodar **manualmente** (sem Docker), você precisa de:
- **Java 17+** (JDK) — [Download](https://adoptium.net/)
- **Maven 3.9+** — [Download](https://maven.apache.org/download.cgi)
- **PostgreSQL 15+** — [Download](https://www.postgresql.org/download/)
- **Node.js 20+** e **npm** — [Download](https://nodejs.org/)

---

## 🚀 Como Rodar o Projeto (Recomendado: via Docker)

A maneira mais fácil de rodar o FinTrack. Com **3 comandos** o Docker prepara tudo automaticamente (Banco de Dados + Backend + Frontend).

### Passo 1 — Abra o Docker Desktop

Antes de qualquer coisa, abra o aplicativo **Docker Desktop** no seu computador e **aguarde** ele inicializar completamente (o ícone da baleia 🐳 na barra de tarefas deve ficar estável, sem animação de carregamento).

> ⚠️ **Se o Docker Desktop não estiver aberto, nenhum comando `docker` vai funcionar!** Você verá um erro parecido com: `failed to connect to the docker API`.

### Passo 2 — Clone o repositório

Abra um terminal (PowerShell, Prompt de Comando ou terminal do VS Code) e execute:

```bash
git clone https://github.com/RonyHelio/FinTrack.git
```

### Passo 3 — Entre na pasta do projeto

```bash
cd FinTrack
```

> ⚠️ **Cuidado:** O projeto possui uma subpasta que também se chama `FinTrack/` (é o backend Java). **NÃO** entre nela. Você deve ficar na **pasta raiz**, que é a que contém o arquivo `docker-compose.yml`. Para confirmar, rode `dir` (Windows) ou `ls` (Mac/Linux) e verifique se o arquivo `docker-compose.yml` aparece na listagem.

### Passo 4 — Suba todos os containers

```bash
docker-compose up --build -d
```

> ⏳ **Na primeira vez** esse comando vai demorar de **3 a 5 minutos**, pois o Docker precisa baixar as imagens base (Java, Node, PostgreSQL) e compilar o código. Nas próximas vezes será muito mais rápido.

### Passo 5 — Acesse no navegador

Aguarde uns 30 segundos após o comando terminar (o backend precisa inicializar) e então abra:

| O quê | Endereço |
|-------|----------|
| 📱 **Aplicação Web (Frontend)** | [http://localhost:8081](http://localhost:8081) |
| 📚 **Documentação da API (Swagger)** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |

### Como parar o projeto

Para encerrar todos os containers quando terminar de usar:

```bash
docker-compose down
```

### 🔧 Solução de problemas comuns

| Problema | Solução |
|----------|---------|
| `failed to connect to the docker API` | Abra o Docker Desktop e espere ele carregar. |
| `localhost:8081` não abre | Aguarde ~30 segundos. O frontend demora um pouco para compilar na primeira vez. |
| `403 Forbidden` nas requisições | Faça logout e login novamente. O token JWT pode ter expirado. |
| `cd FinTrack` deu erro | Você já pode estar dentro da pasta. Rode `dir` e veja se `docker-compose.yml` aparece. |

---

## 🛠 Como Rodar Manualmente (Sem Docker)

Se preferir rodar cada parte separadamente, siga os passos abaixo **nesta exata ordem**:

### Passo 1 — Banco de Dados

Abra o pgAdmin ou o terminal do PostgreSQL e crie o banco:

```sql
CREATE DATABASE fintrack;
CREATE USER fintrack_user WITH PASSWORD 'fintrack_pass';
GRANT ALL PRIVILEGES ON DATABASE fintrack TO fintrack_user;
```

### Passo 2 — Backend (Java Spring Boot)

Abra um terminal, entre na pasta do backend e rode:

```bash
cd FinTrack/FinTrack
mvn spring-boot:run
```

Aguarde até ver no terminal a mensagem `Started FinTrackApplication`. A API estará disponível em `http://localhost:8080/api`.

### Passo 3 — Frontend (React Native Web)

Abra **outro terminal** (sem fechar o do backend), entre na pasta do frontend:

```bash
cd FinTrack/frontend
npm install --legacy-peer-deps
npx expo start --web
```

O navegador vai abrir automaticamente com o app rodando em [http://localhost:8081](http://localhost:8081).

---

## 🔌 Endpoints da API

### Autenticação (público)

| Método | Rota                 | Descrição            |
|--------|----------------------|----------------------|
| POST   | `/api/auth/register` | Registrar usuário    |
| POST   | `/api/auth/login`    | Login → retorna JWT  |

### Dashboard (requer JWT)

| Método | Rota             | Descrição                    |
|--------|------------------|------------------------------|
| GET    | `/api/dashboard` | Resumo financeiro do mês     |

### Transações (requer JWT)

| Método | Rota                       | Descrição                        |
|--------|----------------------------|----------------------------------|
| GET    | `/api/transacoes`          | Listar todas                     |
| GET    | `/api/transacoes/{id}`     | Buscar por ID                    |
| GET    | `/api/transacoes/filtro`   | Filtrar (mes, ano, categoriaId, tipo) |
| POST   | `/api/transacoes`          | Criar transação                  |
| PUT    | `/api/transacoes/{id}`     | Atualizar transação              |
| DELETE | `/api/transacoes/{id}`     | Excluir transação                |

### Categorias (requer JWT)

| Método | Rota                    | Descrição               |
|--------|-------------------------|-------------------------|
| GET    | `/api/categorias`       | Listar (globais + user) |
| POST   | `/api/categorias`       | Criar personalizada     |
| PUT    | `/api/categorias/{id}`  | Atualizar               |
| DELETE | `/api/categorias/{id}`  | Excluir                 |

---

## 🎯 Funcionalidades

- ✅ Cadastro e login com JWT persistente (Secure Store)
- ✅ Dashboard com saldo total, receitas/despesas do mês e gastos por categoria
- ✅ CRUD completo de transações (receitas e despesas)
- ✅ Filtro de transações por mês, ano, tipo e categoria
- ✅ Listagem de categorias (globais + personalizadas)
- ✅ Isolamento de dados por usuário (Tenant Isolation)
- ✅ Documentação interativa via Swagger UI
- ✅ Testes unitários com JUnit 5 e Mockito
- ✅ Dark mode premium com NativeWind

---

## 📁 Estrutura do Projeto

```
FinTrack/
├── docker-compose.yml
├── README.md
│
├── FinTrack/                          # Backend (Spring Boot)
│   ├── pom.xml
│   └── src/
│       ├── main/java/br/com/fintrack/app/
│       │   ├── config/                # DataInitializer, OpenAPI, WebConfig
│       │   ├── controller/            # Auth, Transacao, Categoria, Dashboard
│       │   ├── dto/                   # Request/Response DTOs
│       │   ├── entity/                # JPA Entities (UUID)
│       │   ├── exception/             # GlobalExceptionHandler + customs
│       │   ├── repository/            # JPA Repositories
│       │   ├── security/              # JWT (JwtService, Filter, SecurityConfig)
│       │   └── service/               # Business logic (AuthService, etc.)
│       └── test/                      # JUnit 5 + Mockito
│
└── frontend/                          # Frontend (React Native + Expo)
    ├── App.tsx                        # Entry point (fontes, auth, navigation)
    ├── tailwind.config.js             # Paleta de cores FinTrack
    └── src/
        ├── constants/                 # API URL, cores, ícones
        ├── contexts/                  # AuthContext (SecureStore + JWT)
        ├── navigation/                # RootNavigator, AuthStack, AppTabs
        ├── screens/
        │   ├── auth/                  # Login, Register
        │   └── app/                   # Dashboard, Transações, Categorias, Perfil
        ├── services/                  # Axios API client + services
        ├── types/                     # TypeScript interfaces
        └── utils/                     # Formatação (moeda, data)
```

---

## 📄 Licença

Projeto acadêmico sem fins comerciais.

**Equipe**: Rony Hélio, Rangel Matos e Guilherme  
**Curso**: Sistemas de Informação
