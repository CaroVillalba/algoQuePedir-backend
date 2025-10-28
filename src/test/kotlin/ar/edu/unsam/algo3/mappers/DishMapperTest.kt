package ar.edu.unsam.algo3.mapper

import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.dto.IngredientDTO
import ar.edu.unsam.algo3.dto.DishDTO
import ar.edu.unsam.algo3.exceptions.BusinessException
// import org.junit.jupiter.api.AfterEach // Ya no es necesario
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.* // Aún lo necesitamos para 'mock()' y 'spy()'
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
@DisplayName("Tests para DishMapper")
class DishMapperTest {

    // 1. Dummys y Mocks
    private lateinit var realIngredient: Ingredient
    private lateinit var realIngredientDTO: IngredientDTO
    private lateinit var dummyLocal: Local // Mockear Local está bien, es una 'class'

    // DTO base válido para copiar y crear casos inválidos
    private lateinit var validDTO: DishDTO

    // Entidad base para probar actualizaciones
    private lateinit var existingDish: Dish

    @BeforeEach
    fun setup() {
        // --- ESTA ES LA PARTE IMPORTANTE ---
        // 1. NO mockeamos IngredientMapper
        // 2. Creamos objetos REALES para el mapper

        // (Asumo FoodType es un enum como en los tests anteriores)
        realIngredient = Ingredient("Tomate", 50.0, FoodGroup.FRUTAS_Y_VERDURAS)
        realIngredient.id = 1 // Asignamos ID para el test

        realIngredientDTO = IngredientDTO(
            id = 1,
            name = "Tomate",
            price = 50.0,
            group = "FRUTAS_Y_VERDURAS", // Debe coincidir con el enum
            animalOrigin = false
        )

        // Mockeamos Local porque no nos interesa su lógica aquí
        dummyLocal = mock()

        // 3. Crear objetos de prueba
        validDTO = DishDTO(
            imageURL = "https://example.com/image.png",
            name = "Plato de Prueba",
            description = "Descripción válida",
            ingredients = listOf(realIngredientDTO), // Lista real
            local = 1,
            agreedPercentageWithLocal = 10.0,
            authorRoyalties = 5.0,
            baseValue = 100.0,
            discount = 0.0,
            productionCost = 50.0
        )

        existingDish = Dish(
            imageURL = "http://original.url/img.jpg",
            name = "Plato Original",
            description = "Descripción original",
            ingredients = listOf(realIngredient), // Lista real
            local = dummyLocal,
            agreedPercentageWithLocal = 5.0,
            releaseDate = LocalDate.now().minusDays(10),
            authorRoyalties = 2.0,
            baseValue = 80.0,
            discount = 0.0
        )
    }

    // --- Tests de Mapeo (Caminos Felices) ---

    @Test
    fun `toDTO deberia mapear un Dish a un DishDTO correctamente`() {
        // Arrange
        // Creamos un 'spy' para simular solo la función ingredientCost()
        // 'spy' usa el objeto real PERO nos deja mockear algunos métodos
        existingDish = spy(existingDish)
        whenever(existingDish.ingredientCost()).thenReturn(50.0)

        // Act
        // Usamos el IngredientMapper REAL
        val dto = DishMapper.toDTO(existingDish, 99)

        // Assert
        assertEquals(existingDish.name, dto.name)
        assertEquals(existingDish.description, dto.description)
        assertEquals(50.0, dto.productionCost, "Debe usar el valor de ingredientCost()")
        assertEquals(99, dto.local, "Debe usar el ID de local pasado por parámetro")
        assertEquals(1, dto.ingredients.size)

        // Comprobamos los valores del DTO de ingrediente real
        assertEquals(realIngredient.id, dto.ingredients[0].id)
        assertEquals(realIngredient.name, dto.ingredients[0].name)
    }

    @Test
    fun `toMenuDTO deberia mapear una lista de Dish a MenuDTO`() {
        // (Este test no depende de IngredientMapper, funcionará igual)
        val dish1: Dish = mock {
            on { id } doReturn 1
            on { imageURL } doReturn "url1"
            on { name } doReturn "Plato 1"
            on { description } doReturn "Desc 1"
            on { salePrice() } doReturn 150.0
        }
        val dishes = listOf(dish1)

        val menuDTOs = DishMapper.toMenuDTO(dishes)

        assertEquals(1, menuDTOs.size)
        assertEquals(1, menuDTOs[0].id)
        assertEquals(150.0, menuDTOs[0].price)
    }

    @Test
    fun `toMenuDTO deberia devolver una lista vacia si la entrada esta vacia`() {
        val menuDTOs = DishMapper.toMenuDTO(emptyList())
        assertTrue(menuDTOs.isEmpty())
    }

