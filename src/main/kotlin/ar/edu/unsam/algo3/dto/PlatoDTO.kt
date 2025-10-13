package ar.edu.unsam.algo3.dto

import java.time.LocalDate

// PlatoDTO.kt (DTO de lectura)
data class PlatoDTO(
    val imageURL: String,
    val nombre: String,
    val descripcion: String,
    val ingredientes: List<IngredienteDTO>,
    val porcentajeAcordadoConLocal: Double,
    val regaliasAutor: Double,
    val valorBase: Double,
    val descuento: Double,
)



