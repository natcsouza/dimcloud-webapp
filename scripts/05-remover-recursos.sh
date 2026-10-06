#!/usr/bin/env bash
# DimCloud - apaga tudo (Resource Group inteiro) depois da avaliação.
set -euo pipefail
cd "$(dirname "$0")"
SQL_ADMIN_PASSWORD="nao-usada" source ./00-variaveis.sh
az group delete --name "$RG" --yes --no-wait
echo "Remoção do $RG solicitada."
