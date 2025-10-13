package ar.edu.unsam.algo3.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

interface PedidoObserver {
    fun notificar(pedido: Pedido)
}

class EnviaMailPlatos(internal val mailSender: MailSender, private val platoRepository: PlatoRepository): PedidoObserver {
    override fun notificar(pedido: Pedido) {
        val repo = platoRepository
        val platosDelLocal = repo.search("").filter { it.local == pedido.local }
        println(platosDelLocal)
        val platosRecomendados = platosDelLocal
            .filter { pedido.cliente.esPlatoAdecuadoParaCliente(it) }
            .shuffled()
        println(platosRecomendados)
        if (platosRecomendados.isNotEmpty()) {
            val contenidoMensaje = platosRecomendados.joinToString("\n") { "- ${it.nombre}" }
            val asunto = "Recomendaciones en ${pedido.local.name}"
            val cuerpo = "Platos para ti:\n$contenidoMensaje"

            val mailEnviado = mailSender.enviarCorreo(
                pedido.cliente.mail,
                asunto,
                cuerpo
            )
            println(mailEnviado)

        }
    }
}

object AuditorDeLocales: PedidoObserver {
    var mapaLocales: Map<Local, LocalAuditor> = mapOf()
    private val localesCumplidores = mutableSetOf<Local>()

    override fun notificar(pedido: Pedido) {
        val auditor = mapaLocales[pedido.local]
        auditor?.auditar(pedido)
    }

    fun cumpleObjetivo(local: Local): Boolean {
        return mapaLocales[local]?.cumpleObjetivo(local) ?: false
    }
}

class PedidoVegano: PedidoObserver {
    override fun notificar(pedido: Pedido) {
        if (pedido.esVegano()) {
            val cliente = pedido.cliente
            val tipoActual = cliente.tipoDeCliente

            val nuevosTipos = mutableListOf<TipoDeCliente>()
            nuevosTipos.add(ClienteVegano())
            nuevosTipos.add(tipoActual)

            cliente.tipoDeCliente = ClienteAllTipos(nuevosTipos)
        }
    }
}

class PedidoCertificado(): PedidoObserver {
    override fun notificar(pedido: Pedido) {
        if (pedido.esPedidoCertificado()) {
            val mensaje = Mensaje(
                fechaEmision = LocalDateTime.now(),
                asunto = "¡Pedido Certificado! - Cliente Premium",
                contenido = """
                    Se ha recibido un pedido certificado que requiere atención prioritaria.
                    Cliente: ${pedido.cliente.nombre}
                    Antigüedad: ${ChronoUnit.YEARS.between(pedido.cliente.fechaRegistro, LocalDate.now())} años
                    Cantidad de platos: ${pedido.plato.size}
                """.trimIndent()
            )

            pedido.local.inbox.agregarMensaje(mensaje)
        }
    }
}

interface LocalAuditor {
    fun auditar(pedido: Pedido)
    fun cumpleObjetivo(local: Local): Boolean
}

class LlegaMonto(var montoMinimo: Double, var montoAcumulado: Double = 0.0): LocalAuditor {
    override fun auditar(pedido: Pedido) {
        montoAcumulado += pedido.totalDelPedido()
    }

    override fun cumpleObjetivo(local: Local): Boolean {
        return montoAcumulado >= montoMinimo
    }
}

// Este auditor cumple cuando un local tiene 5 o mas pedidos con 3 o mas platos
class LlegaVentas(var ventasAcumuladas: Int = 0, var ventasObjetivo: Int = 5): LocalAuditor {
    override fun auditar(pedido: Pedido) {
        if (pedido.plato.size >= 3) {
            ventasAcumuladas += 1
        }
    }

    override fun cumpleObjetivo(local: Local): Boolean {
        return ventasAcumuladas >= ventasObjetivo
    }
}

// Este auditor cumple cuando un local vende mas de metaVeganos platos veganos
class LlegaMeta(val metaVeganos: Int, var acumulado: Int = 0): LocalAuditor {
    override fun auditar(pedido: Pedido) {
        pedido.plato.forEach { plato ->
            if (plato.esVegano()) {
                acumulado += 1
            }
        }
    }

    override fun cumpleObjetivo(local: Local): Boolean {
        return acumulado >= metaVeganos
    }
}

class Combinado(val auditores: List<LocalAuditor>): LocalAuditor {
    override fun auditar(pedido: Pedido) {
        auditores.forEach { it.auditar(pedido) }
    }

    override fun cumpleObjetivo(local: Local): Boolean {
        return auditores.all { it.cumpleObjetivo(local) }
    }
}