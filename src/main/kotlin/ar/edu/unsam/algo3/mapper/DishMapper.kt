package ar.edu.unsam.algo3.mapper

import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.dto.IngredientDTO
import ar.edu.unsam.algo3.dto.MenuDTO
import ar.edu.unsam.algo3.dto.PlatoDTO
import ar.edu.unsam.algo3.dto.DishResponseDTO
import java.time.LocalDate

object DishMapper {
    // Convierte entidad a DTO
    fun toDTO(dish: Dish): DishResponseDTO {
        val ingredients: List<IngredientDTO> = dish.ingredients.map{ingredient -> IngredientMapper.toDTO(ingredient)}
        return DishResponseDTO(
            dish.imageURL,
            dish.name,
            dish.description,
            ingredients,
            dish.agreedPercentageWithLocal,
            dish.authorRoyalties,
            dish.baseValue,
            dish.discount
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
        val ingredient = dto.ingredientes.map {ingredient -> IngredientMapper.toEntity(ingredient)}
        return Dish(
            dto.imageURL,
            dto.nombre,
            dto.descripcion,
            ingredient,
            local,
            dto.agreedPercentageWithLocal,
            LocalDate.now(),
            dto.regaliasAutor,
            dto.valorBase,
            dto.descuento,
        )
    }

    // Actualiza una entidad existente a partir de un DTO
    fun updateEntity(dto: PlatoDTO, existing: Dish): Dish {
        val ingredient = dto.ingredientes.map {ingredient -> IngredientMapper.toEntity(ingredient)}

        existing.imageURL = dto.imageURL
        existing.name = dto.nombre
        existing.description = dto.descripcion
        existing.ingredients = ingredient
        existing.agreedPercentageWithLocal = dto.agreedPercentageWithLocal
        existing.authorRoyalties = dto.regaliasAutor
        existing.baseValue = dto.valorBase
        existing.discount = dto.descuento

        return existing
    }
}