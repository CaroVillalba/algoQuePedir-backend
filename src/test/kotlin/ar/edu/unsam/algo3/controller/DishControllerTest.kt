package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.dto.DishDTO
import ar.edu.unsam.algo3.dto.IngredientDTO
import ar.edu.unsam.algo3.domain.LocalRepository
// AÑADIDO: Importar DishRepository para crear un plato de prueba
import ar.edu.unsam.algo3.domain.DishRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals // AÑADIDO: Para verificar la BD
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
// AÑADIDO: Anotación para limpiar la BD entre tests
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
// AÑADIDO: Import estático para 'get', 'post', 'put'
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.* import org.springframework.test.web.servlet.result.MockMvcResultMatchers
// AÑADIDO: Import estático para 'status' y 'jsonPath'
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.* import org.uqbar.geodds.Point
import java.time.LocalDate // AÑADIDO: Necesario para crear un Dish
import org.hamcrest.Matchers.*

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Dado un DishController")
// AÑADIDO: Limpia la BD (y el contexto de Spring) después de CADA test.
// Esto evita que el test de POST afecte al test de GET.
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class DishControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var localRepository: LocalRepository

    // AÑADIDO: Inyectamos el repo de platos
    @Autowired
    lateinit var dishRepository: DishRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private lateinit var local: Local
    private lateinit var dishDTO: DishDTO

    // AÑADIDO: Un plato que ya existe en la BD para probar GET, PUT y DELETE
    private lateinit var existingDish: Dish

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
        localRepository.create(local) // 'local.id' se asigna aquí

        // AÑADIDO: Crear un plato de prueba en la BD
        // (Asumo una definición de Ingredient dummy o importa la real)
        val ingredient = Ingredient("Tomate", 50.0, FoodGroup.FRUTAS_Y_VERDURAS)
        existingDish = Dish(
            "https://example.com/pizza.jpg", "Pizza Test", "Pizza de ejemplo",
            listOf(ingredient),
            local, // Usamos el local real guardado
            10.0, LocalDate.now(), 5.0, 200.0, 0.0
        )
        dishRepository.create(existingDish) // 'existingDish.id' se asigna aquí

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
            local.id, // Usamos el ID real del local guardado
            10.0,
            5.0,
            200.0,
            0.0,
            150.0
        )
    }

    // --- TUS 2 TESTS ORIGINALES (INTACTOS) ---

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

    // --- TESTS AÑADIDOS PARA COBERTURA ---

    @Test
    fun `GET v1 dishes debe devolver 200 OK y la lista con el plato creado`() {
        mockMvc.perform(get("/v1/dishes"))
            .andExpect(status().isOk)
            // Verificamos que la lista tiene 1 elemento (el de setup)
            .andExpect(jsonPath("$.length()", `is`(4)))
            .andExpect(jsonPath("$[3].name", `is`(existingDish.name)))
    }

    @Test
    fun `POST v1 dishes debe devolver 404 Not Found si el Local ID no existe`() {
        val idLocalQueNoExiste = 999
        // Creamos un DTO que apunta a un local inválido
        val dtoConLocalInvalido = dishDTO.copy(local = idLocalQueNoExiste)
        val json = objectMapper.writeValueAsString(dtoConLocalInvalido)

        mockMvc.perform(post("/v1/dishes")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            // El ControllerAdvice debería atrapar ElementDoesNotExistException
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message", `is`("Local con ID 999 no encontrado")))
    }

    @Test
    fun `PUT v1 dishes by ID debe devolver 400 Bad Request si la validacion del DTO falla`() {
        val dishId = existingDish.id
        // El mapper valida que la descripción no esté vacía
        val dtoInvalido = dishDTO.copy(description = "")
        val json = objectMapper.writeValueAsString(dtoInvalido)

        mockMvc.perform(put("/v1/dishes/$dishId")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message", `is`("La descripción del plato no puede estar vacía")))
    }
}