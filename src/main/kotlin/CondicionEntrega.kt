package ar.edu.unsam.algo2
import java.time.LocalTime

interface CondicionEntrega {
    fun cumpleCondicion(pedido: Pedido, delivery: Delivery): Boolean
}

class CondicionRangoHorario(private val horaInicio: LocalTime, private val horaFin: LocalTime) : CondicionEntrega {
    override fun cumpleCondicion(pedido: Pedido, delivery: Delivery): Boolean {
        val horaRetiro = pedido.horaRetiro
        return horaRetiro != null && horaRetiro.isAfter(horaInicio) && horaRetiro.isBefore(horaFin)
    }
}

class CondicionMontoMinimo(private val montoMinimo: Double) : CondicionEntrega {
    override fun cumpleCondicion(pedido: Pedido, delivery: Delivery): Boolean {
        return pedido.totalDelPedido() >= montoMinimo
    }
}

class CondicionLocalEspecifico(private val listaLocales: MutableList<Local>): CondicionEntrega {  //Se cambia la condicion para que no sea parametro la lista de locales
    override fun cumpleCondicion(pedido: Pedido, delivery: Delivery): Boolean {
        return listaLocales.contains(pedido.local)
    }
}

class CondicionPedidoCertificado() : CondicionEntrega {
    override fun cumpleCondicion(pedido: Pedido, delivery: Delivery): Boolean {
        return pedido.esPedidoCertificado()
    }
}

//OPCION 1:

//Una condicion con una lista de condiciones, le aplica un or ||
class CondicionAny(val listaCondiciones: List<CondicionEntrega>) : CondicionEntrega {
    override fun cumpleCondicion(pedido: Pedido, delivery: Delivery): Boolean {
        return listaCondiciones.any { it.cumpleCondicion(pedido, delivery) }
    }
}

//Una condicion con una lista de condiciones, le aplica un or ||
class CondicionAll(val listaCondiciones: List<CondicionEntrega>) : CondicionEntrega {
    override fun cumpleCondicion(pedido: Pedido, delivery: Delivery): Boolean {
        return listaCondiciones.all { it.cumpleCondicion(pedido, delivery) }
    }
}

/*
OPCION 2:

//Una condicion con una lista de condiciones, le aplica un and &&
class CondicionEntregaMixto(val listaCondiciones: List<CondicionEntrega>, val tipoBusqueda: TipoDeCondicion) : CondicionEntrega {
    override fun cumpleCondicion(pedido: Pedido, delivery: Delivery): Boolean {
        if (tipoBusqueda == TipoDeCondicion.ALL) {
            return listaCondiciones.all { it.cumpleCondicion(pedido, delivery) }
        } else {
            return listaCondiciones.any { it.cumpleCondicion(pedido, delivery) }
        }
    }
}*/