package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.dto.IngredientDTO
import ar.edu.unsam.algo3.domain.IngredientRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Dado un IngredientController")
class IngredientControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var ingredientRepository: IngredientRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private lateinit var ingredientDTO: IngredientDTO

    @BeforeEach
    fun setup() {
        // Limpiar repository antes de cada test
        ingredientRepository.getAll().forEach {
            ingredientRepository.delete(it)
        }

        // Crear un IngredientDTO de ejemplo (ID irrelevante, repo lo asignará)
        ingredientDTO = IngredientDTO(
            0,
            "Tomate",
            50.0,
            "FRUTAS_Y_VERDURAS",
            false
        )
    }

    @Test
    fun `GET v1 ingredients debe devolver 200 OK`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/ingredients"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `POST v1 ingredients debe crear un ingrediente y devolver 200 OK`() {
        val json = objectMapper.writeValueAsString(ingredientDTO)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/v1/ingredients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `GET v1 ingredients {id} debe devolver 200 OK`() {
        // Primero crear el ingrediente
        val json = objectMapper.writeValueAsString(ingredientDTO)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/v1/ingredients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )

        // Obtener el ID real del ingrediente recién creado
        val ingredientInRepo = ingredientRepository.getAll().first { it.name == "Tomate" }
        val id = ingredientInRepo.id

        // Hacer GET usando el ID real
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/ingredients/$id"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Tomate"))
    }

    @Test
    fun `DELETE v1 ingredients {id} debe eliminar el ingrediente y devolver 200 OK`() {
        val json = objectMapper.writeValueAsString(ingredientDTO)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/v1/ingredients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )

        val ingredientInRepo = ingredientRepository.getAll().first { it.name == "Tomate" }
        val id = ingredientInRepo.id

        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/ingredients/$id"))
            .andExpect(MockMvcResultMatchers.status().isOk)

        val remaining = ingredientRepository.getAll().find { it.id == id }
        assert(remaining == null)
    }
}