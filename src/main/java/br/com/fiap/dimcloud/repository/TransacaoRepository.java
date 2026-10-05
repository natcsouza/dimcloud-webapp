package br.com.fiap.dimcloud.repository;

import br.com.fiap.dimcloud.model.Transacao;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    @EntityGraph(attributePaths = "correntista")
    List<Transacao> findAllByOrderByDataTransacaoDesc();

    @EntityGraph(attributePaths = "correntista")
    List<Transacao> findByCorrentistaIdOrderByDataTransacaoDesc(Long correntistaId);
}
