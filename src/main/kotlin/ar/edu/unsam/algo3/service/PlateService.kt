package ar.edu.unsam.algo3.service


import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.dto.MenuDTO
import org.springframework.stereotype.Service
import java.math.RoundingMode


@Service
class PlateService ( private val platoRepository: DishRepository){
    fun getAll(): List<MenuDTO> {
        val platos = platoRepository.getAll()


        val menuDTOs = platos.map { plato ->
            MenuDTO(
                imageUrl = plato.imageURL,
                name = plato.name,
                description = plato.description,
                price = plato.salePrice().toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toDouble()
            )
        }
        return menuDTOs
    }
    fun createPlate(dish: Dish){
        platoRepository.create(dish)
        print("Plato created")
    }
}
