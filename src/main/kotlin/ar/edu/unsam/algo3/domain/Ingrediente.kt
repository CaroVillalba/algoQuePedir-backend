package ar.edu.unsam.algo3.domain

open class Ingrediente(
    var nombre: String = "",
    var costo: Double = 0.0,
    val grupoAlimentos: GrupoAlimentos
): Entity()  {
    fun origenAnimal() = grupoAlimentos.esDeOrigenAnimal
}
