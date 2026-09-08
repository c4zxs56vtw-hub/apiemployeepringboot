package com.yohanSald.api.controlleur;

import com.yohanSald.api.model.Employe;
import com.yohanSald.api.service.EmployeService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employes")
public class EmployeViewController {

    private final EmployeService employeService;

    public EmployeViewController(EmployeService employeService) {
        this.employeService = employeService;
    }

    // Liste tous les employes
    @GetMapping
    public String liste(Model model) {
        model.addAttribute("employes", employeService.findAll());
        return "employes/liste";
    }

    // Formulaire creation
    @GetMapping("/nouveau")
    public String formulaireCreation(Model model) {
        model.addAttribute("employe", new Employe());
        model.addAttribute("titre", "Nouvel employe");
        return "employes/formulaire";
    }

    // Formulaire edition
    @GetMapping("/{id}/editer")
    public String formulaireEdition(@PathVariable Long id, Model model) {
        return employeService.findById(id).map(employe -> {
            model.addAttribute("employe", employe);
            model.addAttribute("titre", "Modifier l employe");
            return "employes/formulaire";
        }).orElse("redirect:/employes");
    }

    // Sauvegarder (creation ou edition)
    @PostMapping("/sauvegarder")
    public String sauvegarder(@ModelAttribute Employe employe) {
        employeService.save(employe);
        return "redirect:/employes";
    }

    // Supprimer
    @GetMapping("/{id}/supprimer")
    public String supprimer(@PathVariable Long id) {
        employeService.deleteById(id);
        return "redirect:/employes";
    }
}