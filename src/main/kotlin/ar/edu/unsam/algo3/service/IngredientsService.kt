package ar.edu.unsam.algo3.service

import ar.edu.unsam.algo3.domain.FoodGroup
import ar.edu.unsam.algo3.domain.Ingredient
import ar.edu.unsam.algo3.domain.IngredientRepository
import ar.edu.unsam.algo3.dto.IngredienteDTO
import ar.edu.unsam.algo3.exceptions.ElementDoesNotExistException
import org.springframework.stereotype.Service


@Service
class IngredientsService ( private val ingredienteRepository: IngredientRepository){
    fun getAll(): List<IngredienteDTO> {
        val ingredients = ingredienteRepository.getAll()
        val ingredientDTO = ingredients.map{ ingredient ->
            IngredienteDTO(
                name = ingredient.name,
                price = ingredient.price,
                group = ingredient.group.toString(),
                animalOrigin = ingredient.group.isAnimalOrigin
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
                animalOrigin = ingredient.animalOrigin()
            )
        }
        else throw ElementDoesNotExistException("El ingrediente ${id} no existe.")
    }

    fun create(ingredient: IngredienteDTO) {
        ingredienteRepository.create(Ingredient(
            name = ingredient.name,
            price = ingredient.price,
            group = FoodGroup.valueOf(ingredient.group)
        ))
    }

    fun update(id: Int, ingredient: IngredienteDTO) {
        ingredienteRepository.update(Ingredient(
            name = ingredient.name,
            price = ingredient.price,
            group = FoodGroup.valueOf(ingredient.group)
        ).apply { this.id = id })
    }

    fun delete(id: Int) {
        val ingredient = ingredienteRepository.getById(id)
        if (ingredient != null) {
            ingredienteRepository.delete(ingredient)
        }
        else {
            throw ElementDoesNotExistException("El ingrediente ${id} no existe.")
        }
    }
}

