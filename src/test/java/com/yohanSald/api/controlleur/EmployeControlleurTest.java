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

    // ── Test 1 : GET / -> vue home avec "Laurent" ──
    @Test
    public void testGetHome_retourne200AvecLaurent() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(content().string(containsString("Laurent")));
    }

    // ── Test 2 : GET /api/employes -> liste vide OK ──
    @Test
    public void testGetEmployes_retourne200() throws Exception {
        when(employeService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/employes"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    // ── Test 3 : GET /api/employes/1 -> employe trouve ──
    @Test
    public void testGetEmployeById_existant_retourne200() throws Exception {
        Employe employe = new Employe(1L, "Alice", "Martin", "alice@test.com", "Dev", 45000.0);
        when(employeService.findById(1L)).thenReturn(Optional.of(employe));

        mockMvc.perform(get("/api/employes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.prenom").value("Alice"))
                .andExpect(jsonPath("$.nom").value("Martin"))
                .andExpect(jsonPath("$.email").value("alice@test.com"))
                .andExpect(jsonPath("$.salaire").doesNotExist());
    }

    // ── Test 4 : GET /api/employes/99 -> 404 ──
    @Test
    public void testGetEmployeById_inexistant_retourne404() throws Exception {
        when(employeService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/employes/99"))
                .andExpect(status().isNotFound());
    }

    // ── Test idempotence : DELETE 2x -> toujours 204 ──
    @Test
    public void testDelete_idempotent_retourne204MemeIfAbsent() throws Exception {
        // L'employe n'existe pas (deja supprime)
        when(employeService.findById(99L)).thenReturn(Optional.empty());

        // Premier appel ou appel sur ressource inexistante → 204 quand meme
        mockMvc.perform(delete("/api/employes/99"))
                .andExpect(status().isNoContent());
    }

    // ── Test 5 : POST /api/employes -> 201 Created ──
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

        mockMvc.perform(post("/api/employes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.prenom").value("Bob"))
                .andExpect(jsonPath("$.salaire").doesNotExist());
    }

    // ── Test 6 : POST avec email invalide -> 400 ──
    @Test
    public void testCreate_emailInvalide_retourne400() throws Exception {
        String json = """
                {
                  "prenom": "Bob",
                  "nom": "Dupont",
                  "email": "email-invalide",
                  "salaire": 50000
                }
                """;

        mockMvc.perform(post("/api/employes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    // ── Test 7 : POST email deja utilise -> 400 ──
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

        mockMvc.perform(post("/api/employes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}