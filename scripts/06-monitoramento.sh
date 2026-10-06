#!/usr/bin/env bash
# =====================================================================
# DimCloud - evidências do monitoramento pelo Azure CLI:
#   Application Insights (requisições e comandos SQL como dependências)
#   e métricas do Azure SQL Database.
# A telemetria leva de 1 a 3 minutos para aparecer depois do uso.
# =====================================================================
set -euo pipefail
cd "$(dirname "$0")"
SQL_ADMIN_PASSWORD="nao-usada" source ./00-variaveis.sh
az extension add --name application-insights --only-show-errors 2>/dev/null || true

kql() {
  az monitor app-insights query -g "$RG" --app "$APPINSIGHTS" --offset 2h \
    --analytics-query "$1" \
    --query "tables[0].rows" -o tsv
}

echo "== Application Insights: requisições por rota (últimas 2h)"
kql "requests | summarize qtd=count(), media_ms=round(avg(duration),1) by name | order by qtd desc"

echo
echo "== Application Insights: transações no banco (dependências SQL)"
kql "dependencies | where type == 'SQL' | project timestamp, target, comando=substring(data,0,70), duration=round(duration,1), success | order by timestamp desc | take 15"

echo
echo "== Application Insights: comandos SQL por tipo"
kql "dependencies | where type == 'SQL' | extend op=toupper(extract(@'^\s*(\w+)', 1, data)) | summarize qtd=count() by op"

echo
echo "== Azure SQL Database: métricas da última hora (Metrics do banco)"
DB_ID=$(az sql db show -g "$RG" -s "$SQL_SERVER" -n "$SQL_DB" --query id -o tsv)
az monitor metrics list --resource "$DB_ID" \
  --metric connection_successful dtu_consumption_percent cpu_percent \
  --interval PT5M --offset 1h --aggregation Maximum \
  --query "value[].{metrica:name.value, ultimos_30min_max_por_5min:join(', ', timeseries[0].data[-6:].to_string(maximum))}" -o table
