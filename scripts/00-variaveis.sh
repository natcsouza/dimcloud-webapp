#!/usr/bin/env bash
# =====================================================================
# DimCloud / DimDim - variáveis usadas por todos os scripts do Azure CLI
# Os outros scripts fazem "source" deste arquivo.
# Nomes de SQL Server e Web App precisam ser únicos no mundo: por isso
# levam o RM de quem cria os recursos (troque com: export RM=rmXXXXXX).
# Qualquer nome abaixo pode ser sobrescrito antes de rodar (ex.: export RG=rg-outro)
# para subir um segundo ambiente em paralelo sem colidir com o primeiro.
# =====================================================================

export RM="${RM:-rm564099}"
export LOCATION="${LOCATION:-canadacentral}"   # Azure for Students (FIAP): só canadacentral, eastus2, southcentralus, chilecentral, northcentralus

# Git Bash (Windows) converte "/subscriptions/..." em caminho C:/...; isto desliga a conversão
export MSYS_NO_PATHCONV=1

export RG="${RG:-rg-dimcloud-webapp}"
export SQL_SERVER="${SQL_SERVER:-sql-dimcloud-${RM}}"
export SQL_DB="${SQL_DB:-dimclouddb}"
export SQL_ADMIN="${SQL_ADMIN:-dimcloudadmin}"
export PLAN="${PLAN:-plan-dimcloud}"
export WEBAPP="${WEBAPP:-app-dimcloud-${RM}}"
export LAW="${LAW:-law-dimcloud}"                      # Log Analytics (base do App Insights e logs do banco)
export APPINSIGHTS="${APPINSIGHTS:-appi-dimcloud}"
export RUNTIME="JAVA:21-java21"                # Java SE 21 (Spring Boot jar)

# A senha do banco NUNCA fica no código nem no GitHub:
# vem da variável de ambiente SQL_ADMIN_PASSWORD ou é pedida no terminal.
if [ -z "${SQL_ADMIN_PASSWORD:-}" ]; then
  read -r -s -p "Senha do admin do Azure SQL (mín. 8 chars, maiúscula, minúscula, número e símbolo): " SQL_ADMIN_PASSWORD
  echo
fi
export SQL_ADMIN_PASSWORD

# O Azure Cloud Shell não traz o sqlcmd: baixa o go-sqlcmd oficial da Microsoft
# em ~/.local/bin na primeira vez (usado por 02-criar-tabelas.sh e consultar.sh).
garantir_sqlcmd() {
  command -v sqlcmd >/dev/null 2>&1 && return 0
  echo ">> sqlcmd não encontrado: instalando o go-sqlcmd em ~/.local/bin"
  mkdir -p "$HOME/.local/bin"
  curl -sL https://github.com/microsoft/go-sqlcmd/releases/latest/download/sqlcmd-linux-amd64.tar.bz2 \
    | tar -xj -C "$HOME/.local/bin" sqlcmd
  export PATH="$HOME/.local/bin:$PATH"
}

# O IP público muda (sessão nova do Cloud Shell, rede móvel): libera o IP atual
# no firewall do Azure SQL só quando ele for diferente do último liberado.
liberar_meu_ip() {
  local ip; ip=$(curl -s https://api.ipify.org)
  [ "$(cat "$HOME/.dimcloud_ip_${SQL_SERVER}" 2>/dev/null)" = "$ip" ] && return 0
  echo ">> liberando o IP $ip no firewall do Azure SQL"
  az sql server firewall-rule create -g "$RG" -s "$SQL_SERVER" -n MeuIP \
    --start-ip-address "$ip" --end-ip-address "$ip" -o none
  echo "$ip" > "$HOME/.dimcloud_ip_${SQL_SERVER}"
}
