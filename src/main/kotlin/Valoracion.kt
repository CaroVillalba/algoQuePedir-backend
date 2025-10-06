package ar.edu.unsam.algo2
import ar.edu.unsam.algo2.Cliente
import ar.edu.unsam.algo2.Local
import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

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

