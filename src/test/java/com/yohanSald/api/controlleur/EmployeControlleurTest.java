package com.yohanSald.api.controlleur;

import com.yohanSald.api.model.Employe;
import com.yohanSald.api.service.EmployeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.containsString;

@WebMvcTest(controllers = {EmployeControlleur.class, HomeController.class})
class EmployeControlleurTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeService employeService;

    // ── Test 1 : GET / → vue home avec "Laurent" ──
    @Test
    public void testGetHome_retourne200AvecLaurent() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(content().string(containsString("Laurent")));
    }

    // ── Test 2 : GET /api/v1/employes → 200 + JSON HATEOAS ──
    @Test
    public void testGetEmployes_retourne200() throws Exception {
        when(employeService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/employes"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", containsString("max-age=60")));
    }

    // ── Test 3 : GET /api/v1/employes/1 → 200 + liens HATEOAS ──
    @Test
    public void testGetEmployeById_existant_retourne200() throws Exception {
        Employe employe = new Employe(1L, "Alice", "Martin", "alice@test.com", "Dev", 45000.0);
        when(employeService.findById(1L)).thenReturn(Optional.of(employe));

        mockMvc.perform(get("/api/v1/employes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prenom").value("Alice"))
                .andExpect(jsonPath("$.salaire").doesNotExist())
                .andExpect(jsonPath("$._links.self.href").exists())
                .andExpect(jsonPath("$._links.employes.href").exists());
    }

    // ── Test 4 : GET /api/v1/employes/99 → 404 ──
    @Test
    public void testGetEmployeById_inexistant_retourne404() throws Exception {
        when(employeService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/employes/99"))
                .andExpect(status().isNotFound());
    }

    // ── Test 5 : DELETE idempotent → 204 meme si absent ──
    @Test
    public void testDelete_idempotent_retourne204MemeIfAbsent() throws Exception {
        when(employeService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/v1/employes/99"))
                .andExpect(status().isNoContent());
    }

    // ── Test 6 : POST valide → 201 Created ──
    @Test
    public void testCreate_valide_retourne201() throws Exception {
        Employe saved = new Employe(1L, "Bob", "Dupont", "bob@test.com", "DevOps", 50000.0);
        when(employeService.emailDejaUtilise(any())).thenReturn(false);
        when(employeService.save(any())).thenReturn(saved);

        String json = """
                {
                  "prenom": "Bob",
                  "nom": "Dupont",
                  "email": "bob@test.com",
                  "poste": "DevOps",
                  "salaire": 50000
                }
                """;

        mockMvc.perform(post("/api/v1/employes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.prenom").value("Bob"))
                .andExpect(jsonPath("$.salaire").doesNotExist())
                .andExpect(jsonPath("$._links.self.href").exists());
    }

    // ── Test 7 : POST email invalide → 400 + corps d erreur structure ──
    @Test
    public void testCreate_emailInvalide_retourne400Structure() throws Exception {
        String json = """
                {
                  "prenom": "Bob",
                  "nom": "Dupont",
                  "email": "email-invalide",
                  "salaire": 50000
                }
                """;

        mockMvc.perform(post("/api/v1/employes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erreur").value("Erreur de validation"))
                .andExpect(jsonPath("$.details.email").exists());
    }

    // ── Test 8 : POST email deja utilise → 400 ──
    @Test
    public void testCreate_emailDejaUtilise_retourne400() throws Exception {
        when(employeService.emailDejaUtilise("alice@test.com")).thenReturn(true);

        String json = """
                {
                  "prenom": "Alice",
                  "nom": "Copie",
                  "email": "alice@test.com",
                  "salaire": 40000
                }
                """;

        mockMvc.perform(post("/api/v1/employes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}