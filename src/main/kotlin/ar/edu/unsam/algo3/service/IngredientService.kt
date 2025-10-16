package ar.edu.unsam.algo3.service

import ar.edu.unsam.algo3.domain.Ingredient
import ar.edu.unsam.algo3.domain.IngredientRepository
import ar.edu.unsam.algo3.exceptions.ElementDoesNotExistException
import org.springframework.stereotype.Service

@Service
class IngredientService(private val ingredientRepository: IngredientRepository) {

    fun getAll(): List<Ingredient> {
        return ingredientRepository.getAll()
    }

    fun getByIdEntity(id: Int): Ingredient {
        return ingredientRepository.getById(id)
            ?: throw ElementDoesNotExistException("El ingrediente $id no existe.")
    }

    fun create(ingredient: Ingredient) {
        ingredientRepository.create(ingredient)
    }

    fun update(ingredient: Ingredient) {
        ingredientRepository.update(ingredient)
    }

    fun deleteById(id: Int) {
        val ingredient = ingredientRepository.getById(id)
            ?: throw ElementDoesNotExistException("El ingrediente $id no existe.")
        ingredientRepository.delete(ingredient)
    }
}
