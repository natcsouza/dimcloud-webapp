package br.com.fiap.dimcloud.model;

public enum TipoTransacao {
    DEPOSITO("Depósito"),
    SAQUE("Saque"),
    PIX_ENVIADO("Pix enviado"),
    PIX_RECEBIDO("Pix recebido");

    private final String descricao;

    TipoTransacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    /** Depósito e Pix recebido somam no saldo; saque e Pix enviado subtraem. */
    public boolean isCredito() {
        return this == DEPOSITO || this == PIX_RECEBIDO;
    }
}
