package ar.edu.unsam.algo3.domain

class Local(
    var name: String,
    var address: Direccion,
    var paymentMethods: MutableSet<MedioDePago>,
    var ratings: MutableSet<Valoracion>  = mutableSetOf(),
    var imgURL: String = "",
    var comisionApp: Int = 0,
    var comisionAuthor: Int = 0,
    val inbox: Inbox = Inbox()
) : Entity() {

    fun getEsConfiable(): Boolean = getPuntuacion() >= 4.0

    fun getPuntuacion(): Double {
        return if (ratings.isNotEmpty()) ratings.map { it.puntuacion }.average() else 0.0
    }

    fun crearValoracion(cliente: Cliente, puntuacion: Double) {
        val valoracion = Valoracion(cliente, this, puntuacion)
        ratings.add(valoracion)
    }
}
