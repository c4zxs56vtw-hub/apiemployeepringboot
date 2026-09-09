package com.yohanSald.api.controlleur;

import com.yohanSald.api.dto.EmployeRequestDTO;
import com.yohanSald.api.dto.EmployeResponseDTO;
import com.yohanSald.api.mapper.EmployeMapper;
import com.yohanSald.api.model.Employe;
import com.yohanSald.api.service.EmployeService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v1/employes")
public class EmployeControlleur {

    private final EmployeService employeService;

    public EmployeControlleur(EmployeService employeService) {
        this.employeService = employeService;
    }

    // ── Methode utilitaire : construit un EntityModel avec liens HATEOAS ──
    private EntityModel<EmployeResponseDTO> toModel(Employe employe) {
        EmployeResponseDTO dto = EmployeMapper.toResponseDTO(employe);
        return EntityModel.of(dto,
                linkTo(methodOn(EmployeControlleur.class).findById(employe.getId())).withSelfRel(),
                linkTo(methodOn(EmployeControlleur.class).findAll()).withRel("employes"),
                Link.of("/api/v1/employes/" + employe.getId(), "modifier").withType("PUT"),
                Link.of("/api/v1/employes/" + employe.getId(), "supprimer").withType("DELETE")
        );
    }

    // ── CRUD de base ──

    // GET /api/v1/employes
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<EmployeResponseDTO>>> findAll() {
        List<EntityModel<EmployeResponseDTO>> models = employeService.findAll()
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        CollectionModel<EntityModel<EmployeResponseDTO>> collection = CollectionModel.of(
                models,
                linkTo(methodOn(EmployeControlleur.class).findAll()).withSelfRel()
        );

        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS).cachePublic())
                .body(collection);
    }

    // GET /api/v1/employes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<EmployeResponseDTO>> findById(@PathVariable Long id) {
        return employeService.findById(id)
                .map(employe -> ResponseEntity.ok()
                        .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS).cachePublic())
                        .body(toModel(employe)))
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/v1/employes
    @PostMapping
    public ResponseEntity<EntityModel<EmployeResponseDTO>> create(
            @RequestBody @Valid EmployeRequestDTO dto) {
        if (employeService.emailDejaUtilise(dto.getEmail())) {
            return ResponseEntity.badRequest().build();
        }
        Employe saved = employeService.save(EmployeMapper.toEntity(dto));
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{id}")
                .buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(toModel(saved));
    }

    // PUT /api/v1/employes/{id}
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<EmployeResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody @Valid EmployeRequestDTO dto) {
        return employeService.findById(id)
                .map(existing -> {
                    Employe employe = EmployeMapper.toEntity(dto);
                    employe.setId(id);
                    return ResponseEntity.ok(toModel(employeService.save(employe)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/v1/employes/{id} — Idempotent
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeService.findById(id).ifPresent(e -> employeService.deleteById(id));
        return ResponseEntity.noContent().build();
    }

    // ── Recherches ──

    @GetMapping("/recherche")
    public ResponseEntity<CollectionModel<EntityModel<EmployeResponseDTO>>> rechercherGlobal(
            @RequestParam String mot) {
        List<EntityModel<EmployeResponseDTO>> models = employeService.rechercherGlobal(mot)
                .stream().map(this::toModel).collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(models,
                linkTo(methodOn(EmployeControlleur.class).rechercherGlobal(mot)).withSelfRel()));
    }

    @GetMapping("/poste")
    public ResponseEntity<CollectionModel<EntityModel<EmployeResponseDTO>>> findByPoste(
            @RequestParam String nom) {
        List<EntityModel<EmployeResponseDTO>> models = employeService.findByPoste(nom)
                .stream().map(this::toModel).collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(models,
                linkTo(methodOn(EmployeControlleur.class).findByPoste(nom)).withSelfRel()));
    }

    @GetMapping("/salaire")
    public ResponseEntity<CollectionModel<EntityModel<EmployeResponseDTO>>> findBySalaire(
            @RequestParam Double min, @RequestParam Double max) {
        List<EntityModel<EmployeResponseDTO>> models = employeService.findBySalaireBetween(min, max)
                .stream().map(this::toModel).collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(models,
                linkTo(methodOn(EmployeControlleur.class).findBySalaire(min, max)).withSelfRel()));
    }

    @GetMapping("/tries")
    public ResponseEntity<CollectionModel<EntityModel<EmployeResponseDTO>>> findTriesParSalaire() {
        List<EntityModel<EmployeResponseDTO>> models = employeService.findAllTriesParSalaire()
                .stream().map(this::toModel).collect(Collectors.toList());
        return ResponseEntity.ok(CollectionModel.of(models,
                linkTo(methodOn(EmployeControlleur.class).findTriesParSalaire()).withSelfRel()));
    }

    // ── Statistiques ──

    @GetMapping("/stats/salaires")
    public ResponseEntity<Map<String, Double>> salaireMoyenParPoste() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(300, TimeUnit.SECONDS).cachePublic())
                .body(employeService.salaireMoyenParPoste());
    }

    @GetMapping("/stats/effectifs")
    public ResponseEntity<Map<String, Long>> countParPoste() {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(300, TimeUnit.SECONDS).cachePublic())
                .body(employeService.countParPoste());
    }
}