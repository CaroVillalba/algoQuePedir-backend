package ar.edu.unsam.algo3.dto

data class DishDTO (
    val imageURL: String,
    val name: String,
    val description: String,
    val ingredients: List<IngredientDTO>,
    val agreedPercentageWithLocal: Double,
    val authorRoyalties: Double,
    val baseValue: Double,
    val discount: Double,
    val totalCost: Double
)

