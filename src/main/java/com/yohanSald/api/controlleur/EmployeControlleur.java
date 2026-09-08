package com.yohanSald.api.controlleur;

import com.yohanSald.api.model.Employe;
import com.yohanSald.api.service.EmployeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/employes")
public class EmployeControlleur {

    private final EmployeService employeService;

    public EmployeControlleur(EmployeService employeService) {
        this.employeService = employeService;
    }

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
        employe.setId(null);
        Employe saved = employeService.save(employe);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
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
}