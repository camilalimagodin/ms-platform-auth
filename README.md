# ms-platform-auth
Sistema de Authenticação

## Descrição
Microserviço de autenticação desenvolvido com Spring Boot e Kotlin, utilizando PostgreSQL como banco de dados, Spring Security para segurança e Keycloak para gerenciamento de identidade e acesso.

## Tecnologias Utilizadas
- **Spring Boot 3.1.5** - Framework principal
- **Kotlin 1.9.20** - Linguagem de programação
- **PostgreSQL 15** - Banco de dados
- **Spring Security** - Framework de segurança
- **Keycloak 22.0.5** - Servidor de autenticação e autorização
- **Docker & Docker Compose** - Containerização
- **Gradle** - Gerenciamento de dependências e build

## Pré-requisitos
- JDK 17 ou superior
- Docker e Docker Compose
- Gradle (opcional, o wrapper está incluído)

## Executando o Projeto

### Com Docker Compose (Recomendado)
O projeto inclui um arquivo `docker-compose.yml` que configura todos os serviços necessários:

```bash
# Iniciar todos os serviços
docker-compose up -d

# Verificar logs
docker-compose logs -f

# Parar os serviços
docker-compose down
```

### Localmente (Desenvolvimento)
1. Inicie o PostgreSQL e Keycloak:
```bash
docker-compose up -d postgres keycloak
```

2. Aguarde os serviços iniciarem (cerca de 1-2 minutos)

3. Execute a aplicação:
```bash
./gradlew bootRun
```

## Configuração do Keycloak

Após iniciar o Keycloak, acesse: http://localhost:8080

1. Faça login com as credenciais:
   - **Username**: admin
   - **Password**: admin

2. Crie um novo realm chamado `platform`

3. No realm `platform`, crie um cliente:
   - **Client ID**: platform-auth
   - **Client Protocol**: openid-connect
   - **Access Type**: confidential
   - Anote o **Client Secret** gerado

4. Configure o cliente:
   - Valid Redirect URIs: `http://localhost:8081/*`
   - Web Origins: `http://localhost:8081`

5. Crie usuários de teste no realm

## Endpoints da API

### Públicos
- `GET /api/public/health` - Health check do serviço

### Protegidos (Requer autenticação)
- `GET /api/user/info` - Informações do usuário autenticado
- `GET /api/protected` - Endpoint protegido de exemplo

## Testando a Aplicação

### Executar testes
```bash
./gradlew test
```

### Obter token JWT do Keycloak
```bash
curl -X POST 'http://localhost:8080/realms/platform/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=password' \
  -d 'client_id=platform-auth' \
  -d 'client_secret=YOUR_CLIENT_SECRET' \
  -d 'username=YOUR_USERNAME' \
  -d 'password=YOUR_PASSWORD'
```

### Acessar endpoint protegido
```bash
curl -X GET 'http://localhost:8081/api/protected' \
  -H 'Authorization: Bearer YOUR_JWT_TOKEN'
```

## Estrutura do Projeto
```
ms-platform-auth/
├── src/
│   ├── main/
│   │   ├── kotlin/
│   │   │   └── com/platform/auth/
│   │   │       ├── config/          # Configurações de segurança
│   │   │       ├── controller/      # Controladores REST
│   │   │       ├── model/           # Entidades JPA
│   │   │       ├── repository/      # Repositórios JPA
│   │   │       └── MsPlatformAuthApplication.kt
│   │   └── resources/
│   │       └── application.yml      # Configurações da aplicação
│   └── test/
│       └── kotlin/
│           └── com/platform/auth/
├── build.gradle.kts                 # Configuração do Gradle
├── docker-compose.yml               # Configuração do Docker Compose
├── Dockerfile                       # Dockerfile da aplicação
└── README.md
```

## Variáveis de Ambiente
As seguintes variáveis podem ser configuradas:

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| DB_HOST | Host do PostgreSQL | localhost |
| DB_PORT | Porta do PostgreSQL | 5432 |
| DB_NAME | Nome do banco de dados | authdb |
| DB_USER | Usuário do banco | postgres |
| DB_PASSWORD | Senha do banco | postgres |
| KEYCLOAK_URL | URL do Keycloak | http://localhost:8080 |
| KEYCLOAK_REALM | Realm do Keycloak | platform |
| KEYCLOAK_CLIENT_ID | ID do cliente | platform-auth |
| KEYCLOAK_CLIENT_SECRET | Secret do cliente | secret |
| SERVER_PORT | Porta da aplicação | 8081 |

## Build da Aplicação
```bash
# Build com Gradle
./gradlew build

# Build com Docker
docker build -t ms-platform-auth .
```

## Licença
Este projeto é de código aberto.
