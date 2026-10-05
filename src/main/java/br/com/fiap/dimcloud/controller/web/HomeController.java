package br.com.fiap.dimcloud.controller.web;

import br.com.fiap.dimcloud.model.Correntista;
import br.com.fiap.dimcloud.repository.CorrentistaRepository;
import br.com.fiap.dimcloud.repository.TransacaoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class HomeController {

    private final CorrentistaRepository correntistas;
    private final TransacaoRepository transacoes;

    public HomeController(CorrentistaRepository correntistas, TransacaoRepository transacoes) {
        this.correntistas = correntistas;
        this.transacoes = transacoes;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Correntista> lista = correntistas.findAll();
        BigDecimal saldoTotal = lista.stream().map(Correntista::getSaldo).reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("totalCorrentistas", lista.size());
        model.addAttribute("totalTransacoes", transacoes.count());
        model.addAttribute("saldoTotal", saldoTotal);
        model.addAttribute("ultimas", transacoes.findAllByOrderByDataTransacaoDesc().stream().limit(5).toList());
        return "index";
    }
}
