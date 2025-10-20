# ms-platform-auth
Sistema de Authenticação

## Pré-requisitos

- Java 21
- Docker e Docker Compose
- Gradle (ou use o wrapper incluído `./gradlew`)

## Configuração

### 1. Configurar Variáveis de Ambiente

Copie o arquivo `.env.example` para `.env` e ajuste os valores conforme necessário:

```bash
cp .env.example .env
```

### 2. Iniciar os Serviços com Docker Compose

O projeto utiliza Docker Compose para subir o PostgreSQL e o Keycloak:

```bash
docker-compose up -d
```

Isso irá iniciar:
- PostgreSQL na porta 5432 (banco de dados da aplicação)
- PostgreSQL na porta 5433 (banco de dados do Keycloak)
- Keycloak na porta 8083

### 3. Executar a Aplicação

```bash
./gradlew bootRun
```

A aplicação estará disponível em: `http://localhost:8080`

## Testes

Os testes utilizam um banco de dados H2 em memória, não sendo necessário subir o Docker Compose:

```bash
./gradlew test
```

## Build

Para compilar o projeto:

```bash
./gradlew build
```

## Acesso ao Keycloak

- URL: http://localhost:8083
- Usuário: admin (conforme definido no .env)
- Senha: admin (conforme definido no .env)

## Estrutura do Projeto

- `src/main/kotlin` - Código fonte da aplicação
- `src/main/resources/application.yml` - Configuração da aplicação
- `src/test/kotlin` - Testes
- `src/test/resources/application-test.yml` - Configuração para testes
- `docker-compose.yml` - Configuração dos serviços Docker

