package ar.edu.unsam.algo3.service


import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.dto.MenuDTO
import ar.edu.unsam.algo3.dto.PlatoDTO
import org.springframework.stereotype.Service
import org.uqbar.geodds.Point
import java.math.RoundingMode
import java.time.LocalDate


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
    fun createPlate(plate: Plato){
        platoRepository.create(plate)
        print("Plato created")
    }
}
