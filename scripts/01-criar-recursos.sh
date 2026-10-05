#!/usr/bin/env bash
# =====================================================================
# DimCloud - cria TODA a infraestrutura na Azure via Azure CLI:
#   Resource Group, Azure SQL Database (PaaS, não containerizado),
#   Log Analytics + Application Insights, diagnóstico do banco,
#   App Service Plan Linux + Web App Java 21 e as App Settings.
# =====================================================================
set -euo pipefail
cd "$(dirname "$0")"
source ./00-variaveis.sh

echo ">> Registrando os resource providers (necessário em assinatura nova)"
for ns in Microsoft.Web Microsoft.Sql Microsoft.Insights Microsoft.OperationalInsights; do
  az provider register --namespace "$ns" --wait
done
az extension add --name application-insights --upgrade --only-show-errors

echo ">> Resource Group"
az group create --name "$RG" --location "$LOCATION" -o table

echo ">> Azure SQL Server + banco (Basic, 5 DTU)"
az sql server create \
  --name "$SQL_SERVER" --resource-group "$RG" --location "$LOCATION" \
  --admin-user "$SQL_ADMIN" --admin-password "$SQL_ADMIN_PASSWORD" -o table

az sql db create \
  --resource-group "$RG" --server "$SQL_SERVER" --name "$SQL_DB" \
  --edition Basic --capacity 5 --backup-storage-redundancy Local -o table

echo ">> Firewall: libera serviços da Azure (o Web App) e o IP desta máquina (DDL e consultas)"
az sql server firewall-rule create \
  --resource-group "$RG" --server "$SQL_SERVER" --name AllowAzureServices \
  --start-ip-address 0.0.0.0 --end-ip-address 0.0.0.0 -o table

MEU_IP=$(curl -s https://api.ipify.org)
az sql server firewall-rule create \
  --resource-group "$RG" --server "$SQL_SERVER" --name MeuIP \
  --start-ip-address "$MEU_IP" --end-ip-address "$MEU_IP" -o table

echo ">> Log Analytics + Application Insights (workspace-based)"
az monitor log-analytics workspace create \
  --resource-group "$RG" --workspace-name "$LAW" --location "$LOCATION" -o table
LAW_ID=$(az monitor log-analytics workspace show -g "$RG" -n "$LAW" --query id -o tsv)

az monitor app-insights component create \
  --app "$APPINSIGHTS" --resource-group "$RG" --location "$LOCATION" \
  --kind web --application-type web --workspace "$LAW_ID" -o table
AI_CONN=$(az monitor app-insights component show -g "$RG" --app "$APPINSIGHTS" --query connectionString -o tsv)

echo ">> Diagnóstico do Azure SQL -> Log Analytics (monitoramento do banco)"
DB_ID=$(az sql db show -g "$RG" -s "$SQL_SERVER" -n "$SQL_DB" --query id -o tsv)
az monitor diagnostic-settings create \
  --name diag-dimclouddb --resource "$DB_ID" --workspace "$LAW_ID" \
  --logs '[{"category":"SQLInsights","enabled":true},{"category":"QueryStoreRuntimeStatistics","enabled":true},{"category":"Errors","enabled":true},{"category":"DatabaseWaitStatistics","enabled":true}]' \
  --metrics '[{"category":"Basic","enabled":true}]' -o none

echo ">> App Service Plan (Linux B1) + Web App Java 21"
az appservice plan create \
  --name "$PLAN" --resource-group "$RG" --location "$LOCATION" \
  --sku B1 --is-linux -o table

az webapp create \
  --name "$WEBAPP" --resource-group "$RG" --plan "$PLAN" \
  --runtime "$RUNTIME" -o table

echo ">> App Settings: banco + Application Insights (ficam no Web App, não no código)"
JDBC_URL="jdbc:sqlserver://${SQL_SERVER}.database.windows.net:1433;database=${SQL_DB};encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;"
az webapp config appsettings set \
  --name "$WEBAPP" --resource-group "$RG" \
  --settings SPRING_DATASOURCE_URL="$JDBC_URL" \
             SPRING_DATASOURCE_USERNAME="$SQL_ADMIN" \
             SPRING_DATASOURCE_PASSWORD="$SQL_ADMIN_PASSWORD" \
             APPLICATIONINSIGHTS_CONNECTION_STRING="$AI_CONN" \
             APPLICATIONINSIGHTS_ROLE_NAME="dimcloud-webapp" \
             WEBSITES_PORT=8080 -o none

az webapp config set --name "$WEBAPP" --resource-group "$RG" --always-on true -o none
az webapp update --name "$WEBAPP" --resource-group "$RG" --https-only true -o none

echo
echo "Recursos criados. Web App: https://${WEBAPP}.azurewebsites.net"
echo "Próximo passo: ./02-criar-tabelas.sh"
