package ar.edu.unsam.algo3.service


import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.exceptions.ElementDoesNotExistException
import org.springframework.stereotype.Service

@Service
class DishService(
    private val dishRepository: DishRepository,
) {
    fun getAll(): List<Dish> {
        return dishRepository.getAll()
    }

    fun createDish(dish: Dish) {
        dishRepository.create(dish)
    }

    fun getById(id: Int): Dish {
        return dishRepository.getById(id) ?: throw ElementDoesNotExistException("Plato no encontrado")
    }

    fun getByLocal(localId: Int): List<Dish> {
        return dishRepository.findByLocal(localId) ?: throw ElementDoesNotExistException("No se encontraron platos para el local con ID $localId")
    }

    fun update(dish: Dish) {
        dishRepository.update(dish)
    }
}
