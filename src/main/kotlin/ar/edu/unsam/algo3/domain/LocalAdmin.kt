package ar.edu.unsam.algo3.domain

import java.time.LocalDate

class LocalAdmin(
    firstName: String = "",
    lastName: String = "",
    username: String = "",
    password: String = "",
    registrationDate: LocalDate = LocalDate.now(),
    email: String = "",
    var localId: Int? = null
) : User(firstName, lastName, username, password, registrationDate, email)
