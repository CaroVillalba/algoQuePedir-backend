package ar.edu.unsam.algo2

class Local(
    var nombre: String,
    var direccion: Direccion, //es un objjeto direccion con lo que pone ulises
    var metodosDePago: MutableSet<MedioDePago>, //interface compartida entre el pedido con el local
    // pago != efectivo ==> + pago * 0.05 en el valor final.
    var valoraciones: MutableSet<Valoracion>,
    val inbox: Inbox = Inbox()
): Entity() {

    fun getEsConfiable(): Boolean = getPuntuacion() >= 4.0 // recorre la lista de valoraciones, sacando un promedio

    fun getPuntuacion(): Double{
        return valoraciones.map{it.puntuacion}.average()
    }

    fun crearValoracion(cliente: Cliente, puntuacion: Double) {
        val valoracion = Valoracion(cliente, this, puntuacion)
        valoraciones.add(valoracion)
    }
}

