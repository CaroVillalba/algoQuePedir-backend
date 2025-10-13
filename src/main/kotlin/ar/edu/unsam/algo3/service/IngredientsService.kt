package ar.edu.unsam.algo3.service

import ar.edu.unsam.algo3.domain.GrupoAlimentos
import ar.edu.unsam.algo3.domain.Ingrediente
import ar.edu.unsam.algo3.domain.IngredienteRepository
import ar.edu.unsam.algo3.domain.PlatoRepository
import ar.edu.unsam.algo3.dto.IngredienteDTO
import ar.edu.unsam.algo3.dto.MenuDTO
import org.springframework.stereotype.Service
import java.math.RoundingMode


@Service
class IngredientsService ( private val ingredienteRepository: IngredienteRepository){
    fun getAll(): List<IngredienteDTO> {
        val ingredients = ingredienteRepository.getAll()
        val ingredientDTO = ingredients.map{ ingredient ->
            IngredienteDTO(
                name = ingredient.name,
                price = ingredient.price,
                group = ingredient.group.toString(),
                animalOrigin = ingredient.group.esDeOrigenAnimal
            )
        }
        return ingredientDTO
    }

    fun getById(id: Int): IngredienteDTO {
        val ingredient = ingredienteRepository.getById(id)
        if (ingredient != null) {
            return IngredienteDTO(
                name = ingredient.name,
                price = ingredient.price,
                group = ingredient.group.toString(),
                animalOrigin = ingredient.origenAnimal()
            )
        }
        else {

        }
    }
}

