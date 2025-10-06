package ar.edu.unsam.algo2

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period

abstract class Usuario(
    var nombre: String = "",
    var apellido: String = "",
    var username: String = "",
    var password: String = "",
    var fechaNacimiento: LocalDate? = null,
    var fechaRegistro: LocalDate = LocalDate.now(),
    var mail: String = "",
): Entity() {
    fun calcularEdad(): Int {
        val hoy = LocalDate.now()
        return Period.between(fechaNacimiento, hoy).years
    }

    val edad: Int
        get() = calcularEdad()
}