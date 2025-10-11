package ar.edu.unsam.algo3.domain

import ar.edu.unsam.algo3.exceptions.CuponNoAplicacionException
import java.time.DayOfWeek
import java.time.LocalDate

abstract class Cupon (
    val fechaEmision: LocalDate,
    var duracion: Int,
    var porcentajeBaseDescuento: Double,
    var fueAplicado: Boolean
): Entity()  {
    init {
        require(porcentajeBaseDescuento in 0.0..1.0) {
            "El porcentaje base debe estar entre 0 y 1"
        }
    }

    fun aplicar(pedido: Pedido) {
        if (validarQueAplica(pedido)) {
            //pedido.aplicarDescuento(this) // <-- pasar el cupon completo
            fueAplicado = true
        }
        else{
            throw CuponNoAplicacionException("El cupón no puede ser aplicado a este pedido")
        }
    }

    fun tieneRestriccionesGenerales(pedido: Pedido): Boolean {
        return estaExpirado() || fueAplicado || calcularDescuentoTotal(pedido) > pedido.totalDelPedido()
    }

    fun validarQueAplica(pedido: Pedido): Boolean {
        return !tieneRestriccionesGenerales(pedido) && esAplicableEspecifico(pedido)
    }

    fun calcularDescuentoTotal(pedido: Pedido): Double {
        return descuentoBase(pedido) + calcularDescuentoEspecifico(pedido)
    }

    fun descuentoBase(pedido: Pedido): Double {
        return pedido.totalDelPedido() * porcentajeBaseDescuento
    }

    fun estaExpirado(): Boolean {
        val fechaVencimiento = fechaEmision.plusDays(duracion.toLong())
        return LocalDate.now().isAfter(fechaVencimiento)
    }

     abstract fun esAplicableEspecifico(pedido: Pedido): Boolean
     abstract fun calcularDescuentoEspecifico(pedido: Pedido): Double
}

class CuponDiaEspecifico(
    fechaEmision: LocalDate,
    duracion: Int,
    porcentajeBaseDescuento: Double,
    fueAplicado: Boolean,
    var dia: DayOfWeek
) : Cupon(fechaEmision, duracion, porcentajeBaseDescuento, fueAplicado) {

    override fun calcularDescuentoEspecifico(pedido: Pedido): Double {
        val algunPlatoLanzadoEnDia = pedido.plato.any { it.lanzamiento.dayOfWeek == dia }
        val porcentajeExtra = if (algunPlatoLanzadoEnDia) 0.10 else 0.05
        return pedido.totalDelPedido() * porcentajeExtra
    }

    override fun esAplicableEspecifico(pedido: Pedido): Boolean {
        return LocalDate.now().dayOfWeek == dia
    }
}

class CuponLocalEspecifico(
    fechaEmision: LocalDate,
    duracion: Int,
    porcentajeBaseDescuento: Double,
    fueAplicado: Boolean,
    private val localesEspecificos: List<Local>
) : Cupon(fechaEmision, duracion, porcentajeBaseDescuento, fueAplicado) {

    override fun calcularDescuentoEspecifico(pedido: Pedido): Double {
        return if (pedido.esPedidoCertificado()) 1000.0 else 500.0
    }

    override fun esAplicableEspecifico(pedido: Pedido): Boolean {
        return pedido.local in localesEspecificos
    }
}

class CuponTopeMaximo(
    fechaEmision: LocalDate,
    duracion: Int,
    porcentajeBaseDescuento: Double,
    fueAplicado: Boolean,
    var topeMaximo: Double,
    private val porcentajeDescuentoExtra: Double
) : Cupon(fechaEmision, duracion, porcentajeBaseDescuento, fueAplicado) {

    init {
        require(topeMaximo > 0.0) {
            "El tope máximo debe ser mayor que 0"
        }
    }

    override fun calcularDescuentoEspecifico(pedido: Pedido): Double {
        val descuentoCalculado = pedido.totalDelPedido() * porcentajeDescuentoExtra
        return minOf(descuentoCalculado, topeMaximo)
    }

    override fun esAplicableEspecifico(pedido: Pedido): Boolean = true
}

class CuponBase: Cupon(
    fechaEmision = LocalDate.now(),
    duracion = 1,
    porcentajeBaseDescuento = 0.0,
    fueAplicado = false,
){
    override fun esAplicableEspecifico(pedido: Pedido): Boolean = true
    override fun calcularDescuentoEspecifico(pedido: Pedido): Double = 0.0
}