    @Test
    fun `toEntity deberia mapear un DishDTO a una nueva entidad Dish`() {
        // Act
        // Usamos el IngredientMapper REAL
        val entity = DishMapper.toEntity(validDTO, dummyLocal)

        // Assert
        assertEquals(validDTO.name, entity.name)
        assertEquals(dummyLocal, entity.local, "Debe usar el objeto Local pasado")
        assertEquals(LocalDate.now(), entity.releaseDate, "Debe setear la fecha de alta a hoy")
        assertEquals(1, entity.ingredients.size)

        // Comprobamos los valores de la entidad Ingrediente real
        assertEquals(realIngredientDTO.name, entity.ingredients[0].name)
        assertEquals(realIngredientDTO.price, entity.ingredients[0].price)
    }

    @Test
    fun `updateEntity deberia actualizar una entidad Dish existente`() {
        // Act
        // Usamos el IngredientMapper REAL
        val updatedEntity = DishMapper.updateEntity(validDTO, existingDish)

        // Assert
        assertSame(existingDish, updatedEntity, "Debe modificar la instancia existente")
        assertEquals(validDTO.name, updatedEntity.name)
        assertNotEquals(LocalDate.now(), updatedEntity.releaseDate, "NO debe cambiar la fecha de alta")
        assertEquals(1, updatedEntity.ingredients.size)

        // Comprobamos los valores de la entidad Ingrediente real
        assertEquals(realIngredientDTO.name, updatedEntity.ingredients[0].name)
    }

    // --- Tests de Validación (Caminos Tristes) ---
    // (Estos tests no dependen de IngredientMapper, deberían funcionar igual)

    @Test
    fun `validateDTO deberia lanzar excepcion si el nombre esta vacio`() {
        val invalidDTO = validDTO.copy(name = "   ")

        val exToEntity = assertThrows<BusinessException> { DishMapper.toEntity(invalidDTO, dummyLocal) }
        assertEquals("El nombre del plato no puede estar vacío", exToEntity.message)

        val exUpdate = assertThrows<BusinessException> { DishMapper.updateEntity(invalidDTO, existingDish) }
        assertEquals("El nombre del plato no puede estar vacío", exUpdate.message)
    }

    @Test
    fun `validateDTO deberia lanzar excepcion si la descripcion esta vacia`() {
        val invalidDTO = validDTO.copy(description = "")
        val ex = assertThrows<BusinessException> { DishMapper.toEntity(invalidDTO, dummyLocal) }
        assertEquals("La descripción del plato no puede estar vacía", ex.message)
    }

    @Test
    fun `validateDTO deberia lanzar excepcion si la URL de la imagen no es http`() {
        val invalidDTO = validDTO.copy(imageURL = "ftp://invalid.com/img.png")
        val ex = assertThrows<BusinessException> { DishMapper.toEntity(invalidDTO, dummyLocal) }
        assertEquals("La URL de la imagen no es válida", ex.message)
    }

    @Test
    fun `validateDTO deberia lanzar excepcion si el precio base es cero o menor`() {
        val invalidDTO = validDTO.copy(baseValue = 0.0)
        val ex = assertThrows<BusinessException> { DishMapper.toEntity(invalidDTO, dummyLocal) }
        assertEquals("El precio base debe ser mayor a cero", ex.message)
    }

    @Test
    fun `validateDTO deberia lanzar excepcion si las regalias son negativas`() {
        val invalidDTO = validDTO.copy(authorRoyalties = -5.0)
        val ex = assertThrows<BusinessException> { DishMapper.toEntity(invalidDTO, dummyLocal) }
        assertEquals("Las regalías de autor deben ser mayores o iguales a cero", ex.message)
    }

    @Test
    fun `validateDTO deberia lanzar excepcion si el descuento es negativo`() {
        val invalidDTO = validDTO.copy(discount = -10.0)
        val ex = assertThrows<BusinessException> { DishMapper.toEntity(invalidDTO, dummyLocal) }
        assertEquals("El descuento debe ser mayor o igual a cero", ex.message)
    }

    @Test
    fun `validateDTO deberia lanzar excepcion si el porcentaje con local es negativo`() {
        val invalidDTO = validDTO.copy(agreedPercentageWithLocal = -10.0)
        val ex = assertThrows<BusinessException> { DishMapper.toEntity(invalidDTO, dummyLocal) }
        assertEquals("El porcentaje acordado con el local debe ser mayor o igual a cero", ex.message)
    }

    @Test
    fun `validateDTO deberia lanzar excepcion si no hay ingredientes`() {
        val invalidDTO = validDTO.copy(ingredients = emptyList())
        val ex = assertThrows<BusinessException> { DishMapper.toEntity(invalidDTO, dummyLocal) }
        assertEquals("Debe seleccionar al menos un ingrediente", ex.message)
    }
}