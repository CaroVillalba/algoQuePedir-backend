package ar.edu.unsam.algo3.dto

// IngredienteDTO.kt
data class IngredienteDTO(
    val name: String,
    val price: Double,
    val group: String,
    val animalOrigin: Boolean?
)