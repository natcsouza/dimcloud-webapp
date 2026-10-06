package br.com.fiap.dimcloud.controller.web;

import br.com.fiap.dimcloud.model.TipoTransacao;
import br.com.fiap.dimcloud.model.Transacao;
import br.com.fiap.dimcloud.repository.CorrentistaRepository;
import br.com.fiap.dimcloud.repository.TransacaoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping("/transacoes")
public class TransacaoController {

    private final TransacaoRepository repo;
    private final CorrentistaRepository correntistas;

    public TransacaoController(TransacaoRepository repo, CorrentistaRepository correntistas) {
        this.repo = repo;
        this.correntistas = correntistas;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) Long correntista, Model model) {
        model.addAttribute("transacoes", correntista == null
                ? repo.findAllByOrderByDataTransacaoDesc()
                : repo.findByCorrentista_IdOrderByDataTransacaoDesc(correntista));
        model.addAttribute("correntistas", correntistas.findAllByOrderByNomeAsc());
        model.addAttribute("filtro", correntista);
        return "transacoes/lista";
    }

    @GetMapping("/nova")
    public String nova(@RequestParam(required = false) Long correntista, Model model) {
        Transacao t = new Transacao();
        t.setDataTransacao(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        if (correntista != null) {
            correntistas.findById(correntista).ifPresent(t::setCorrentista);
        }
        return form(t, model);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return repo.findById(id).map(t -> form(t, model)).orElseGet(() -> {
            ra.addFlashAttribute("erro", "Transação não encontrada");
            return "redirect:/transacoes";
        });
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("transacao") Transacao form, BindingResult br,
                         Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            return form(form, model);
        }
        boolean nova = form.getId() == null;
        repo.save(form);
        ra.addFlashAttribute("ok", nova ? "Transação registrada (INSERT)" : "Transação atualizada (UPDATE)");
        return "redirect:/transacoes";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        repo.findById(id).ifPresent(repo::delete);
        ra.addFlashAttribute("ok", "Transação excluída (DELETE)");
        return "redirect:/transacoes";
    }

    private String form(Transacao t, Model model) {
        model.addAttribute("transacao", t);
        model.addAttribute("correntistas", correntistas.findAllByOrderByNomeAsc());
        model.addAttribute("tipos", TipoTransacao.values());
        return "transacoes/form";
    }
}
