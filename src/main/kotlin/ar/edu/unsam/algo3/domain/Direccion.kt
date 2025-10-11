package ar.edu.unsam.algo3.domain

import org.uqbar.geodds.Point

//Clase Direccion, con direccion y coordenadas
class Direccion (
    var calle: String = "",
    var altura: String = "",
    var coordenadas: Point
) {
    fun direccion() = calle+" "+altura
}