package ar.edu.unsam.algo3.service


import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.dto.MenuDTO
import ar.edu.unsam.algo3.dto.PlatoDTO
import ar.edu.unsam.algo3.exceptions.ElementDoesNotExistException
import ar.edu.unsam.algo3.mapper.DishMapper
import org.springframework.stereotype.Service
import java.math.RoundingMode


@Service
class DishService (private val platoRepository: DishRepository, private val localRepository: LocalRepository){
    fun getAll(): List<Dish> {
        val platos = platoRepository.getAll()
        return platos
    }

    fun createPlate(dish: Dish){
        platoRepository.create(dish)
    }

    fun getById(id: Int): Dish {
        val dish = platoRepository.getById(id) ?: throw ElementDoesNotExistException("Plato no encontrado")
        return dish
    }

    fun update(dish: Dish){
        platoRepository.update(dish)
    }
}
