# Arquitetura do ms-platform-auth

## Visão Geral

O **ms-platform-auth** é um microserviço de autenticação e autorização desenvolvido com as melhores práticas de desenvolvimento moderno, utilizando:

- **Spring Boot 3.1.5** com **Kotlin 1.8.22**
- **PostgreSQL 15** como banco de dados relacional
- **Spring Security** para segurança da aplicação
- **Keycloak 22.0.5** para gerenciamento de identidade e acesso (IAM)
- **Docker** e **Docker Compose** para containerização
- **Gradle 8.4** para gerenciamento de build

## Arquitetura de Componentes

```
┌─────────────────────────────────────────────────────────────┐
│                        Cliente (API)                         │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           │ HTTP/REST + JWT Bearer Token
                           │
┌──────────────────────────▼──────────────────────────────────┐
│                  ms-platform-auth (Spring Boot)              │
│  ┌────────────────────────────────────────────────────────┐ │
│  │           Controllers (REST API Endpoints)             │ │
│  └─────────────────────────┬──────────────────────────────┘ │
│                            │                                 │
│  ┌─────────────────────────▼──────────────────────────────┐ │
│  │        Spring Security + OAuth2 Resource Server        │ │
│  │    (JWT Validation, Authorization, Role-Based Access)  │ │
│  └─────────────────────────┬──────────────────────────────┘ │
│                            │                                 │
│  ┌─────────────────────────▼──────────────────────────────┐ │
│  │              Service Layer (Business Logic)            │ │
│  └─────────────────────────┬──────────────────────────────┘ │
│                            │                                 │
│  ┌─────────────────────────▼──────────────────────────────┐ │
│  │         Repository Layer (Spring Data JPA)             │ │
│  └─────────────────────────┬──────────────────────────────┘ │
└────────────────────────────┼────────────────────────────────┘
                             │
                             │ JDBC
                             │
┌────────────────────────────▼────────────────────────────────┐
│                   PostgreSQL Database                        │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Tables: users, ...                                  │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                 Keycloak (Identity Provider)                 │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  - Realms & Clients                                  │  │
│  │  - User Management                                   │  │
│  │  - Role & Permission Management                      │  │
│  │  - JWT Token Generation & Validation                 │  │
│  │  - OAuth2 / OpenID Connect                           │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## Componentes Principais

### 1. Camada de Apresentação (Controllers)

**Responsabilidades:**
- Expor endpoints REST
- Validar requisições HTTP
- Retornar respostas formatadas (JSON)

**Componentes:**
- `AuthController.kt` - Endpoints de autenticação e informações do usuário

**Endpoints:**
- `GET /api/public/health` - Health check (público)
- `GET /api/protected` - Endpoint protegido de exemplo
- `GET /api/user/info` - Informações do usuário autenticado

### 2. Camada de Segurança

**Responsabilidades:**
- Autenticação via JWT
- Autorização baseada em roles
- Validação de tokens
- Proteção de endpoints

**Componentes:**
- `SecurityConfig.kt` - Configuração do Spring Security
- OAuth2 Resource Server - Validação de JWT
- JWT Authentication Converter - Extração de roles do token

**Fluxo de Autenticação:**
1. Cliente obtém JWT do Keycloak
2. Cliente envia requisição com token no header `Authorization: Bearer <token>`
3. Spring Security valida o token com Keycloak (via JWK Set)
4. Extrai roles e permissões do token
5. Permite ou nega acesso baseado nas configurações de autorização

### 3. Camada de Modelo (Entities)

**Responsabilidades:**
- Representar estruturas de dados
- Mapear tabelas do banco de dados
- Definir relacionamentos entre entidades

**Componentes:**
- `User.kt` - Entidade de usuário

**Estrutura da Tabela `users`:**
```sql
- id: BIGSERIAL (Primary Key)
- username: VARCHAR(255) UNIQUE NOT NULL
- email: VARCHAR(255) UNIQUE NOT NULL
- keycloak_id: VARCHAR(255) UNIQUE
- created_at: TIMESTAMP NOT NULL
- updated_at: TIMESTAMP NOT NULL
```

### 4. Camada de Repositório

**Responsabilidades:**
- Acesso ao banco de dados
- Operações CRUD
- Queries customizadas

**Componentes:**
- `UserRepository.kt` - Repository JPA para User

**Métodos Disponíveis:**
- `findByUsername(username: String)`
- `findByEmail(email: String)`
- `findByKeycloakId(keycloakId: String)`
- Todos os métodos padrão do JpaRepository

### 5. Banco de Dados (PostgreSQL)

**Responsabilidades:**
- Persistência de dados
- Transações ACID
- Indexação e performance

**Configuração:**
- Host: `localhost` (default) ou configurável via `DB_HOST`
- Porta: `5432` (default) ou configurável via `DB_PORT`
- Database: `authdb`
- Usuário: `postgres`
- Senha: `postgres`

### 6. Keycloak (IAM)

**Responsabilidades:**
- Gerenciamento de identidade
- Autenticação de usuários
- Geração e validação de JWT
- Single Sign-On (SSO)
- Gerenciamento de roles e permissões

**Configuração:**
- URL: `http://localhost:8080`
- Realm: `platform`
- Client ID: `platform-auth`
- Admin Console: `http://localhost:8080`
  - Username: `admin`
  - Password: `admin`

