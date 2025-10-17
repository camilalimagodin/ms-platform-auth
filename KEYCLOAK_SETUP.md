# Guia de Configuração do Keycloak

Este guia fornece instruções detalhadas para configurar o Keycloak para funcionar com o ms-platform-auth.

## Iniciando o Keycloak

### Opção 1: Usando Docker Compose (Recomendado)

```bash
# Iniciar apenas PostgreSQL e Keycloak
docker compose up -d postgres keycloak

# Verificar os logs do Keycloak
docker compose logs -f keycloak

# Aguardar até ver a mensagem: "Keycloak ... started"
```

### Opção 2: Usando Docker diretamente

```bash
docker run -d \
  --name keycloak \
  -p 8080:8080 \
  -e KEYCLOAK_ADMIN=admin \
  -e KEYCLOAK_ADMIN_PASSWORD=admin \
  quay.io/keycloak/keycloak:22.0.5 \
  start-dev
```

## Configurando o Keycloak

### 1. Acessar o Console Administrativo

1. Abra o navegador e acesse: http://localhost:8080
2. Clique em "Administration Console"
3. Faça login com:
   - **Username**: admin
   - **Password**: admin

### 2. Criar um Novo Realm

1. No menu lateral esquerdo, passe o mouse sobre "Master" e clique no botão "+" ou "Create Realm"
2. Preencha:
   - **Realm name**: `platform`
3. Clique em "Create"

### 3. Criar um Cliente (Client)

1. No menu lateral, clique em "Clients"
2. Clique em "Create client"
3. Na página "General Settings":
   - **Client type**: OpenID Connect
   - **Client ID**: `platform-auth`
   - Clique em "Next"
4. Na página "Capability config":
   - Habilite "Client authentication"
   - Habilite "Authorization"
   - Deixe marcado "Standard flow"
   - Deixe marcado "Direct access grants"
   - Clique em "Next"
5. Na página "Login settings":
   - **Valid redirect URIs**: 
     - `http://localhost:8081/*`
     - `http://localhost:8081/login/oauth2/code/*`
   - **Valid post logout redirect URIs**: `http://localhost:8081/*`
   - **Web origins**: `http://localhost:8081`
   - Clique em "Save"

### 4. Obter o Client Secret

1. Após criar o cliente, clique na aba "Credentials"
2. Copie o valor do "Client secret" (você precisará dele para configurar a aplicação)

### 5. Criar Roles (Funções)

1. No menu lateral, clique em "Realm roles"
2. Clique em "Create role"
3. Crie as seguintes roles:
   - **Role name**: `user` (descrição: "Usuário comum")
   - **Role name**: `admin` (descrição: "Administrador")
   - **Role name**: `manager` (descrição: "Gerente")

### 6. Criar Usuários de Teste

#### Criar Usuário Administrador

1. No menu lateral, clique em "Users"
2. Clique em "Create new user"
3. Preencha:
   - **Username**: `admin`
   - **Email**: `admin@example.com`
   - **First name**: `Admin`
   - **Last name**: `User`
   - **Email verified**: ON
4. Clique em "Create"
5. Clique na aba "Credentials"
6. Clique em "Set password"
7. Preencha:
   - **Password**: `admin123`
   - **Password confirmation**: `admin123`
   - **Temporary**: OFF
8. Clique em "Save"
9. Clique na aba "Role mapping"
10. Clique em "Assign role"
11. Selecione as roles: `admin`, `user`
12. Clique em "Assign"

#### Criar Usuário Comum

1. Repita o processo anterior com:
   - **Username**: `user`
   - **Email**: `user@example.com`
   - **First name**: `Test`
   - **Last name**: `User`
   - **Password**: `user123`
   - **Roles**: `user`

## Configurar a Aplicação

### Atualizar as Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto ou exporte as variáveis:

```bash
# Keycloak Configuration
export KEYCLOAK_URL=http://localhost:8080
export KEYCLOAK_ISSUER_URI=http://localhost:8080/realms/platform
export KEYCLOAK_JWK_SET_URI=http://localhost:8080/realms/platform/protocol/openid-connect/certs
export KEYCLOAK_AUTH_URI=http://localhost:8080/realms/platform/protocol/openid-connect/auth
export KEYCLOAK_TOKEN_URI=http://localhost:8080/realms/platform/protocol/openid-connect/token
export KEYCLOAK_USERINFO_URI=http://localhost:8080/realms/platform/protocol/openid-connect/userinfo
export KEYCLOAK_CLIENT_ID=platform-auth
export KEYCLOAK_CLIENT_SECRET=seu_client_secret_aqui
export KEYCLOAK_REALM=platform
```

