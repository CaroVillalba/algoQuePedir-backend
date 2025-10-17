package ar.edu.unsam.algo3.dto

data class DishResponseDTO (
    val imageURL: String,
    val nombre: String,
    val descripcion: String,
    val ingredientes: List<IngredientDTO>,
    val agreedPercentageWithLocal: Double,
    val regaliasAutor: Double,
    val valorBase: Double,
    val descuento: Double,
)

