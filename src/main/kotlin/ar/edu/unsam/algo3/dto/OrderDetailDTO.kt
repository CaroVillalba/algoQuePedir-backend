package ar.edu.unsam.algo3.dto

data class OrderDetailDTO(
    val id: Int,
    val imageUrl: String,
    val name: String,
    val description: String,
    val price: Double,
    val cant: Int
)