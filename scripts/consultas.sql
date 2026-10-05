-- =====================================================================
-- DimCloud - consultas para mostrar no vídeo a persistência após cada
-- operação do CRUD (rodar no Query editor do portal ou com sqlcmd).
-- =====================================================================

-- 1) Tabela de correntistas
SELECT * FROM dbo.TB_CORRENTISTA ORDER BY ID_CORRENTISTA;

-- 2) Tabela de transações
SELECT * FROM dbo.TB_TRANSACAO ORDER BY ID_TRANSACAO;

-- 3) Relacionamento (JOIN) + saldo por correntista
SELECT c.ID_CORRENTISTA, c.NOME,
       COUNT(t.ID_TRANSACAO) AS QTD_TRANSACOES,
       SUM(CASE WHEN t.TIPO IN ('DEPOSITO','PIX_RECEBIDO') THEN t.VALOR
                WHEN t.TIPO IN ('SAQUE','PIX_ENVIADO')     THEN -t.VALOR
                ELSE 0 END) AS SALDO
FROM dbo.TB_CORRENTISTA c
LEFT JOIN dbo.TB_TRANSACAO t ON t.ID_CORRENTISTA = c.ID_CORRENTISTA
GROUP BY c.ID_CORRENTISTA, c.NOME
ORDER BY c.ID_CORRENTISTA;