### Executar a Aplicação com Keycloak

```bash
# Com Docker Compose (tudo junto)
docker compose up -d

# OU localmente com PostgreSQL e Keycloak no Docker
docker compose up -d postgres keycloak
./gradlew bootRun --args='--spring.profiles.active=with-keycloak'
```

## Testar a Integração

### 1. Obter um Token JWT

Use o endpoint de token do Keycloak para obter um JWT:

```bash
curl -X POST 'http://localhost:8080/realms/platform/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=password' \
  -d 'client_id=platform-auth' \
  -d 'client_secret=SEU_CLIENT_SECRET' \
  -d 'username=admin' \
  -d 'password=admin123' | jq .
```

Resposta esperada:
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI...",
  "expires_in": 300,
  "refresh_expires_in": 1800,
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI...",
  "token_type": "Bearer",
  "not-before-policy": 0,
  "session_state": "...",
  "scope": "profile email"
}
```

### 2. Usar o Token para Acessar Endpoints Protegidos

Copie o valor do `access_token` da resposta anterior e use-o para fazer requisições:

```bash
# Salvar o token em uma variável
TOKEN="cole_seu_access_token_aqui"

# Acessar endpoint protegido
curl -X GET 'http://localhost:8081/api/protected' \
  -H "Authorization: Bearer $TOKEN"

# Obter informações do usuário
curl -X GET 'http://localhost:8081/api/user/info' \
  -H "Authorization: Bearer $TOKEN" | jq .
```

### 3. Testar sem Token (deve retornar 401)

```bash
curl -v http://localhost:8081/api/protected
# Deve retornar HTTP 401 Unauthorized
```

### 4. Testar Endpoint Público (não requer autenticação)

```bash
curl http://localhost:8081/api/public/health | jq .
# Deve retornar: {"status": "UP", "service": "ms-platform-auth"}
```

## Solução de Problemas

### Erro: "Connection refused" ao conectar com Keycloak

- Verifique se o Keycloak está rodando: `docker compose ps`
- Aguarde alguns segundos para o Keycloak inicializar completamente
- Verifique os logs: `docker compose logs keycloak`

### Erro: "Invalid client credentials"

- Verifique se o `client_secret` está correto
- Confirme que o client ID é `platform-auth`
- Verifique se o client authentication está habilitado

### Token expirado

Os tokens JWT expiram após 5 minutos por padrão. Para obter um novo token:

```bash
# Usando refresh token
curl -X POST 'http://localhost:8080/realms/platform/protocol/openid-connect/token' \
  -H 'Content-Type: application/x-www-form-urlencoded' \
  -d 'grant_type=refresh_token' \
  -d 'client_id=platform-auth' \
  -d 'client_secret=SEU_CLIENT_SECRET' \
  -d 'refresh_token=SEU_REFRESH_TOKEN'
```

## Configurações Avançadas

### Personalizar o Tempo de Expiração do Token

1. No Keycloak, vá para o realm "platform"
2. Clique em "Realm settings" > "Tokens"
3. Ajuste:
   - **Access Token Lifespan**: 5 minutes (padrão)
   - **Refresh Token Max Lifespan**: 30 minutes

### Habilitar HTTPS

Para produção, é altamente recomendado usar HTTPS:

1. Configure um certificado SSL
2. Atualize o Keycloak para usar HTTPS
3. Atualize as URLs na configuração da aplicação

### Adicionar Claims Customizados

1. No Keycloak, vá para "Clients" > "platform-auth"
2. Clique na aba "Client scopes"
3. Crie um novo scope ou edite um existente
4. Adicione mappers para incluir claims customizados no JWT

## Referências

- [Documentação Oficial do Keycloak](https://www.keycloak.org/documentation)
- [Spring Security OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)
- [Keycloak Admin REST API](https://www.keycloak.org/docs/latest/server_admin/index.html#admin-rest-api)
