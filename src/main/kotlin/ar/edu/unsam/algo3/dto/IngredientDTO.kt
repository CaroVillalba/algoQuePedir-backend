package ar.edu.unsam.algo3.dto

data class IngredientDTO(
    val id: Int,
    val name: String,
    val price: Double,
    val group: String,
    val animalOrigin: Boolean?
)