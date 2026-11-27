package ar.edu.unsam.algo3.service


import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ar.edu.unsam.algo3.domain.Dish
import ar.edu.unsam.algo3.domain.DishRepository
import ar.edu.unsam.algo3.exceptions.ElementDoesNotExistException

@ExtendWith(MockitoExtension::class)
class DishServiceTest {
    @Mock
    private lateinit var platoRepository: DishRepository
    @InjectMocks
    private lateinit var dishService: DishService

    @Test
    fun `getAll deberia retornar la lista de platos del repositorio`() {
        val mockDish1: Dish = mock()
        val mockDish2: Dish = mock()
        val mockDishes = listOf(mockDish1, mockDish2)
        whenever(platoRepository.getAll()).thenReturn(mockDishes)

        val result = dishService.getAll()
        assertNotNull(result)
        assertEquals(2, result.size, "El tamaño de la lista debe ser 2")
        assertEquals(mockDishes, result, "La lista retornada debe ser la misma que la del repositorio")
        verify(platoRepository).getAll()
    }

    @Test
    fun `createDish deberia llamar al metodo create del repositorio`() {
        val newDish: Dish = mock()
        dishService.createDish(newDish)
        verify(platoRepository).create(newDish)
    }

    @Test
    fun `getById deberia retornar un plato cuando se encuentra`() {
        val dishId = 1
        val mockDish: Dish = mock()
        whenever(platoRepository.getById(dishId)).thenReturn(mockDish)
        val result = dishService.getById(dishId)
        assertNotNull(result)
        assertEquals(mockDish, result, "El plato retornado debe ser el mock")
        verify(platoRepository).getById(dishId)
    }

    @Test
    fun `getById deberia lanzar ElementDoesNotExistException cuando el plato no se encuentra`() {
        val dishId = 99
        whenever(platoRepository.getById(dishId)).thenReturn(null)
        val exception = assertThrows<ElementDoesNotExistException> {
            dishService.getById(dishId)
        }
        assertEquals("Plato no encontrado", exception.message)
        verify(platoRepository).getById(dishId)
    }

    @Test
    fun `update deberia llamar al metodo update del repositorio`() {
        val updatedDish: Dish = mock()
        dishService.update(updatedDish)
        verify(platoRepository).update(updatedDish)
    }
}