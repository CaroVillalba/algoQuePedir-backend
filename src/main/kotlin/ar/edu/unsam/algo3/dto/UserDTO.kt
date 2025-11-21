package ar.edu.unsam.algo3.dto

data class UserDTO (
    val id: Int?,
    val firstname: String,
    val lastname: String,
    val address: AddressDTO,
    val username: String,
    val password: String,
    val email: String,
    val location: String? = null,
)