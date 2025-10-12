package ar.edu.unsam.algo3.dto

import org.uqbar.geodds.Point

data class DireccionOrderDTO (
    val calle: String,
    val altura: String,
    val coordenadas: Point
)