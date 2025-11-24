package ar.edu.unsam.algo3.dto

import ar.edu.unsam.algo3.domain.Ingredient

data class UserDTO (
    val id: Int?,
    val firstname: String,
    val lastname: String,
    val address: AddressDTO,
    val username: String,
    val password: String,
    val email: String,
    val location: String? = null,
    val preferredIngredients: MutableSet<Ingredient> = mutableSetOf(),
    val ingredientsToAvoid: MutableSet<Ingredient> = mutableSetOf(),
)