package com.yohanSald.api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EmployeRequestDTO {

    @NotBlank(message = "Le prenom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prenom doit contenir entre 2 et 50 caracteres")
    private String prenom;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit contenir entre 2 et 50 caracteres")
    private String nom;

    @NotBlank(message = "L email est obligatoire")
    @Email(message = "L adresse email n est pas valide")
    private String email;

    @Size(max = 80, message = "Le poste ne doit pas depasser 80 caracteres")
    private String poste;

    @Positive(message = "Le salaire doit etre un nombre positif")
    private Double salaire;
}