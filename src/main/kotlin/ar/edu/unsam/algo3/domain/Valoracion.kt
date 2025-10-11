package ar.edu.unsam.algo3.domain
import java.time.LocalDate
import java.time.Period

class ValoracionPendiente(
    val local : Local,
    var fechaUltimoPedido: LocalDate = LocalDate.now()
){

    fun puedePuntuar(): Boolean = Period.between(fechaUltimoPedido, LocalDate.now()).days < 7

}

class Valoracion(
    val cliente: Cliente,
    val local: Local,
    var puntuacion: Double
): Entity() {}

