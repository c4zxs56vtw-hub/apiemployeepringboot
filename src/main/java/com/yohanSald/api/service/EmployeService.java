package com.yohanSald.api.service;

import com.yohanSald.api.model.Employe;
import com.yohanSald.api.repository.EmployeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Optional;

@Service
public class EmployeService {

    private final EmployeRepository employeRepository;

    public EmployeService(EmployeRepository employeRepository) {
        this.employeRepository = employeRepository;
    }

    // ── CRUD de base ──

    public List<Employe> findAll() {
        return employeRepository.findAll();
    }

    public Optional<Employe> findById(Long id) {
        return employeRepository.findById(id);
    }

    public Employe save(Employe employe) {
        return employeRepository.save(employe);
    }

    public void deleteById(Long id) {
        employeRepository.deleteById(id);
    }

    // ── Recherches simples ──

    public Optional<Employe> findByEmail(String email) {
        return employeRepository.findByEmail(email);
    }

    public List<Employe> findByPoste(String poste) {
        return employeRepository.findByPoste(poste);
    }

    public List<Employe> rechercherParNom(String nom) {
        return employeRepository.findByNomContainingIgnoreCase(nom);
    }

    public List<Employe> rechercherParPrenom(String prenom) {
        return employeRepository.findByPrenomContainingIgnoreCase(prenom);
    }

    public List<Employe> findAllTriesParSalaire() {
        return employeRepository.findAllByOrderBySalaireDesc();
    }

    public boolean emailDejaUtilise(String email) {
        return employeRepository.existsByEmail(email);
    }

    // ── Recherches complexes ──

    public List<Employe> rechercherGlobal(String mot) {
        return employeRepository.rechercherGlobal(mot);
    }

    public List<Employe> findBySalaireBetween(Double min, Double max) {
        return employeRepository.findBySalaireBetween(min, max);
    }

    public List<Employe> findByPosteEtSalaireMin(String poste, Double seuil) {
        return employeRepository.findByPosteEtSalaireMin(poste, seuil);
    }

    // ── Statistiques ──

    public Map<String, Double> salaireMoyenParPoste() {
        Map<String, Double> stats = new LinkedHashMap<>();
        employeRepository.salaireMoyenParPoste()
                .forEach(row -> stats.put((String) row[0], (Double) row[1]));
        return stats;
    }

    public Map<String, Long> countParPoste() {
        Map<String, Long> stats = new LinkedHashMap<>();
        employeRepository.countParPoste()
                .forEach(row -> stats.put((String) row[0], (Long) row[1]));
        return stats;
    }
}