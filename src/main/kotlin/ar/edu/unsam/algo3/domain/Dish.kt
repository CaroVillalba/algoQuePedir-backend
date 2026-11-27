package ar.edu.unsam.algo3.domain
import java.math.RoundingMode
import java.time.Duration
import java.time.LocalDate

class Dish(
    var imageURL: String,
    var name: String,
    var description: String,
    var ingredients: List<Ingredient>,
    var local: Local,
    var agreedPercentageWithLocal: Double,
    var releaseDate: LocalDate,
    var authorRoyalties: Double,
    var baseValue: Double,
    var discount: Double,  //valor fijo, no porcentual
): Entity() {
    //calcula el costo
    fun ingredientCost(): Double = ingredients.sumOf { it.price }

    //cantidad de días que han pasado desde el lanzamiento
    fun daysSinceRelease(): Int = Duration.between(
        releaseDate.atStartOfDay(),
        LocalDate.now().atStartOfDay()
    ).toDays().toInt()

    //calcula el valor
    fun salePrice(): Double {
        var result = salePriceWithoutDiscount()
        if (conditionalDiscount() > 0.0){
            result -= conditionalDiscount()
        }
        return result.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toDouble()
    }

    fun salePriceWithoutDiscount(): Double {
        return ingredientCost() + baseValue + platformCost() + royaltiesCost()
    }

    fun platformCost(): Double = baseValue * (agreedPercentageWithLocal/100.0)
    fun royaltiesCost(): Double = if (isAuthorDish()) baseValue * (authorRoyalties/100.0) else 0.0

    fun conditionalDiscount(): Double {
        val days = daysSinceRelease()

        if (isNew()) {
            if (days <= 20.0){
                return salePriceWithoutDiscount() * ((30.0 - days)/100.0)
            } else {
                return salePriceWithoutDiscount() * 0.1
            }
        } else {
            return salePriceWithoutDiscount() * (discount/100.0)
        }
    }

    fun isNew(): Boolean = daysSinceRelease() <= 30.0

    fun onPromotion(): Boolean = (discount != 0.0)

    fun isAuthorDish(): Boolean = authorRoyalties != 0.0

    fun isVegan(): Boolean {
        return !ingredients.any { it.animalOrigin() }
    }
}
