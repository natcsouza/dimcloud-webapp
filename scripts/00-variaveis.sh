#!/usr/bin/env bash
# =====================================================================
# DimCloud / DimDim - variáveis usadas por todos os scripts do Azure CLI
# Os outros scripts fazem "source" deste arquivo.
# Nomes de SQL Server e Web App precisam ser únicos no mundo: por isso
# levam o RM de quem cria os recursos (troque com: export RM=rmXXXXXX).
# =====================================================================

export RM="${RM:-rm564099}"
export LOCATION="${LOCATION:-mexicocentral}"   # Azure for Students (FIAP) bloqueia brazilsouth

export RG="rg-dimcloud-webapp"
export SQL_SERVER="sql-dimcloud-${RM}"
export SQL_DB="dimclouddb"
export SQL_ADMIN="dimcloudadmin"
export PLAN="plan-dimcloud"
export WEBAPP="app-dimcloud-${RM}"
export LAW="law-dimcloud"                      # Log Analytics (base do App Insights e logs do banco)
export APPINSIGHTS="appi-dimcloud"
export RUNTIME="JAVA:21-java21"                # Java SE 21 (Spring Boot jar)

# A senha do banco NUNCA fica no código nem no GitHub:
# vem da variável de ambiente SQL_ADMIN_PASSWORD ou é pedida no terminal.
if [ -z "${SQL_ADMIN_PASSWORD:-}" ]; then
  read -r -s -p "Senha do admin do Azure SQL (mín. 8 chars, maiúscula, minúscula, número e símbolo): " SQL_ADMIN_PASSWORD
  echo
fi
export SQL_ADMIN_PASSWORD
