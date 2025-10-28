package ar.edu.unsam.algo3.mapper

import ar.edu.unsam.algo3.domain.Dish
import ar.edu.unsam.algo3.domain.FoodGroup
import ar.edu.unsam.algo3.domain.Ingredient
import ar.edu.unsam.algo3.domain.Local
import ar.edu.unsam.algo3.dto.DishDTO
import ar.edu.unsam.algo3.dto.IngredientDTO
import ar.edu.unsam.algo3.exceptions.BusinessException
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import java.time.LocalDate

@ExtendWith(MockitoExtension::class)
@DisplayName("Tests para DishMapper")
class DishMapperTest {

    private lateinit var realIngredient: Ingredient
    private lateinit var realIngredientDTO: IngredientDTO
    private lateinit var dummyLocal: Local

    private lateinit var validDTO: DishDTO

    private lateinit var existingDish: Dish

    @BeforeEach
    fun setup() {
        realIngredient = Ingredient("Tomate", 50.0, FoodGroup.FRUTAS_Y_VERDURAS,1)
        realIngredient.id = 1

        realIngredientDTO = IngredientDTO(
            id = 1,
            name = "Tomate",
            price = 50.0,
            group = "FRUTAS_Y_VERDURAS",
            animalOrigin = false
        )

        dummyLocal = mock()

        validDTO = DishDTO(
            imageURL = "https://example.com/image.png",
            name = "Plato de Prueba",
            description = "Descripción válida",
            ingredients = listOf(realIngredientDTO),
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
            ingredients = listOf(realIngredient),
            local = dummyLocal,
            agreedPercentageWithLocal = 5.0,
            releaseDate = LocalDate.now().minusDays(10),
            authorRoyalties = 2.0,
            baseValue = 80.0,
            discount = 0.0
        )
    }

    @Test
    fun `toDTO deberia mapear un Dish a un DishDTO correctamente`() {
        existingDish = spy(existingDish)
        whenever(existingDish.ingredientCost()).thenReturn(50.0)

        val dto = DishMapper.toDTO(existingDish, 99)

        Assertions.assertEquals(existingDish.name, dto.name)
        Assertions.assertEquals(existingDish.description, dto.description)
        Assertions.assertEquals(50.0, dto.productionCost, "Debe usar el valor de ingredientCost()")
        Assertions.assertEquals(99, dto.local, "Debe usar el ID de local pasado por parámetro")
        Assertions.assertEquals(1, dto.ingredients.size)

        Assertions.assertEquals(realIngredient.id, dto.ingredients[0].id)
        Assertions.assertEquals(realIngredient.name, dto.ingredients[0].name)
    }

    @Test
    fun `toMenuDTO deberia mapear una lista de Dish a MenuDTO`() {
        val dish1: Dish = mock {
            on { id } doReturn 1
            on { imageURL } doReturn "url1"
            on { name } doReturn "Plato 1"
            on { description } doReturn "Desc 1"
            on { salePrice() } doReturn 150.0
        }
        val dishes = listOf(dish1)

        val menuDTOs = DishMapper.toMenuDTO(dishes)

        Assertions.assertEquals(1, menuDTOs.size)
        Assertions.assertEquals(1, menuDTOs[0].id)
        Assertions.assertEquals(150.0, menuDTOs[0].price)
    }

    @Test
    fun `toMenuDTO deberia devolver una lista vacia si la entrada esta vacia`() {
        val menuDTOs = DishMapper.toMenuDTO(emptyList())
        Assertions.assertTrue(menuDTOs.isEmpty())
    }

    @Test
    fun `toEntity deberia mapear un DishDTO a una nueva entidad Dish`() {
        val entity = DishMapper.toEntity(validDTO, dummyLocal)

        Assertions.assertEquals(validDTO.name, entity.name)
        Assertions.assertEquals(dummyLocal, entity.local, "Debe usar el objeto Local pasado")
        Assertions.assertEquals(LocalDate.now(), entity.releaseDate, "Debe setear la fecha de alta a hoy")
        Assertions.assertEquals(1, entity.ingredients.size)

        Assertions.assertEquals(realIngredientDTO.name, entity.ingredients[0].name)
        Assertions.assertEquals(realIngredientDTO.price, entity.ingredients[0].price)
    }

