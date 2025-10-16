package ar.edu.unsam.algo3.dto

data class IngredienteDTO(
    val id: Int,
    val name: String,
    val price: Double,
    val group: String,
    val animalOrigin: Boolean?
)