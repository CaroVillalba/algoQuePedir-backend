package ar.edu.unsam.algo3.mapper

import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.dto.IngredientDTO
import ar.edu.unsam.algo3.dto.MenuDTO
import ar.edu.unsam.algo3.dto.PlatoDTO
import ar.edu.unsam.algo3.dto.DishDTO
import java.time.LocalDate

object DishMapper {
    // Convierte entidad a DTO
    fun toDTO(dish: Dish): DishDTO {
        val ingredients: List<IngredientDTO> = dish.ingredients.map{ingredient -> IngredientMapper.toDTO(ingredient)}
        return DishDTO(
            dish.imageURL,
            dish.name,
            dish.description,
            ingredients,
            dish.agreedPercentageWithLocal,
            dish.authorRoyalties,
            dish.baseValue,
            dish.discount,
            dish.salePrice()
        )
    }

    // Convierte entidad a DTO
    fun toMenuDTO(dishes: List<Dish>): List<MenuDTO> {
        return dishes.map{dish -> MenuDTO(
            dish.id,
            dish.imageURL,
            dish.name,
            dish.description,
            dish.salePrice()
        )}
    }

    // Convierte DTO a nueva entidad
    fun toEntity(dto: PlatoDTO, local: Local): Dish {
        val ingredient = dto.ingredients.map {ingredient -> IngredientMapper.toEntity(ingredient)}
        return Dish(
            dto.imageURL,
            dto.name,
            dto.description,
            ingredient,
            local,
            dto.agreedPercentageWithLocal,
            LocalDate.now(),
            dto.authorRoyalties,
            dto.baseValue,
            dto.discount,
        )
    }

    // Actualiza una entidad existente a partir de un DTO
    fun updateEntity(dto: PlatoDTO, existing: Dish): Dish {
        val ingredient = dto.ingredients.map {ingredient -> IngredientMapper.toEntity(ingredient)}

        existing.imageURL = dto.imageURL
        existing.name = dto.name
        existing.description = dto.description
        existing.ingredients = ingredient
        existing.agreedPercentageWithLocal = dto.agreedPercentageWithLocal
        existing.authorRoyalties = dto.authorRoyalties
        existing.baseValue = dto.baseValue
        existing.discount = dto.discount

        return existing
    }
}