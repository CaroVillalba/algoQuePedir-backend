package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.dto.DishDTO
import ar.edu.unsam.algo3.dto.IngredientDTO
import ar.edu.unsam.algo3.domain.LocalRepository
import ar.edu.unsam.algo3.domain.DishRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.* import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.* import org.uqbar.geodds.Point
import java.time.LocalDate
import org.hamcrest.Matchers.*

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Dado un DishController")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DishControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var localRepository: LocalRepository

    @Autowired
    lateinit var dishRepository: DishRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private lateinit var local: Local
    private lateinit var dishDTO: DishDTO

    private lateinit var existingDish: Dish

    @BeforeEach
    fun setup() {
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


        val ingredient = Ingredient("Tomate", 50.0, FoodGroup.FRUTAS_Y_VERDURAS)
        existingDish = Dish(
            "https://example.com/pizza.jpg", "Pizza Test", "Pizza de ejemplo",
            listOf(ingredient),
            local,
            10.0, LocalDate.now(), 5.0, 200.0, 0.0
        )
        dishRepository.create(existingDish)

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

    @Test
    fun `GET v1 dishes debe devolver 200 OK y la lista con el plato creado`() {
        mockMvc.perform(get("/v1/dishes"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()", `is`(4)))
            .andExpect(jsonPath("$[3].name", `is`(existingDish.name)))
    }

    @Test
    fun `POST v1 dishes debe devolver 404 Not Found si el Local ID no existe`() {
        val idLocalQueNoExiste = 999
        val dtoConLocalInvalido = dishDTO.copy(local = idLocalQueNoExiste)
        val json = objectMapper.writeValueAsString(dtoConLocalInvalido)

        mockMvc.perform(post("/v1/dishes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message", `is`("Local con ID 999 no encontrado")))
    }

    @Test
    fun `PUT v1 dishes by ID debe devolver 400 Bad Request si la validacion del DTO falla`() {
        val dishId = existingDish.id
        val dtoInvalido = dishDTO.copy(description = "")
        val json = objectMapper.writeValueAsString(dtoInvalido)

        mockMvc.perform(put("/v1/dishes/$dishId")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message", `is`("La descripción del plato no puede estar vacía")))
    }
}