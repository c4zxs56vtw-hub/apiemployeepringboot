package com.yohanSald.api.mapper;

import com.yohanSald.api.dto.EmployeRequestDTO;
import com.yohanSald.api.dto.EmployeResponseDTO;
import com.yohanSald.api.model.Employe;

public class EmployeMapper {

    private EmployeMapper() {}

    // RequestDTO -> Entite (pour creation / mise a jour)
    public static Employe toEntity(EmployeRequestDTO dto) {
        Employe employe = new Employe();
        employe.setPrenom(dto.getPrenom());
        employe.setNom(dto.getNom());
        employe.setEmail(dto.getEmail());
        employe.setPoste(dto.getPoste());
        employe.setSalaire(dto.getSalaire());
        return employe;
    }

    // Entite -> ResponseDTO (ce qu on expose au client)
    public static EmployeResponseDTO toResponseDTO(Employe employe) {
        EmployeResponseDTO dto = new EmployeResponseDTO();
        dto.setId(employe.getId());
        dto.setPrenom(employe.getPrenom());
        dto.setNom(employe.getNom());
        dto.setEmail(employe.getEmail());
        dto.setPoste(employe.getPoste());
        return dto;
    }
}