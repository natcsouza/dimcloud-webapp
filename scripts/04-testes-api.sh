#!/usr/bin/env bash
# =====================================================================
# DimCloud - testa o CRUD das duas tabelas pela API JSON com curl
# (GET, POST, PUT e DELETE). O front-end faz o mesmo pelas telas.
# Uso: ./04-testes-api.sh   (ou BASE=https://... ./04-testes-api.sh)
# =====================================================================
set -euo pipefail
cd "$(dirname "$0")/.."
SQL_ADMIN_PASSWORD="nao-usada" source ./scripts/00-variaveis.sh
unset MSYS_NO_PATHCONV   # aqui não tem az; no Git Bash o curl precisa da conversão para /dev/null
BASE="${BASE:-https://${WEBAPP}.azurewebsites.net}"
H='Content-Type: application/json'

echo "== POST correntista";   C=$(curl -s -X POST "$BASE/api/correntistas" -H "$H" -d @api-json/correntista-post.json); echo "$C"
CID=$(echo "$C" | sed -E 's/.*"id":([0-9]+).*/\1/')
echo "== GET correntistas";   curl -s "$BASE/api/correntistas"; echo
echo "== PUT correntista $CID"; curl -s -X PUT "$BASE/api/correntistas/$CID" -H "$H" -d @api-json/correntista-put.json; echo

echo "== POST transacao";     T=$(sed "s/\"correntistaId\": 1/\"correntistaId\": $CID/" api-json/transacao-post.json | curl -s -X POST "$BASE/api/transacoes" -H "$H" -d @-); echo "$T"
TID=$(echo "$T" | sed -E 's/.*"id":([0-9]+).*/\1/')
echo "== GET transacoes";     curl -s "$BASE/api/transacoes"; echo
echo "== PUT transacao $TID"; sed "s/\"correntistaId\": 1/\"correntistaId\": $CID/" api-json/transacao-put.json | curl -s -X PUT "$BASE/api/transacoes/$TID" -H "$H" -d @-; echo
echo "== GET extrato";        curl -s "$BASE/api/correntistas/$CID/transacoes"; echo

echo "== DELETE transacao $TID";   curl -s -o /dev/null -w "HTTP %{http_code}\n" -X DELETE "$BASE/api/transacoes/$TID"
echo "== DELETE correntista $CID"; curl -s -o /dev/null -w "HTTP %{http_code}\n" -X DELETE "$BASE/api/correntistas/$CID"
