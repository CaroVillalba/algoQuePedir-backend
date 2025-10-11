package ar.edu.unsam.algo3.domain

interface ClienteCommand {
    fun execute(cliente: Cliente)
}

interface CalculoPuntajeStrategy {
    fun calcularPuntaje(local: Local): Double
}

class PuntuarLocalesClienteCommand(var puntajeStrategy: CalculoPuntajeStrategy) : ClienteCommand {
    override fun execute(cliente: Cliente) {
        var valoracionesValidas = cliente.valoracionesPendientes.filter{it.puedePuntuar()}.toList()
        valoracionesValidas.forEach{ it -> cliente.puntuar(it.local, puntajeStrategy.calcularPuntaje(it.local))}

    }
}

class PuntuarFijoClienteCommandStrat(var puntaje: Double) : CalculoPuntajeStrategy {
    override fun calcularPuntaje(local: Local): Double {
        return puntaje
    }
}

class PuntuarPromedioClienteCommandStrat() : CalculoPuntajeStrategy {
    override fun calcularPuntaje(local: Local): Double {
        return local.getPuntuacion()
    }
}

class PuntuarRandomClienteCommandStrat() : CalculoPuntajeStrategy {
    override fun calcularPuntaje(local: Local): Double {
        return (0..5).random().toDouble()
    }
}

class CrearPedidoClienteCommand(
    var plato: List<Plato>,
    var local: Local,
    var medioDePago: MedioDePago,
    //var mailSender: MailSender,
    //var platoRepository: PlatoRepository
) : ClienteCommand {
    override fun execute(cliente: Cliente) {
        cliente.crearPedido(
            plato,
            local,
            medioDePago
        )
    }
}