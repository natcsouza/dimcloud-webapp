package br.com.fiap.dimcloud.repository;

import br.com.fiap.dimcloud.model.Correntista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CorrentistaRepository extends JpaRepository<Correntista, Long> {

    List<Correntista> findAllByOrderByNomeAsc();

    boolean existsByCpfAndIdNot(String cpf, Long id);

    boolean existsByCpf(String cpf);
}
