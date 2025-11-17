package ar.edu.unsam.algo3.domain

import java.time.LocalDate
import java.time.Period

abstract class User(
    var firstname: String = "",
    var lastname: String = "",
    var username: String = "",
    var password: String = "",
    var registrationDate: LocalDate = LocalDate.now(),
    var email: String = "",
    var birthDate: LocalDate = LocalDate.now(),
) : Entity() {



    fun calculateAge(): Int {
        val today = LocalDate.now()
        return Period.between(birthDate, today).years
    }

    val age: Int
        get() = calculateAge()
}
