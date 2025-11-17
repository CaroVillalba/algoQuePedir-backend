package ar.edu.unsam.algo3.dto
import org.uqbar.geodds.Point

import ar.edu.unsam.algo3.domain.Rating

data class LocalDTO (
    val id: Int?,
    val name: String,
    val address: String,
    val coordinates: Point,
    val imgURL: String,
    var rating: Double,
    val reviews: Int,
    val orders: Int,
    val appCommission: Double,
    val esCercano: Boolean? = null
)