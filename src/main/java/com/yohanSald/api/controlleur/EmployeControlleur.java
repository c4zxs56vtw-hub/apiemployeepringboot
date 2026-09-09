package com.yohanSald.api.controlleur;

import com.yohanSald.api.dto.EmployeRequestDTO;
import com.yohanSald.api.dto.EmployeResponseDTO;
import com.yohanSald.api.mapper.EmployeMapper;
import com.yohanSald.api.model.Employe;
import com.yohanSald.api.service.EmployeService;
import jakarta.validation.Valid;
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

    // GET /api/employes
    @GetMapping
    public ResponseEntity<List<EmployeResponseDTO>> findAll() {
        List<EmployeResponseDTO> dtos = employeService.findAll()
                .stream()
                .map(EmployeMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    // GET /api/employes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<EmployeResponseDTO> findById(@PathVariable Long id) {
        return employeService.findById(id)
                .map(EmployeMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/employes
    @PostMapping
    public ResponseEntity<EmployeResponseDTO> create(@RequestBody @Valid EmployeRequestDTO dto) {
        if (employeService.emailDejaUtilise(dto.getEmail())) {
            return ResponseEntity.badRequest().build();
        }
        Employe employe = EmployeMapper.toEntity(dto);
        Employe saved = employeService.save(employe);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(EmployeMapper.toResponseDTO(saved));
    }

    // PUT /api/employes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<EmployeResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid EmployeRequestDTO dto) {
        return employeService.findById(id)
                .map(existing -> {
                    Employe employe = EmployeMapper.toEntity(dto);
                    employe.setId(id);
                    Employe updated = employeService.save(employe);
                    return ResponseEntity.ok(EmployeMapper.toResponseDTO(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/employes/{id}
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
    public ResponseEntity<List<EmployeResponseDTO>> rechercherGlobal(@RequestParam String mot) {
        List<EmployeResponseDTO> dtos = employeService.rechercherGlobal(mot)
                .stream()
                .map(EmployeMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    // GET /api/employes/poste?nom=Developpeur
    @GetMapping("/poste")
    public ResponseEntity<List<EmployeResponseDTO>> findByPoste(@RequestParam String nom) {
        List<EmployeResponseDTO> dtos = employeService.findByPoste(nom)
                .stream()
                .map(EmployeMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    // GET /api/employes/salaire?min=40000&max=60000
    @GetMapping("/salaire")
    public ResponseEntity<List<EmployeResponseDTO>> findBySalaire(
            @RequestParam Double min,
            @RequestParam Double max) {
        List<EmployeResponseDTO> dtos = employeService.findBySalaireBetween(min, max)
                .stream()
                .map(EmployeMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    // GET /api/employes/tries
    @GetMapping("/tries")
    public ResponseEntity<List<EmployeResponseDTO>> findTriesParSalaire() {
        List<EmployeResponseDTO> dtos = employeService.findAllTriesParSalaire()
                .stream()
                .map(EmployeMapper::toResponseDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    // ── Statistiques ──

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