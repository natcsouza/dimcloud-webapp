package br.com.fiap.dimcloud.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TB_TRANSACAO")
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TRANSACAO")
    private Long id;

    @JsonIgnore
    @NotNull(message = "Escolha o correntista")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_CORRENTISTA", nullable = false)
    private Correntista correntista;

    @NotNull(message = "Escolha o tipo")
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 20)
    private TipoTransacao tipo;

    @NotNull(message = "Informe o valor")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    @Column(name = "VALOR", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Size(max = 150)
    @Column(name = "DESCRICAO", length = 150)
    private String descricao;

    @NotNull(message = "Informe a data")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "DT_TRANSACAO", nullable = false)
    private LocalDateTime dataTransacao;

    /** Exposto no JSON da API no lugar do objeto inteiro. */
    public Long getCorrentistaId() {
        return correntista != null ? correntista.getId() : null;
    }

    public String getCorrentistaNome() {
        return correntista != null ? correntista.getNome() : null;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Correntista getCorrentista() { return correntista; }
    public void setCorrentista(Correntista correntista) { this.correntista = correntista; }
    public TipoTransacao getTipo() { return tipo; }
    public void setTipo(TipoTransacao tipo) { this.tipo = tipo; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDateTime getDataTransacao() { return dataTransacao; }
    public void setDataTransacao(LocalDateTime dataTransacao) { this.dataTransacao = dataTransacao; }
}
