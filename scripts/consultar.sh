#!/usr/bin/env bash
# =====================================================================
# DimCloud - mostra o conteúdo das tabelas no Azure SQL (sqlcmd).
# Usado no vídeo depois de CADA operação do CRUD para provar a persistência.
# Uso: ./consultar.sh correntistas | transacoes | saldo | tudo
# =====================================================================
set -euo pipefail
cd "$(dirname "$0")"
source ./00-variaveis.sh
garantir_sqlcmd
liberar_meu_ip

q() {
  sqlcmd -S "tcp:${SQL_SERVER}.database.windows.net,1433" -d "$SQL_DB" \
    -U "$SQL_ADMIN" -P "$SQL_ADMIN_PASSWORD" -N -l 60 -W -s ' | ' \
    -Q "SET NOCOUNT ON; $1"
}

CORR="SELECT ID_CORRENTISTA, NOME, CPF, EMAIL, TELEFONE, DT_CADASTRO FROM dbo.TB_CORRENTISTA ORDER BY ID_CORRENTISTA"
TRAN="SELECT ID_TRANSACAO, ID_CORRENTISTA, TIPO, VALOR, DESCRICAO, DT_TRANSACAO FROM dbo.TB_TRANSACAO ORDER BY ID_TRANSACAO"
SALDO="SELECT c.ID_CORRENTISTA, c.NOME, COUNT(t.ID_TRANSACAO) AS QTD,
  SUM(CASE WHEN t.TIPO IN ('DEPOSITO','PIX_RECEBIDO') THEN t.VALOR
           WHEN t.TIPO IN ('SAQUE','PIX_ENVIADO') THEN -t.VALOR ELSE 0 END) AS SALDO
  FROM dbo.TB_CORRENTISTA c LEFT JOIN dbo.TB_TRANSACAO t ON t.ID_CORRENTISTA = c.ID_CORRENTISTA
  GROUP BY c.ID_CORRENTISTA, c.NOME ORDER BY c.ID_CORRENTISTA"

case "${1:-tudo}" in
  correntistas) echo "== TB_CORRENTISTA"; q "$CORR" ;;
  transacoes)   echo "== TB_TRANSACAO";   q "$TRAN" ;;
  saldo)        echo "== JOIN correntista x transações"; q "$SALDO" ;;
  tudo)         echo "== TB_CORRENTISTA"; q "$CORR"; echo; echo "== TB_TRANSACAO"; q "$TRAN" ;;
  *) echo "uso: $0 correntistas|transacoes|saldo|tudo"; exit 1 ;;
esac
