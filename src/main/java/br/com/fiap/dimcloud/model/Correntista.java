package br.com.fiap.dimcloud.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "TB_CORRENTISTA")
public class Correntista {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CORRENTISTA")
    private Long id;

    @NotBlank(message = "Informe o nome")
    @Size(max = 100)
    @Column(name = "NOME", nullable = false, length = 100)
    private String nome;

    @NotBlank(message = "Informe o CPF")
    @Pattern(regexp = "[0-9]{11}", message = "CPF com 11 dígitos, só números")
    @Column(name = "CPF", nullable = false, length = 11, unique = true)
    private String cpf;

    @NotBlank(message = "Informe o e-mail")
    @Email(message = "E-mail inválido")
    @Size(max = 120)
    @Column(name = "EMAIL", nullable = false, length = 120)
    private String email;

    @Size(max = 20)
    @Column(name = "TELEFONE", length = 20)
    private String telefone;

    @Column(name = "DT_CADASTRO", nullable = false)
    private LocalDateTime dataCadastro;

    @JsonIgnore
    @OneToMany(mappedBy = "correntista", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dataTransacao DESC")
    private List<Transacao> transacoes = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (dataCadastro == null) {
            dataCadastro = LocalDateTime.now();
        }
    }

    public BigDecimal getSaldo() {
        return transacoes.stream()
                .map(t -> t.getTipo().isCredito() ? t.getValor() : t.getValor().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
    public List<Transacao> getTransacoes() { return transacoes; }
    public void setTransacoes(List<Transacao> transacoes) { this.transacoes = transacoes; }
}
