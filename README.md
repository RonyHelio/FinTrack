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

## 🚀 Como Rodar o Backend

### Opção 1: Docker Compose (recomendado)

```bash
# Na raiz do projeto
docker-compose up -d
```

Isso sobe o PostgreSQL (porta 5432) e o Spring Boot (porta 8080) automaticamente.

### Opção 2: Manual

#### 1. Criar o banco de dados

```sql
CREATE DATABASE fintrack;
CREATE USER fintrack_user WITH PASSWORD 'fintrack_pass';
GRANT ALL PRIVILEGES ON DATABASE fintrack TO fintrack_user;
```

#### 2. Configurar o `application.properties`

O arquivo já está configurado em `FinTrack/src/main/resources/application.properties`. Ajuste as credenciais se necessário:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/fintrack
spring.datasource.username=fintrack_user
spring.datasource.password=fintrack_pass
```

#### 3. Rodar o backend

```bash
cd FinTrack
mvn spring-boot:run
```

O backend estará disponível em `http://localhost:8080`.

#### 4. Acessar o Swagger

Abra no navegador: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

> ⚠️ Na primeira execução, o `DataInitializer` cria automaticamente 8 categorias globais: Alimentação, Transporte, Moradia, Lazer, Saúde, Educação, Salário e Investimentos.

---

## 📱 Como Rodar o Frontend

### 1. Instalar dependências

```bash
cd frontend
npm install
```

### 2. Configurar o IP da API

Edite o arquivo `frontend/src/constants/index.ts` e ajuste o `API_BASE_URL` com o IP da sua rede local:

```typescript
export const API_BASE_URL = "http://SEU_IP_LOCAL:8080/api";
```

> 💡 Para descobrir seu IP: `ipconfig` (Windows) ou `ifconfig` (Mac/Linux).

### 3. Iniciar o Expo

```bash
npx expo start
```

### 4. Testar no celular

- Escaneie o QR Code com o **Expo Go** (Android) ou a câmera do iPhone
- Ou pressione `a` para abrir no emulador Android

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
