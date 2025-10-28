package ar.edu.unsam.algo3.mapper

import ar.edu.unsam.algo3.domain.FoodGroup
import ar.edu.unsam.algo3.domain.Ingredient
import ar.edu.unsam.algo3.dto.IngredientDTO
import ar.edu.unsam.algo3.exceptions.BusinessException

object IngredientMapper {
    // Convierte entidad a DTO
    fun toDTO(ingredient: Ingredient): IngredientDTO {
        return IngredientDTO(
            ingredient.id,
            ingredient.name,
            ingredient.price,
            ingredient.group.toString(),
            ingredient.animalOrigin()
        )
    }

    // Convierte DTO a nueva entidad
    fun toEntity(dto: IngredientDTO): Ingredient {
        validateDTO(dto)
        return Ingredient(
            dto.name,
            dto.price,
            FoodGroup.valueOf(dto.group),
            dto.id
        )
    }

    // Actualiza una entidad existente a partir de un DTO
    fun updateEntity(dto: IngredientDTO, existing: Ingredient): Ingredient {
        validateDTO(dto)
        existing.name = dto.name
        existing.price = dto.price
        existing.group = FoodGroup.valueOf(dto.group)

        return existing
    }

    fun validateDTO(dto: IngredientDTO) {
        if (dto.name.isBlank()) throw BusinessException("El nombre del ingrediente no puede estar vacío")
        if (dto.price <= 0) throw BusinessException("El precio debe ser mayor a cero")
        try {
            FoodGroup.valueOf(dto.group)
        } catch (e: IllegalArgumentException) {
            throw BusinessException("Grupo de alimento inválido: ${dto.group}")
        }
    }
}