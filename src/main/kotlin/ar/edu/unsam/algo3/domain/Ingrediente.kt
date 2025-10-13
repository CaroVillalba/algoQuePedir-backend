package ar.edu.unsam.algo3.domain

open class Ingrediente(
    var name: String = "",
    var price: Double = 0.0,
    var group: GrupoAlimentos
): Entity()  {
    fun origenAnimal() = group.esDeOrigenAnimal
}
