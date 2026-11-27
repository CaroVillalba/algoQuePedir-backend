package ar.edu.unsam.algo3.dto

data class AuthDTO (
    val id: Int?,
    val username: String,
    val password: String,
    val localId: Int?
)