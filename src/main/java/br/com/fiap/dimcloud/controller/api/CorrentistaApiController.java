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
import java.util.List;

@RestController
@RequestMapping("/api/correntistas")
public class CorrentistaApiController {

    private final CorrentistaRepository correntistas;
    private final TransacaoRepository transacoes;

    public CorrentistaApiController(CorrentistaRepository correntistas, TransacaoRepository transacoes) {
        this.correntistas = correntistas;
        this.transacoes = transacoes;
    }

    @GetMapping
    public List<Correntista> listar() {
        return correntistas.findAllByOrderByNomeAsc();
    }

    @GetMapping("/{id}")
    public Correntista buscar(@PathVariable Long id) {
        return correntistas.findById(id).orElseThrow(this::naoEncontrado);
    }

    @GetMapping("/{id}/transacoes")
    public List<Transacao> extrato(@PathVariable Long id) {
        buscar(id);
        return transacoes.findByCorrentistaIdOrderByDataTransacaoDesc(id);
    }

    @PostMapping
    public ResponseEntity<Correntista> criar(@Valid @RequestBody Correntista body) {
        if (correntistas.existsByCpf(body.getCpf())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }
        body.setId(null);
        Correntista salvo = correntistas.save(body);
        return ResponseEntity.created(URI.create("/api/correntistas/" + salvo.getId())).body(salvo);
    }

    @PutMapping("/{id}")
    public Correntista atualizar(@PathVariable Long id, @Valid @RequestBody Correntista body) {
        Correntista atual = buscar(id);
        if (correntistas.existsByCpfAndIdNot(body.getCpf(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }
        atual.setNome(body.getNome());
        atual.setCpf(body.getCpf());
        atual.setEmail(body.getEmail());
        atual.setTelefone(body.getTelefone());
        return correntistas.save(atual);
    }

    /** Apaga o correntista e, em cascata, as transações dele. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        correntistas.delete(buscar(id));
    }

    private ResponseStatusException naoEncontrado() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Correntista não encontrado");
    }
}