    @Test
    fun `updateEntity deberia actualizar una entidad Dish existente`() {
        val updatedEntity = DishMapper.updateEntity(validDTO, existingDish)

        Assertions.assertSame(existingDish, updatedEntity, "Debe modificar la instancia existente")
        Assertions.assertEquals(validDTO.name, updatedEntity.name)
        Assertions.assertNotEquals(LocalDate.now(), updatedEntity.releaseDate, "NO debe cambiar la fecha de alta")
        Assertions.assertEquals(1, updatedEntity.ingredients.size)

        Assertions.assertEquals(realIngredientDTO.name, updatedEntity.ingredients[0].name)
    }

    @Test
    fun `validateDTO deberia lanzar excepcion si el nombre esta vacio`() {
        val invalidDTO = validDTO.copy(name = "   ")

        val exToEntity = assertThrows<BusinessException> { DishMapper.toEntity(invalidDTO, dummyLocal) }
        Assertions.assertEquals("El nombre del plato no puede estar vacío", exToEntity.message)

        val exUpdate = assertThrows<BusinessException> { DishMapper.updateEntity(invalidDTO, existingDish) }
        Assertions.assertEquals("El nombre del plato no puede estar vacío", exUpdate.message)
    }

    @Test
    fun `validateDTO deberia lanzar excepcion si la descripcion esta vacia`() {
        val invalidDTO = validDTO.copy(description = "")
        val ex = assertThrows<BusinessException> { DishMapper.toEntity(invalidDTO, dummyLocal) }
        Assertions.assertEquals("La descripción del plato no puede estar vacía", ex.message)
    }

    @Test
    fun `validateDTO deberia lanzar excepcion si la URL de la imagen no es http`() {
        val invalidDTO = validDTO.copy(imageURL = "ftp://invalid.com/img.png")
        val ex = assertThrows<BusinessException> { DishMapper.toEntity(invalidDTO, dummyLocal) }
        Assertions.assertEquals("La URL de la imagen no es válida", ex.message)
    }

    @Test
    fun `validateDTO deberia lanzar excepcion si el precio base es cero o menor`() {
        val invalidDTO = validDTO.copy(baseValue = 0.0)
        val ex = assertThrows<BusinessException> { DishMapper.toEntity(invalidDTO, dummyLocal) }
        Assertions.assertEquals("El precio base debe ser mayor a cero", ex.message)
    }

    @Test
    fun `validateDTO deberia lanzar excepcion si las regalias son negativas`() {
        val invalidDTO = validDTO.copy(authorRoyalties = -5.0)
        val ex = assertThrows<BusinessException> { DishMapper.toEntity(invalidDTO, dummyLocal) }
        Assertions.assertEquals("Las regalías de autor deben ser mayores o iguales a cero", ex.message)
    }

    @Test
    fun `validateDTO deberia lanzar excepcion si el descuento es negativo`() {
        val invalidDTO = validDTO.copy(discount = -10.0)
        val ex = assertThrows<BusinessException> { DishMapper.toEntity(invalidDTO, dummyLocal) }
        Assertions.assertEquals("El descuento debe ser mayor o igual a cero", ex.message)
    }

    @Test
    fun `validateDTO deberia lanzar excepcion si el porcentaje con local es negativo`() {
        val invalidDTO = validDTO.copy(agreedPercentageWithLocal = -10.0)
        val ex = assertThrows<BusinessException> { DishMapper.toEntity(invalidDTO, dummyLocal) }
        Assertions.assertEquals("El porcentaje acordado con el local debe ser mayor o igual a cero", ex.message)
    }

    @Test
    fun `validateDTO deberia lanzar excepcion si no hay ingredientes`() {
        val invalidDTO = validDTO.copy(ingredients = emptyList())
        val ex = assertThrows<BusinessException> { DishMapper.toEntity(invalidDTO, dummyLocal) }
        Assertions.assertEquals("Debe seleccionar al menos un ingrediente", ex.message)
    }
}