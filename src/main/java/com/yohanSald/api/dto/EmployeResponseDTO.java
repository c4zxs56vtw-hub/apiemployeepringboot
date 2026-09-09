package com.yohanSald.api.dto;

import lombok.Data;

@Data
public class EmployeResponseDTO {

    private Long id;
    private String prenom;
    private String nom;
    private String email;
    private String poste;
    // Le salaire est volontairement exclu de la reponse publique
}