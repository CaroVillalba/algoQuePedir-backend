package ar.edu.unsam.algo2

import java.time.LocalDateTime

class Mensaje(
    val fechaEmision: LocalDateTime,
    val asunto: String,
    val contenido: String,
    private var leido: Boolean = false
) {
    fun marcarComoLeido() {
        leido = true
    }

    fun marcarComoNoLeido() {
        leido = false
    }

    fun estaLeido() = leido
}

class Inbox {
    val mensajes = mutableListOf<Mensaje>()

    fun agregarMensaje(mensaje: Mensaje) {
        mensajes.add(mensaje)
    }

    fun obtenerMensajes(): List<Mensaje> = mensajes.toList()

    fun obtenerMensajesNoLeidos(): List<Mensaje> =
        mensajes.filter { !it.estaLeido() }

    fun obtenerMensajesLeidos(): List<Mensaje> =
        mensajes.filter { it.estaLeido() }
}


