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

- **Java 17+** (JDK)
- **Maven 3.9+**
- **PostgreSQL 15+** (ou Docker)
- **Node.js 18+** e **npm 9+**
- **Expo CLI** (`npx expo`)
- **Expo Go** no celular (Android/iOS) ou emulador Android

---

## 🚀 Como Rodar o Projeto (Recomendado: via Docker)

A maneira mais fácil e garantida de rodar o FinTrack é utilizando o **Docker**. Com um único comando, o Docker vai preparar o Banco de Dados, o Backend (Java) e o Frontend (Aplicativo Web) para você.

### Passo a Passo

**1. Instale o Docker Desktop:**
Se você ainda não tem o Docker, baixe e instale o [Docker Desktop](https://www.docker.com/products/docker-desktop/) no seu computador e certifique-se de que ele está aberto e rodando.

**2. Abra o terminal na pasta do projeto:**
Abra o seu terminal (Prompt de Comando, PowerShell ou terminal do VS Code) e certifique-se de estar na pasta raiz do projeto (onde está o arquivo `docker-compose.yml`).

**3. Clone o repositório e entre na pasta:**
```bash
git clone https://github.com/RonyHelio/FinTrack.git
cd FinTrack
```

> ⚠️ **Atenção:** O comando `docker-compose` deve ser executado na pasta raiz do projeto (onde está o arquivo `docker-compose.yml`). **Não** entre na subpasta `FinTrack/FinTrack/` (que é o backend).

**4. Execute o comando de inicialização:**
Certifique-se de que o **Docker Desktop está aberto e rodando**, depois digite:
```bash
docker-compose up --build -d
```
*A primeira vez que você rodar esse comando pode demorar alguns minutos, pois o Docker vai baixar todas as ferramentas e compilar o código do zero.*

**4. Acesse a aplicação:**
Assim que o terminal finalizar a execução, os três sistemas estarão rodando sincronizados!
- **📱 O Aplicativo (Frontend):** Abra no seu navegador o endereço [http://localhost:8081](http://localhost:8081)
- **⚙️ O Backend (API):** Está rodando nos bastidores na porta `8080`.
- **📚 A Documentação da API (Swagger):** Acesse em [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

**Dica:** Para parar o projeto quando terminar de usar, basta rodar no terminal:
```bash
docker-compose down
```

---

## 🛠 Como Rodar Manualmente (Sem Docker)

Se preferir rodar cada parte manualmente (ideal para desenvolvimento isolado), siga os passos abaixo nesta exata ordem:

### 1. Subir o Banco de Dados
Você precisará de um servidor PostgreSQL rodando localmente (na porta 5432). Crie um banco chamado `fintrack` e um usuário `fintrack_user` com a senha `fintrack_pass`:
```sql
CREATE DATABASE fintrack;
CREATE USER fintrack_user WITH PASSWORD 'fintrack_pass';
GRANT ALL PRIVILEGES ON DATABASE fintrack TO fintrack_user;
```

### 2. Subir o Backend (Java Spring Boot)
No seu terminal, entre na pasta do backend e rode o projeto via Maven:
```bash
cd FinTrack
mvn spring-boot:run
```
*A API ficará disponível em [http://localhost:8080/api](http://localhost:8080/api).*

### 3. Subir o Frontend (React Native Web)
Abra **outro** terminal, entre na pasta do frontend e instale as bibliotecas necessárias:
```bash
cd frontend
npm install --legacy-peer-deps
```
Em seguida, inicie o servidor web do aplicativo:
```bash
npx expo start --web
```
*O seu navegador deve abrir automaticamente com o aplicativo rodando em [http://localhost:8081](http://localhost:8081). Se for testar pelo celular, leia as instruções no terminal do Expo.*

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
