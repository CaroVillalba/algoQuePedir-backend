package ar.edu.unsam.algo3.mapper

import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.dto.IngredientDTO
import ar.edu.unsam.algo3.dto.MenuDTO
import ar.edu.unsam.algo3.dto.DishDTO
import java.time.LocalDate
import ar.edu.unsam.algo3.exceptions.BusinessException

object DishMapper {
    // Convierte entidad a DTO
    fun toDTO(dish: Dish, local: Int): DishDTO {
        val ingredients: List<IngredientDTO> = dish.ingredients.map{ingredient -> IngredientMapper.toDTO(ingredient)}
        return DishDTO(
            dish.imageURL,
            dish.name,
            dish.description,
            ingredients,
            local,
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
    fun toEntity(dto: DishDTO, local: Local): Dish {
        validateDTO(dto)
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
    fun updateEntity(dto: DishDTO, existing: Dish): Dish {
        validateDTO(dto)
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

    fun validateDTO(dto: DishDTO) {
        if (dto.name.isBlank()) throw BusinessException("El nombre del plato no puede estar vacío")
        if (dto.description.isBlank()) throw BusinessException("La descripción del plato no puede estar vacía")
        if (!dto.imageURL.startsWith("http")) throw BusinessException("La URL de la imagen no es válida")
        if (dto.baseValue <= 0) throw BusinessException("El precio base debe ser mayor a cero")
        if (dto.authorRoyalties < 0) throw BusinessException("Las regalías de autor deben ser mayores o iguales a cero")
        if (dto.discount < 0) throw BusinessException("El descuento debe ser mayor o igual a cero")
        if (dto.agreedPercentageWithLocal < 0) throw BusinessException("El porcentaje acordado con el local debe ser mayor o igual a cero")
        if (dto.ingredients.isEmpty()) throw BusinessException("Debe seleccionar al menos un ingrediente")
    }
}