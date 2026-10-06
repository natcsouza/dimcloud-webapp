#!/usr/bin/env bash
# =====================================================================
# DimCloud - executa o DDL (ddl.sql) no Azure SQL Database com sqlcmd.
# Sem sqlcmd? Portal > banco dimclouddb > Query editor > cole ddl.sql > Run.
# =====================================================================
set -euo pipefail
cd "$(dirname "$0")"
source ./00-variaveis.sh
garantir_sqlcmd
liberar_meu_ip

sqlcmd -S "tcp:${SQL_SERVER}.database.windows.net,1433" -d "$SQL_DB" \
  -U "$SQL_ADMIN" -P "$SQL_ADMIN_PASSWORD" -N -l 60 -i ddl.sql

echo ">> Tabelas criadas:"
sqlcmd -S "tcp:${SQL_SERVER}.database.windows.net,1433" -d "$SQL_DB" \
  -U "$SQL_ADMIN" -P "$SQL_ADMIN_PASSWORD" -N -l 60 \
  -Q "SET NOCOUNT ON; SELECT name AS tabela FROM sys.tables ORDER BY name"
