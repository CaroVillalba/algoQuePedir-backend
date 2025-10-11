package ar.edu.unsam.algo3.service


import ar.edu.unsam.algo3.domain.PlatoRepository
import ar.edu.unsam.algo3.dto.MenuDTO
import org.springframework.stereotype.Service
import java.math.RoundingMode


@Service
class PlateService ( private val platoRepository: PlatoRepository){
    fun getAll(): List<MenuDTO> {
        val platos = platoRepository.getAll()


        val menuDTOs = platos.map { plato ->
            MenuDTO(
                imageUrl = plato.imageURL,
                name = plato.nombre,
                description = plato.descripcion,
                price = plato.valorDeVenta().toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toDouble()
            )
        }
        return menuDTOs
    }
}
