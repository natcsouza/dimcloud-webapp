package br.com.fiap.dimcloud.controller.web;

import br.com.fiap.dimcloud.model.Correntista;
import br.com.fiap.dimcloud.repository.CorrentistaRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/correntistas")
public class CorrentistaController {

    private final CorrentistaRepository repo;

    public CorrentistaController(CorrentistaRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("correntistas", repo.findAllByOrderByNomeAsc());
        return "correntistas/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("correntista", new Correntista());
        return "correntistas/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return repo.findById(id).map(c -> {
            model.addAttribute("correntista", c);
            return "correntistas/form";
        }).orElseGet(() -> {
            ra.addFlashAttribute("erro", "Correntista não encontrado");
            return "redirect:/correntistas";
        });
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("correntista") Correntista form, BindingResult br, RedirectAttributes ra) {
        boolean cpfRepetido = form.getId() == null
                ? repo.existsByCpf(form.getCpf())
                : repo.existsByCpfAndIdNot(form.getCpf(), form.getId());
        if (cpfRepetido) {
            br.rejectValue("cpf", "cpf.duplicado", "CPF já cadastrado");
        }
        if (br.hasErrors()) {
            return "correntistas/form";
        }
        boolean novo = form.getId() == null;
        Correntista alvo = novo ? new Correntista() : repo.findById(form.getId()).orElseThrow();
        alvo.setNome(form.getNome());
        alvo.setCpf(form.getCpf());
        alvo.setEmail(form.getEmail());
        alvo.setTelefone(form.getTelefone());
        repo.save(alvo);
        ra.addFlashAttribute("ok", novo ? "Correntista cadastrado (INSERT)" : "Correntista atualizado (UPDATE)");
        return "redirect:/correntistas";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes ra) {
        repo.findById(id).ifPresent(repo::delete);
        ra.addFlashAttribute("ok", "Correntista excluído (DELETE) junto com suas transações");
        return "redirect:/correntistas";
    }
}
