package ar.edu.unsam.algo2

open class Ingrediente(
    var nombre: String = "",
    var costo: Double = 0.0,
    val grupoAlimentos: GrupoAlimentos
): Entity()  {
    fun origenAnimal() = grupoAlimentos.esDeOrigenAnimal
}
