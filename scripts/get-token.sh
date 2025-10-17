#!/bin/bash

# Script para obter JWT token do Keycloak
# Uso: ./get-token.sh [username] [password]

KEYCLOAK_URL="${KEYCLOAK_URL:-http://localhost:8080}"
REALM="${KEYCLOAK_REALM:-platform}"
CLIENT_ID="${KEYCLOAK_CLIENT_ID:-platform-auth}"
CLIENT_SECRET="${KEYCLOAK_CLIENT_SECRET:-secret}"

USERNAME="${1:-admin}"
PASSWORD="${2:-admin123}"

echo "Obtendo token para usuário: $USERNAME"
echo "Keycloak URL: $KEYCLOAK_URL"
echo "Realm: $REALM"
echo ""

RESPONSE=$(curl -s -X POST "$KEYCLOAK_URL/realms/$REALM/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=$CLIENT_ID" \
  -d "client_secret=$CLIENT_SECRET" \
  -d "username=$USERNAME" \
  -d "password=$PASSWORD")

# Verificar se obteve o token
if echo "$RESPONSE" | grep -q "access_token"; then
    echo "✅ Token obtido com sucesso!"
    echo ""
    
    # Extrair e mostrar o access_token
    ACCESS_TOKEN=$(echo "$RESPONSE" | grep -o '"access_token":"[^"]*' | cut -d'"' -f4)
    echo "Access Token:"
    echo "$ACCESS_TOKEN"
    echo ""
    
    # Salvar em arquivo
    echo "$ACCESS_TOKEN" > /tmp/jwt-token.txt
    echo "Token salvo em: /tmp/jwt-token.txt"
    echo ""
    
    # Mostrar comando de exemplo
    echo "Para usar o token:"
    echo "export TOKEN=\"$ACCESS_TOKEN\""
    echo 'curl -H "Authorization: Bearer $TOKEN" http://localhost:8081/api/protected'
    echo ""
    
    # Ou usar diretamente do arquivo
    echo "Ou carregar do arquivo:"
    echo 'export TOKEN=$(cat /tmp/jwt-token.txt)'
    
else
    echo "❌ Erro ao obter token:"
    echo "$RESPONSE" | jq .
fi
