package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.dto.DishDTO
import ar.edu.unsam.algo3.dto.IngredientDTO
import ar.edu.unsam.algo3.domain.LocalRepository
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
import org.uqbar.geodds.Point

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Dado un DishController")
class DishControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var localRepository: LocalRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private lateinit var local: Local
    private lateinit var dishDTO: DishDTO

    @BeforeEach
    fun setup() {
        // Crear local de ejemplo
        val address = Address("Calle Falsa", "123", Point(10.0, 20.0))
        local = Local(
            "Local Test",
            "https://example.com/local.jpg",
            address,
            10.0,
            5.0,
            mutableSetOf(PaymentMethod.EFECTIVO)
        )
        localRepository.create(local)

        // Crear DishDTO de ejemplo
        val ingredientDTOs = listOf(
            IngredientDTO(1, "Tomate", 50.0, "FRUTAS_Y_VERDURAS", false),
            IngredientDTO(2, "Queso", 100.0, "LACTEOS", true)
        )
        dishDTO = DishDTO(
            "https://example.com/pizza.jpg",
            "Pizza Test",
            "Pizza de ejemplo",
            ingredientDTOs,
            local.id,
            10.0,
            5.0,
            200.0,
            0.0,
            150.0
        )
    }

    @Test
    fun `GET v1 dishes debe devolver 200 OK`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/dishes"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `POST v1 dishes debe crear un plato y devolver 200 OK`() {
        val json = objectMapper.writeValueAsString(dishDTO)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/v1/dishes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(MockMvcResultMatchers.status().isOk)
    }
}
