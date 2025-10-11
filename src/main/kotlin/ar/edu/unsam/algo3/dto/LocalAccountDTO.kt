package ar.edu.unsam.algo3.dto

data class LocalAccountDTO(
    val nombre: String,
    val imgURL: String,
    val direccion: DireccionDTO,
    val comisionApp: Int,
    val comisionAutor: Int,
    val metodosDePago: List<String>
)