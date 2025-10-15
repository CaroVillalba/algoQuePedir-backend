package ar.edu.unsam.algo3.domain

open class Ingredient(
    var name: String = "",
    var price: Double = 0.0,
    var group: FoodGroups
): Entity()  {
    fun animalOrigin() = group.isAnimalOrigin
}
