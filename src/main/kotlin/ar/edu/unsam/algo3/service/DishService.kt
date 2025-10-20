package ar.edu.unsam.algo3.service


import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.exceptions.ElementDoesNotExistException
import org.springframework.stereotype.Service
import ar.edu.unsam.algo3.exceptions.BusinessException

@Service
class DishService(
    private val platoRepository: DishRepository,
    private val localRepository: LocalRepository
) {
    fun getAll(): List<Dish> {
        return platoRepository.getAll()
    }

    fun createDish(dish: Dish) {
        platoRepository.create(dish)
    }

    fun getById(id: Int): Dish {
        return platoRepository.getById(id) ?: throw ElementDoesNotExistException("Plato no encontrado")
    }

    fun update(dish: Dish) {
        platoRepository.update(dish)
    }
}
