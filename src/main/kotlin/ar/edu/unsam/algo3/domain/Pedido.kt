package ar.edu.unsam.algo3.domain

import ar.edu.unsam.algo3.exceptions.AsignacionPedidoException
import ar.edu.unsam.algo3.exceptions.MedioDePagoInvalidoException
import ar.edu.unsam.algo3.exceptions.PedidoStateException
import ar.edu.unsam.algo3.exceptions.PlatoInvalidoException
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.math.RoundingMode

enum class EstadoPedido {
    PENDIENTE,
    PREPARADO,
    ENTREGADO,
    CANCELADO
}
object MailsenderGlobal: AuditoriaMailSender()
object RepositoryGlobal: PlatoRepository()
class Pedido(
    val cliente: Cliente,
    val plato: List<Plato>,
    var estado: EstadoPedido = EstadoPedido.PENDIENTE,
    var local: Local,
    var delivery: Delivery?, /*ATENCION!!: CAMBIO QUE HICE, ponerle null. Sino, no termino más de probar esto. */
    var medioDePago: MedioDePago,
    var horaRetiro: LocalTime? = null,
    var cuponAplicado: Cupon = CuponBase()
) : Entity() {

    var observers: MutableList<PedidoObserver> = mutableListOf(
        AuditorDeLocales,
        PedidoVegano(),
        PedidoCertificado()
    )


    companion object {
        fun crearPedido(
            cliente: Cliente,
            plato: List<Plato>,
            local: Local,
            medioDePago: MedioDePago,

            ): Pedido {
            val platoRepository = RepositoryGlobal
            val mailSender = MailsenderGlobal
            var pedido = Pedido(cliente, plato, EstadoPedido.PENDIENTE, local, null, medioDePago)
            pedido.observers.add(EnviaMailPlatos(mailSender, platoRepository))
            pedido.validarPlatos()
            pedido.validarMedioDePago()
            return pedido
        }
    }

    private fun validarPlatos() {
        plato.forEach {
            if (it.local != local) {
                throw PlatoInvalidoException("El plato ${it.nombre} no pertenece al local.")
            }

            if (!cliente.esPlatoAdecuadoParaCliente(it)) {
                throw PlatoInvalidoException("El plato ${it.nombre} contiene ingrediente(s) que el cliente evita.")
            }
        }
    }

    private fun validarMedioDePago() {
        if (!local.paymentMethods.contains(medioDePago)) {
            throw MedioDePagoInvalidoException("El medio de pago $medioDePago no es válido para este local.")
        }
    }

    fun esVegano(): Boolean{
        return plato.all { it.esVegano() }
    }

    fun asignarDelivery(delivery: Delivery) {
        if(!delivery.tomarPedido(this)) {
            throw AsignacionPedidoException("No se pudo asignar el pedido al delivery.")
        }
        this.delivery = delivery

    }

    fun esPedidoCertificado(): Boolean {
        val hoy = LocalDate.now()
        val antiguedadAnosChronoUnit =
            ChronoUnit.YEARS.between(cliente.fechaRegistro, hoy) //usé ChonoUnit para más control sobre el año
        val puntuacionLocal = local.getPuntuacion()
        return antiguedadAnosChronoUnit >= 1 && puntuacionLocal in 4.0..5.0
    }

    fun marcarComoPreparado() {
        if (estado != EstadoPedido.PREPARADO) {
            estado = EstadoPedido.PREPARADO
        }
        else{
            throw PedidoStateException("El estado ya estaba preparado")
        }
    }

    fun marcarComoEntregado() {
        validarPlatos()
        validarMedioDePago()
        validarEstadoEntregable()
        cliente.confirmarPedido(this)
    }

    fun validarEstadoEntregable() {
        if (estado != EstadoPedido.ENTREGADO && estado == EstadoPedido.PREPARADO) {
            estado = EstadoPedido.ENTREGADO
        }
        else{
            throw PedidoStateException("No se puede entregar un pedido no preparado o el pedido ya estaba entregado")
        }
    }

    fun totalDelPedido(): Double {
        val costoPlatos = plato.sumOf { it.valorDeVenta() }
        val costoPlatosSinDescuentos = plato.sumOf { it.valorDeVentaSinDescuento() }
        val costoDelivery = costoPlatosSinDescuentos * 0.1
        val totalBase = costoPlatos + costoDelivery
        return (totalBase * medioDePago.impuesto()).toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toDouble()
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*fun totalDelPedido(): Double {
        *//*val totalBase = totalBase()*//*
        val totalConImpuesto = aplicarImpuesto()
        return redondear(totalConImpuesto)
    }*/

    fun costoPlatos(): Double =
        plato.sumOf { it.valorDeVenta() }

    fun costoPlatosSinDescuentos(): Double =
        plato.sumOf { it.valorDeVentaSinDescuento() }

    fun costoDelivery(): Double =
        costoPlatosSinDescuentos() * 0.1

    fun totalBase(): Double =
        costoPlatos() + costoDelivery()

    fun aplicarImpuesto(): Double =
        this.totalBase() * medioDePago.impuesto()

    fun redondear(valor: Double): Double =
        valor.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toDouble()

    fun totalPlatos(): Int = plato.count()

    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun totalDelPedidoConCupon(cupon: Cupon): Double {
        return (totalDelPedido() - cupon.calcularDescuentoTotal(this)).toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toDouble()
    }

    fun aplicarDescuento(cupon: Cupon) {
        cupon.aplicar(this) // detonante de si puede ser o no
        this.cuponAplicado = cupon
    }

    fun tieneCuponAplicado(cupon: Cupon): Boolean = totalDelPedidoConCupon(cupon) != totalDelPedido()
    fun notificarObservers() = observers.forEach{it.notificar(this)}
}