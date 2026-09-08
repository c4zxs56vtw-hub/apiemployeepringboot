package com.yohanSald.api.repository;

import com.yohanSald.api.model.Employe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeRepository extends JpaRepository<Employe, Long> {

    // ────────────────────────────────────────
    // APPROCHE 1 : Requetes derivees (simples)
    // ────────────────────────────────────────

    // Trouver par email exact (unicite)
    Optional<Employe> findByEmail(String email);

    // Trouver par poste
    List<Employe> findByPoste(String poste);

    // Recherche partielle sur le nom (insensible a la casse)
    List<Employe> findByNomContainingIgnoreCase(String nom);

    // Recherche partielle sur le prenom
    List<Employe> findByPrenomContainingIgnoreCase(String prenom);

    // Verifier si un email est deja utilise
    boolean existsByEmail(String email);

    // Trier par salaire decroissant
    List<Employe> findAllByOrderBySalaireDesc();

    // ────────────────────────────────────────
    // APPROCHE 2 : @Query JPQL (cas complexes)
    // ────────────────────────────────────────

    // Recherche globale sur nom, prenom ou email
    @Query("SELECT e FROM Employe e WHERE " +
           "LOWER(e.nom)    LIKE LOWER(CONCAT('%', :mot, '%')) OR " +
           "LOWER(e.prenom) LIKE LOWER(CONCAT('%', :mot, '%')) OR " +
           "LOWER(e.email)  LIKE LOWER(CONCAT('%', :mot, '%'))")
    List<Employe> rechercherGlobal(@Param("mot") String mot);

    // Filtrer par fourchette de salaire
    @Query("SELECT e FROM Employe e WHERE e.salaire BETWEEN :min AND :max ORDER BY e.salaire DESC")
    List<Employe> findBySalaireBetween(@Param("min") Double min, @Param("max") Double max);

    // Filtrer par poste et salaire minimum
    @Query("SELECT e FROM Employe e WHERE e.poste = :poste AND e.salaire >= :seuil ORDER BY e.salaire DESC")
    List<Employe> findByPosteEtSalaireMin(@Param("poste") String poste, @Param("seuil") Double seuil);

    // Statistiques : salaire moyen par poste
    @Query("SELECT e.poste, AVG(e.salaire) FROM Employe e GROUP BY e.poste ORDER BY AVG(e.salaire) DESC")
    List<Object[]> salaireMoyenParPoste();

    // Compter les employes par poste
    @Query("SELECT e.poste, COUNT(e) FROM Employe e GROUP BY e.poste")
    List<Object[]> countParPoste();
}