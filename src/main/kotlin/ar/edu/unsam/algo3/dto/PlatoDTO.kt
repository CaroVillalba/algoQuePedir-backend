package ar.edu.unsam.algo3.dto

data class PlatoDTO(
    val imageURL: String,
    val nombre: String,
    val descripcion: String,
    val ingredientes: List<IngredientDTO>,
    val porcentajeAcordadoConLocal: Double,
    val regaliasAutor: Double,
    val valorBase: Double,
    val descuento: Double,
)



