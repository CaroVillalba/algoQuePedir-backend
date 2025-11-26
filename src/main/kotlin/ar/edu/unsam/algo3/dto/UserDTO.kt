package ar.edu.unsam.algo3.dto


import ar.edu.unsam.algo3.domain.PendingRating
import java.time.LocalDate
import ar.edu.unsam.algo3.domain.Ingredient
import com.jayway.jsonpath.Criteria


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
    val pendingRatings: MutableSet<PendingRatingDTO>? = mutableSetOf(),
    val criteria: CriteriaDTO? = null,
    val preferredRestaurants: MutableSet<LocalAccountDTO>? = mutableSetOf(),
)


data class PendingRatingDTO (
    val localId: Int?,
    val localName: String,
    val localRating: Double,
    val date: LocalDate,
    val localImage: String = ""
)

data class CriteriaDTO(
    val faithful: Boolean,
    val marketing: Boolean,
    val marketingWords: List<String>,
    val vegan: Boolean,
    val exquisite: Boolean,
    val conservative: Boolean,
    val impatient: Boolean,
    val preferredRestaurants: List<LocalAccountDTO>? = null,
    val distance: Int? = null      // si querés incluirlo
)
