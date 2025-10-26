package ar.edu.unsam.algo3.dto

import org.uqbar.geodds.Point

data class DireccionOrderDTO (
    val street: String,
    val number: String,
    val coordinates: Point
)