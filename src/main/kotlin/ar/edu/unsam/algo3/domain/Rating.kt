package ar.edu.unsam.algo3.domain

import java.time.LocalDate
import java.time.Period

class PendingRating(
    val local: Local,
    var lastOrderDate: LocalDate = LocalDate.now()
) {

    fun canRate(): Boolean = Period.between(lastOrderDate, LocalDate.now()).days < 7

}

class Rating(
    val client: Client,
    val local: Local,
    var score: Double
    //agregar string, descripcion
) : Entity() {}
