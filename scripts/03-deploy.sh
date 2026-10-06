#!/usr/bin/env bash
# =====================================================================
# DimCloud - build (Maven) + deploy automatizado com "az webapp deploy"
# (tipo jar) no Azure Web App. Usa só a sessão do "az login".
# =====================================================================
set -euo pipefail
cd "$(dirname "$0")/.."
SQL_ADMIN_PASSWORD="nao-usada-no-deploy" source ./scripts/00-variaveis.sh

echo ">> mvn package"
# (env -u: no Git Bash, a conversão de caminho desligada para o az quebra o classpath do Maven)
env -u MSYS_NO_PATHCONV mvn -q -DskipTests clean package

echo ">> az webapp deploy (jar)"
az webapp deploy \
  --resource-group "$RG" --name "$WEBAPP" \
  --src-path target/dimcloud.jar --type jar --restart true

echo
echo "Deploy concluído: https://${WEBAPP}.azurewebsites.net"
