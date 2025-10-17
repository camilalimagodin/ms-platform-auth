#!/bin/bash

# Script para testar os endpoints da API
# Uso: ./test-api.sh [token]

BASE_URL="${BASE_URL:-http://localhost:8081}"
TOKEN="${1}"

echo "======================================"
echo "Testando API: $BASE_URL"
echo "======================================"
echo ""

# Teste 1: Health endpoint (público)
echo "1. Testando endpoint público /api/public/health"
echo "   Comando: curl $BASE_URL/api/public/health"
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" "$BASE_URL/api/public/health")
HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE:/d')

echo "   Status: $HTTP_CODE"
echo "   Resposta: $BODY"
if [ "$HTTP_CODE" = "200" ]; then
    echo "   ✅ PASSOU"
else
    echo "   ❌ FALHOU"
fi
echo ""

# Teste 2: Protected endpoint sem token
echo "2. Testando endpoint protegido sem token"
echo "   Comando: curl $BASE_URL/api/protected"
RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" "$BASE_URL/api/protected")
HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)

echo "   Status: $HTTP_CODE"
if [ "$HTTP_CODE" = "401" ]; then
    echo "   ✅ PASSOU (401 esperado)"
else
    echo "   ❌ FALHOU (esperado 401, recebeu $HTTP_CODE)"
fi
echo ""

# Teste 3: Protected endpoint com token
if [ -n "$TOKEN" ]; then
    echo "3. Testando endpoint protegido com token"
    echo "   Comando: curl -H 'Authorization: Bearer TOKEN' $BASE_URL/api/protected"
    RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -H "Authorization: Bearer $TOKEN" "$BASE_URL/api/protected")
    HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
    BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE:/d')
    
    echo "   Status: $HTTP_CODE"
    echo "   Resposta: $BODY"
    if [ "$HTTP_CODE" = "200" ]; then
        echo "   ✅ PASSOU"
    else
        echo "   ❌ FALHOU"
    fi
    echo ""
    
    # Teste 4: User info endpoint
    echo "4. Testando endpoint /api/user/info com token"
    echo "   Comando: curl -H 'Authorization: Bearer TOKEN' $BASE_URL/api/user/info"
    RESPONSE=$(curl -s -w "\nHTTP_CODE:%{http_code}" -H "Authorization: Bearer $TOKEN" "$BASE_URL/api/user/info")
    HTTP_CODE=$(echo "$RESPONSE" | grep "HTTP_CODE:" | cut -d: -f2)
    BODY=$(echo "$RESPONSE" | sed '/HTTP_CODE:/d')
    
    echo "   Status: $HTTP_CODE"
    echo "   Resposta: $BODY"
    if [ "$HTTP_CODE" = "200" ]; then
        echo "   ✅ PASSOU"
    else
        echo "   ❌ FALHOU"
    fi
    echo ""
else
    echo "3. ⏭️  Pulando testes com token (nenhum token fornecido)"
    echo "   Para testar com token, use: ./test-api.sh YOUR_TOKEN"
    echo "   Ou obtenha um token com: ./get-token.sh"
    echo ""
fi

echo "======================================"
echo "Resumo dos Testes"
echo "======================================"
