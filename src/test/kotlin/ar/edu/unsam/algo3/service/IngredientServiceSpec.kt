import ar.edu.unsam.algo3.domain.FoodGroup
import ar.edu.unsam.algo3.service.IngredientService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import ar.edu.unsam.algo3.domain.Ingredient
import ar.edu.unsam.algo3.exceptions.ElementDoesNotExistException
import org.junit.jupiter.api.assertThrows
import java.util.*




class ElementDoesNotExistException(message: String) : RuntimeException(message)


class IngredientServiceTest {


    private val ingredientRepository: ar.edu.unsam.algo3.domain.IngredientRepository = mockk()


    private val ingredientService = IngredientService(ingredientRepository)


    private val ingredient1 = Ingredient("harina", 1.0, FoodGroup.LACTEOS,  null)
    private val ingredient2 = Ingredient("yupi",2.0, FoodGroup.LACTEOS, null)
    private val ingredientList = listOf(ingredient1, ingredient2)


    @Test
    fun `getAll debe retornar todos los ingredientes del repositorio`() {

        every { ingredientRepository.getAll() } returns ingredientList


        val result = ingredientService.getAll()
        assertEquals(ingredientList, result)
        verify(exactly = 1) { ingredientRepository.getAll() }
    }

    @Test
    fun `getByIdEntity debe retornar el ingrediente si existe`() {
        val idToFind = 1
        every { ingredientRepository.getById(idToFind) } returns ingredient1

        val result = ingredientService.getByIdEntity(idToFind)

        assertNotNull(result)
        assertEquals(ingredient1, result)
        verify(exactly = 1) { ingredientRepository.getById(idToFind) }
    }

    @Test
    fun `getByIdEntity debe lanzar ElementDoesNotExistException si el ingrediente no existe`() {
        val idToFind = 99
        every { ingredientRepository.getById(idToFind) } returns null

        val exception = assertThrows<ElementDoesNotExistException> {
            ingredientService.getByIdEntity(idToFind)
        }

        assertEquals("El ingrediente $idToFind no existe.", exception.message)
        verify(exactly = 1) { ingredientRepository.getById(idToFind) }
    }

    @Test
    fun `create debe llamar al método create del repositorio con el ingrediente proporcionado`() {
        val newIngredient = Ingredient("papa", 12.3, FoodGroup.LACTEOS, null)

        every { ingredientRepository.create(newIngredient) } returns Unit


        ingredientService.create(newIngredient)


        verify(exactly = 1) { ingredientRepository.create(newIngredient) }
    }


    @Test
    fun `update debe llamar al método update del repositorio con el ingrediente proporcionado`() {
        val updatedIngredient = Ingredient( "Harina Integral", 1.2, FoodGroup.PROTEINAS, null)

        every { ingredientRepository.update(updatedIngredient) } returns Unit
        ingredientService.update(updatedIngredient)
        verify(exactly = 1) { ingredientRepository.update(updatedIngredient) }
    }

    // --- Tests para deleteById(id: Int) ---
    @Test
    fun `deleteById debe eliminar el ingrediente si existe`() {
        val idToDelete = 1

        every { ingredientRepository.getById(idToDelete) } returns ingredient1
        every { ingredientRepository.delete(ingredient1) } returns Unit


        ingredientService.deleteById(idToDelete)

        verify(exactly = 1) { ingredientRepository.getById(idToDelete) }
        verify(exactly = 1) { ingredientRepository.delete(ingredient1) }
    }

    @Test
    fun `deleteById debe lanzar ElementDoesNotExistException si el ingrediente a eliminar no existe`() {
        val idToDelete = 99
        every { ingredientRepository.getById(idToDelete) } returns null

        val exception = assertThrows<ElementDoesNotExistException> {
            ingredientService.deleteById(idToDelete)
        }

        assertEquals("El ingrediente $idToDelete no existe.", exception.message)
        verify(exactly = 1) { ingredientRepository.getById(idToDelete) }
        verify(exactly = 0) { ingredientRepository.delete(any()) }
    }
}