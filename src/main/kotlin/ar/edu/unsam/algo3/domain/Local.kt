package ar.edu.unsam.algo3.domain

class Local(
    var nombre: String,
    var direccion: Direccion,
    var metodosDePago: MutableSet<MedioDePago>,
    var valoraciones: MutableSet<Valoracion>,
    var imgURL: String = "",
    var comisionApp: Int = 0,
    var comisionAutor: Int = 0,
    val inbox: Inbox = Inbox()
) : Entity() {

    fun getEsConfiable(): Boolean = getPuntuacion() >= 4.0

    fun getPuntuacion(): Double {
        return if (valoraciones.isNotEmpty()) valoraciones.map { it.puntuacion }.average() else 0.0
    }

    fun crearValoracion(cliente: Cliente, puntuacion: Double) {
        val valoracion = Valoracion(cliente, this, puntuacion)
        valoraciones.add(valoracion)
    }
}
