package br.com.fiap.dimcloud.controller.api;

import br.com.fiap.dimcloud.model.Correntista;
import br.com.fiap.dimcloud.model.Transacao;
import br.com.fiap.dimcloud.repository.CorrentistaRepository;
import br.com.fiap.dimcloud.repository.TransacaoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoApiController {

    private final TransacaoRepository transacoes;
    private final CorrentistaRepository correntistas;

    public TransacaoApiController(TransacaoRepository transacoes, CorrentistaRepository correntistas) {
        this.transacoes = transacoes;
        this.correntistas = correntistas;
    }

    @GetMapping
    public List<Transacao> listar() {
        return transacoes.findAllByOrderByDataTransacaoDesc();
    }

    @GetMapping("/{id}")
    public Transacao buscar(@PathVariable Long id) {
        return transacoes.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transação não encontrada"));
    }

    @PostMapping
    public ResponseEntity<Transacao> criar(@Valid @RequestBody TransacaoRequest body) {
        Transacao t = new Transacao();
        preencher(t, body);
        Transacao salva = transacoes.save(t);
        return ResponseEntity.created(URI.create("/api/transacoes/" + salva.getId())).body(salva);
    }

    @PutMapping("/{id}")
    public Transacao atualizar(@PathVariable Long id, @Valid @RequestBody TransacaoRequest body) {
        Transacao t = buscar(id);
        preencher(t, body);
        return transacoes.save(t);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        transacoes.delete(buscar(id));
    }

    private void preencher(Transacao t, TransacaoRequest body) {
        Correntista c = correntistas.findById(body.correntistaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Correntista inexistente"));
        t.setCorrentista(c);
        t.setTipo(body.tipo());
        t.setValor(body.valor());
        t.setDescricao(body.descricao());
        t.setDataTransacao(body.dataTransacao() != null ? body.dataTransacao() : LocalDateTime.now());
    }
}
