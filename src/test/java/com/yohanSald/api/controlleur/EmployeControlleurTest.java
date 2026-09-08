package com.yohanSald.api.controlleur;

import com.yohanSald.api.model.Employe;
import com.yohanSald.api.service.EmployeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = EmployeControlleur.class)
class EmployeControlleurTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeService employeService;

    @Test
    public void testGetEmployes_retourne200() throws Exception {
        when(employeService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/employes"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetEmployeById_existant_retourne200() throws Exception {
        Employe employe = new Employe(1L, "Alice", "Martin", "alice@test.com", "Dev", 45000.0);
        when(employeService.findById(1L)).thenReturn(Optional.of(employe));

        mockMvc.perform(get("/api/employes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prenom").value("Alice"));
    }

    @Test
    public void testGetEmployeById_inexistant_retourne404() throws Exception {
        when(employeService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/employes/99"))
                .andExpect(status().isNotFound());
    }
}