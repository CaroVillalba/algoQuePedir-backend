package ar.edu.unsam.algo3.dto

import ar.edu.unsam.algo3.domain.Rating

data class LocalDTO (
    val id: Int?,
    val name: String,
    val address: String,
    val imgURL: String,
    var rating: Double,
    val reviews: Int,
    val orders: Int
)