package com.yohanSald.api;

import com.yohanSald.api.model.Employe;
import com.yohanSald.api.repository.EmployeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final EmployeRepository employeRepository;

    public DataLoader(EmployeRepository employeRepository) {
        this.employeRepository = employeRepository;
    }

    @Override
    public void run(String... args) {
        if (employeRepository.count() == 0) {
            employeRepository.save(new Employe(null, "Alice",  "Martin",  "alice.martin@email.com",   "Developpeur",    45000.0));
            employeRepository.save(new Employe(null, "Bob",    "Dupont",  "bob.dupont@email.com",     "Chef de projet", 55000.0));
            employeRepository.save(new Employe(null, "Claire", "Bernard", "claire.bernard@email.com", "Designer UX",    42000.0));
            employeRepository.save(new Employe(null, "David",  "Moreau",  "david.moreau@email.com",   "DevOps",         50000.0));
            employeRepository.save(new Employe(null, "Emma",   "Leroy",   "emma.leroy@email.com",     "Data Analyst",   47000.0));
        }
    }
}