package ar.edu.unsam.algo3.dto

data class PlatoDTO(
    val imageURL: String,
    val name: String,
    val description: String,
    val ingredients: List<IngredientDTO>,
    val agreedPercentageWithLocal: Double,
    val local: Int,
    val authorRoyalties: Double,
    val baseValue: Double,
    val discount: Double,
)