## Fluxo de Autenticação Completo

```
1. Usuário → Keycloak: POST /realms/platform/protocol/openid-connect/token
   Body: username, password, client_id, client_secret
   
2. Keycloak valida credenciais
   
3. Keycloak → Usuário: JWT Token (access_token + refresh_token)

4. Usuário → ms-platform-auth: GET /api/protected
   Header: Authorization: Bearer <access_token>
   
5. ms-platform-auth → Keycloak: Valida JWT (via JWK Set)

6. Keycloak → ms-platform-auth: Token válido + Claims

7. ms-platform-auth: Verifica autorização (roles, permissions)

8. ms-platform-auth → Usuário: Resposta (200 OK ou 403 Forbidden)
```

## Estrutura JWT Token

Um token JWT típico contém:

```json
{
  "exp": 1697561234,
  "iat": 1697560934,
  "jti": "uuid",
  "iss": "http://localhost:8080/realms/platform",
  "sub": "user-uuid",
  "typ": "Bearer",
  "azp": "platform-auth",
  "session_state": "uuid",
  "realm_access": {
    "roles": ["user", "admin"]
  },
  "scope": "openid profile email",
  "email_verified": true,
  "preferred_username": "admin",
  "email": "admin@example.com"
}
```

## Perfis de Execução

### Perfil Padrão (sem Keycloak)
```bash
./gradlew bootRun
```
- Apenas Resource Server (validação JWT)
- Não registra OAuth2 Client

### Perfil com Keycloak
```bash
./gradlew bootRun --args='--spring.profiles.active=with-keycloak'
```
- Resource Server + OAuth2 Client
- Suporte completo a flows OAuth2

## Segurança

### Recursos de Segurança Implementados:

1. **Autenticação JWT** - Tokens assinados e validados
2. **Autorização Baseada em Roles** - RBAC (Role-Based Access Control)
3. **Stateless Sessions** - Sem estado no servidor
4. **CSRF Desabilitado** - Adequado para APIs REST
5. **CORS Configurável** - Controle de origens permitidas
6. **Endpoints Públicos** - Health checks acessíveis
7. **Endpoints Protegidos** - Requerem autenticação

### Recomendações para Produção:

1. ✅ Usar HTTPS em todas as comunicações
2. ✅ Rotacionar secrets regularmente
3. ✅ Implementar rate limiting
4. ✅ Habilitar logs de auditoria
5. ✅ Configurar timeouts adequados
6. ✅ Usar variáveis de ambiente para configurações sensíveis
7. ✅ Implementar monitoramento e alertas

## Escalabilidade

O serviço foi projetado para ser escalável:

- **Stateless** - Pode ser replicado horizontalmente
- **Docker** - Fácil deploy em clusters (Kubernetes, Docker Swarm)
- **Connection Pool** - HikariCP para gerenciamento eficiente de conexões
- **Caching** - Pode adicionar Redis/Memcached para cache de tokens

## Monitoramento

Endpoints disponíveis para monitoramento:

- `/api/public/health` - Health check básico
- Spring Boot Actuator pode ser adicionado para métricas avançadas

## Tecnologias e Ferramentas

| Componente | Tecnologia | Versão |
|-----------|-----------|---------|
| Linguagem | Kotlin | 1.8.22 |
| Framework | Spring Boot | 3.1.5 |
| Segurança | Spring Security | (via Spring Boot) |
| Banco de Dados | PostgreSQL | 15 |
| IAM | Keycloak | 22.0.5 |
| Build | Gradle | 8.4 |
| JDK | OpenJDK | 17 |
| Container | Docker | Latest |

## Scripts Auxiliares

O projeto inclui scripts para facilitar o desenvolvimento:

### `scripts/get-token.sh`
Obtém JWT token do Keycloak
```bash
./scripts/get-token.sh admin admin123
```

### `scripts/test-api.sh`
Testa os endpoints da API
```bash
./scripts/test-api.sh [optional-jwt-token]
```

## Próximos Passos

Funcionalidades que podem ser adicionadas:

- [ ] Integração com Spring Boot Actuator
- [ ] Cache com Redis
- [ ] Rate Limiting
- [ ] API Gateway integration
- [ ] Logs estruturados (ELK Stack)
- [ ] Métricas com Prometheus/Grafana
- [ ] Testes de integração
- [ ] CI/CD pipeline
- [ ] Swagger/OpenAPI documentation
- [ ] Multi-tenancy support
