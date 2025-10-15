package ar.edu.unsam.algo3.dto

data class LocalAccountDTO(
    val name: String,
    val imgURL: String,
    val address: DireccionDTO,
    val appCommission: Double,
    val authorCommission: Double,
    val paymentMethods: List<String>
)