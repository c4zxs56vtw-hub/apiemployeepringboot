package com.yohanSald.api.controlleur;

import com.yohanSald.api.model.Employe;
import com.yohanSald.api.service.EmployeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employes")
public class EmployeControlleur {

    private final EmployeService employeService;

    public EmployeControlleur(EmployeService employeService) {
        this.employeService = employeService;
    }

    // ── CRUD de base ──

    @GetMapping
    public ResponseEntity<List<Employe>> findAll() {
        return ResponseEntity.ok(employeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employe> findById(@PathVariable Long id) {
        return employeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Employe> create(@RequestBody Employe employe) {
        if (employeService.emailDejaUtilise(employe.getEmail())) {
            return ResponseEntity.badRequest().build();
        }
        employe.setId(null);
        Employe saved = employeService.save(employe);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employe> update(@PathVariable Long id, @RequestBody Employe employe) {
        return employeService.findById(id)
                .map(existing -> {
                    employe.setId(id);
                    return ResponseEntity.ok(employeService.save(employe));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return employeService.findById(id)
                .map(existing -> {
                    employeService.deleteById(id);
                    return ResponseEntity.<Void>noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ── Recherches ──

    // GET /api/employes/recherche?mot=alice
    @GetMapping("/recherche")
    public ResponseEntity<List<Employe>> rechercherGlobal(@RequestParam String mot) {
        return ResponseEntity.ok(employeService.rechercherGlobal(mot));
    }

    // GET /api/employes/poste?nom=Developpeur
    @GetMapping("/poste")
    public ResponseEntity<List<Employe>> findByPoste(@RequestParam String nom) {
        return ResponseEntity.ok(employeService.findByPoste(nom));
    }

    // GET /api/employes/salaire?min=40000&max=60000
    @GetMapping("/salaire")
    public ResponseEntity<List<Employe>> findBySalaire(
            @RequestParam Double min,
            @RequestParam Double max) {
        return ResponseEntity.ok(employeService.findBySalaireBetween(min, max));
    }

    // GET /api/employes/tries
    @GetMapping("/tries")
    public ResponseEntity<List<Employe>> findTriesParSalaire() {
        return ResponseEntity.ok(employeService.findAllTriesParSalaire());
    }


    // GET /api/employes/stats/salaires
    @GetMapping("/stats/salaires")
    public ResponseEntity<Map<String, Double>> salaireMoyenParPoste() {
        return ResponseEntity.ok(employeService.salaireMoyenParPoste());
    }

    // GET /api/employes/stats/effectifs
    @GetMapping("/stats/effectifs")
    public ResponseEntity<Map<String, Long>> countParPoste() {
        return ResponseEntity.ok(employeService.countParPoste());
    }
}