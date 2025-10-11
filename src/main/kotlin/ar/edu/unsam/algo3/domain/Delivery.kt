package ar.edu.unsam.algo3.domain
import ar.edu.unsam.algo3.exceptions.PedidoInvalidoException
import ar.edu.unsam.algo3.exceptions.PedidoStateException
import org.uqbar.geodds.Polygon //Se agrega dependencia

import java.time.LocalDate

class Delivery (
    nombre: String,
    apellido: String,
    username: String,
    password: String,
    fechaNacimiento: LocalDate,
    var direccion: Direccion, //objeto Direccion del aporte de Ulises
    var zonaDeTrabajo: Polygon, // Se cambia por tipo de objeto poligono
    val condicionesEntrega: CondicionEntrega,
) : Usuario(nombre, apellido, username, password, fechaNacimiento) {

    fun tomarPedido(pedido: Pedido) : Boolean { //por qué devuelve un boolean?
        if (pedido.estado != EstadoPedido.PREPARADO) {
            println("El pedido no esta preparado")
            throw PedidoStateException("El pedido no está preparado.")
        }

        val retiroDentro = zonaDeTrabajo.isInside(pedido.local.direccion.coordenadas)  //Se cambia por el metodo de polygon
        val entregaDentro = zonaDeTrabajo.isInside(pedido.cliente.direccion.coordenadas) // (https://github.com/uqbar-project/geodds-xtend/blob/master/src/main/java/org/uqbar/geodds/Polygon.xtend)

        if (!retiroDentro || !entregaDentro) {
            throw PedidoInvalidoException("El pedido no está dentro la zona de trabajo.")
        }

        if (!cumpleConCondiciones(pedido)) {
            throw PedidoInvalidoException("El pedido no cumple con las condiciones de entrega.")
        }

        return true
    }

    fun cumpleConCondiciones(pedido: Pedido): Boolean {
        return condicionesEntrega.cumpleCondicion(pedido, this)

    }

    fun entregarPedido(pedido: Pedido) {
        pedido.marcarComoEntregado()
    }

}