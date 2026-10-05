package br.com.fiap.dimcloud.controller.web;

import br.com.fiap.dimcloud.model.Correntista;
import br.com.fiap.dimcloud.repository.CorrentistaRepository;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.util.Locale;

/** Converte o id escolhido no select do formulário de transação em Correntista. */
@Component
public class CorrentistaFormatter implements Formatter<Correntista> {

    private final CorrentistaRepository repo;

    public CorrentistaFormatter(CorrentistaRepository repo) {
        this.repo = repo;
    }

    @Override
    public Correntista parse(String text, Locale locale) {
        if (text == null || text.isBlank()) {
            return null;
        }
        return repo.findById(Long.valueOf(text)).orElse(null);
    }

    @Override
    public String print(Correntista c, Locale locale) {
        return c.getId() == null ? "" : c.getId().toString();
    }
}
