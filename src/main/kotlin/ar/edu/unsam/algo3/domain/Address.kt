package ar.edu.unsam.algo3.domain

import org.uqbar.geodds.Point

// Clase Address, con calle, altura y coordenadas
class Address(
    var street: String = "",
    var number: String = "",
    var coordinates: Point
) {
    fun fullAddress() = "$street $number"
}
