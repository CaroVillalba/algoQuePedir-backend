package ar.edu.unsam.algo3.dto


import ar.edu.unsam.algo3.domain.PendingRating
import java.time.LocalDate
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
    val pendingRatings: MutableSet<PendingRatingDTO>
)


data class PendingRatingDTO (
    val localId: Int?,
    val localName: String,
    val localRating: Double,
    val date: LocalDate,
    val localImage: String = ""
)
