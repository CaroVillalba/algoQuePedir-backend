package ar.edu.unsam.algo3.dto

data class PlatoDTO(
    val imageURL: String,
    val nombre: String,
    val descripcion: String,
    val ingredientes: List<IngredientDTO>,
    val agreedPercentageWithLocal: Double,
    val local: Int, // el id del Local y hacer un get del local para conseguirlo y pasarselo
    val regaliasAutor: Double,
    val valorBase: Double,
    val descuento: Double,
)



