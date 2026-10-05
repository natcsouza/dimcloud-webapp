package br.com.fiap.dimcloud.controller.api;

import br.com.fiap.dimcloud.model.TipoTransacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Corpo do POST/PUT de /api/transacoes. */
public record TransacaoRequest(
        @NotNull Long correntistaId,
        @NotNull TipoTransacao tipo,
        @NotNull @DecimalMin("0.01") BigDecimal valor,
        @Size(max = 150) String descricao,
        LocalDateTime dataTransacao) {
}
