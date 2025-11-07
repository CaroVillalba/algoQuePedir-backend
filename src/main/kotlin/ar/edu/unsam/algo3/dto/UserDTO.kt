package ar.edu.unsam.algo3.dto

data class UserDTO (
    val id: Int?,
    val name: String,
    val surname: String,
    val username: String,
    val email: String,
    val address: AddressDTO,
)