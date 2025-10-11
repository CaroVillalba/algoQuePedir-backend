package ar.edu.unsam.algo2

import ar.edu.unsam.algo3.domain.AuditorDeLocales
import ar.edu.unsam.algo3.domain.Cliente
import ar.edu.unsam.algo3.domain.ClienteAllTipos
import ar.edu.unsam.algo3.domain.ClienteNormal
import ar.edu.unsam.algo3.domain.ClienteVegano
import ar.edu.unsam.algo3.domain.Combinado
import ar.edu.unsam.algo3.domain.Direccion
import ar.edu.unsam.algo3.domain.GrupoAlimentos
import ar.edu.unsam.algo3.domain.Inbox
import ar.edu.unsam.algo3.domain.Ingrediente
import ar.edu.unsam.algo3.domain.LlegaMeta
import ar.edu.unsam.algo3.domain.LlegaMonto
import ar.edu.unsam.algo3.domain.LlegaVentas
import ar.edu.unsam.algo3.domain.Local
import ar.edu.unsam.algo3.domain.MailSender
import ar.edu.unsam.algo3.domain.MailsenderGlobal
import ar.edu.unsam.algo3.domain.MedioDePago
import ar.edu.unsam.algo3.domain.Pedido
import ar.edu.unsam.algo3.domain.Plato
import ar.edu.unsam.algo3.domain.PlatoRepository
import ar.edu.unsam.algo3.domain.RepositoryGlobal
import ar.edu.unsam.algo3.domain.Valoracion
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import java.time.LocalDate
import org.uqbar.geodds.Point

class ObserverSpec : DescribeSpec({
    describe("Tests de Observers") {
        lateinit var local: Local
        //lateinit var mailSender: AuditoriaMailSender
        //lateinit var platoRepository: PlatoRepository

        beforeEach {
            local = Local(
                nombre = "Test Local",
                direccion = Direccion("Calle Test", "123", Point(0, 0)),
                metodosDePago = mutableSetOf(MedioDePago.EFECTIVO),
                valoraciones = mutableSetOf(),
                inbox = Inbox()
            )
            MailsenderGlobal
            //platoRepository = PlatoRepository()
            AuditorDeLocales.mapaLocales = emptyMap()

            // *** AGREGAR ESTAS LÍNEAS PARA LIMPIAR EL REPOSITORIO GLOBAL ***
            RepositoryGlobal.elements.clear()
            RepositoryGlobal.idDisponible = 1 // Reiniciar el contador de IDs si es necesario para los tests
            // *** FIN DE AGREGADO ***

            // *** AGREGAR ESTA LÍNEA PARA LIMPIAR EL LOG DEL MAILSENDER ***
            MailsenderGlobal.log.clear()
            // *** FIN DE AGREGADO ***
        }

        describe("Tests de Auditores") {
            it("Auditor de monto debe marcar como cumplido cuando supera el monto mínimo") {
                val auditorMonto = LlegaMonto(1000.0)
                AuditorDeLocales.mapaLocales = mapOf(local to auditorMonto)

                val pedido = crearPedidoConMonto(1500.0, local, MailsenderGlobal, RepositoryGlobal)
                pedido.observers.forEach { it.notificar(pedido) }

                AuditorDeLocales.cumpleObjetivo(local) shouldBe true
            }

            it("Auditor de ventas debe marcar como cumplido cuando hay 5 pedidos con más de 3 platos") {
                val auditorVentas = LlegaVentas()
                AuditorDeLocales.mapaLocales = mapOf(local to auditorVentas)

                repeat(5) {
                    val pedido = crearPedidoConPlatos(4, local, MailsenderGlobal, RepositoryGlobal)
                    pedido.observers.forEach { it.notificar(pedido) }
                }

                AuditorDeLocales.cumpleObjetivo(local) shouldBe true
            }

            it("Auditor de meta vegana debe marcar como cumplido cuando alcanza la meta") {
                val auditorMeta = LlegaMeta(3)
                AuditorDeLocales.mapaLocales = mapOf(local to auditorMeta)

                repeat(3) {
                    val pedidoVegano = crearPedidoVegano(local, crearCliente(), MailsenderGlobal, RepositoryGlobal)
                    pedidoVegano.observers.forEach { it.notificar(pedidoVegano) }
                }

                AuditorDeLocales.cumpleObjetivo(local) shouldBe true
            }

            it("Auditor combinado debe marcar como cumplido cuando se cumplen todos los objetivos") {
                val auditorMonto = LlegaMonto(1000.0)
                val auditorVentas = LlegaVentas()
                val auditorMeta = LlegaMeta(3)
                val auditorCombinado = Combinado(listOf(auditorMonto, auditorVentas, auditorMeta))
                AuditorDeLocales.mapaLocales = mapOf(local to auditorCombinado)

                val pedidoMontoAlto = crearPedidoConMonto(1200.0, local, MailsenderGlobal, RepositoryGlobal)
                pedidoMontoAlto.observers.forEach { it.notificar(pedidoMontoAlto) }

                repeat(5) {
                    val pedidoConPlatos = crearPedidoConPlatos(4, local, MailsenderGlobal, RepositoryGlobal)
                    pedidoConPlatos.observers.forEach { it.notificar(pedidoConPlatos) }
                }

                repeat(3) {
                    val pedidoVegano = crearPedidoVegano(local, crearCliente(), MailsenderGlobal, RepositoryGlobal)
                    pedidoVegano.observers.forEach { it.notificar(pedidoVegano) }
                }

                auditorCombinado.cumpleObjetivo(local) shouldBe true
            }

            it("Auditor combinado debe marcar como no cumplido cuando falta algún objetivo") {
                val auditorMonto = LlegaMonto(1000.0)
                val auditorVentas = LlegaVentas()
                val auditorMeta = LlegaMeta(100)
                val auditorCombinado = Combinado(listOf(auditorMonto, auditorVentas, auditorMeta))
                AuditorDeLocales.mapaLocales = mapOf(local to auditorCombinado)

                val pedidoMontoAlto = crearPedidoConMonto(1200.0, local, MailsenderGlobal, RepositoryGlobal)
                pedidoMontoAlto.observers.forEach { it.notificar(pedidoMontoAlto) }

                repeat(5) {
                    val pedidoConPlatos = crearPedidoConPlatos(4, local, MailsenderGlobal, RepositoryGlobal)
                    pedidoConPlatos.observers.forEach { it.notificar(pedidoConPlatos) }
                }

                val pedidoVegano = crearPedidoVegano(local, crearCliente(), MailsenderGlobal, RepositoryGlobal)
                pedidoVegano.observers.forEach { it.notificar(pedidoVegano) }

                auditorCombinado.cumpleObjetivo(local) shouldBe false
            }
        }

        describe("Tests de Pedido") {
            it("Debe actualizar preferencias cuando el pedido es 100% vegano") {
                val cliente = crearCliente()
                val pedidoVegano = crearPedidoVegano(local, cliente, MailsenderGlobal, RepositoryGlobal)
                pedidoVegano.observers.forEach { it.notificar(pedidoVegano) }

                (cliente.tipoDeCliente is ClienteAllTipos) shouldBe true
                (cliente.tipoDeCliente as ClienteAllTipos).tipos.any { it is ClienteVegano } shouldBe true
            }

            it("Debe enviar mensaje al inbox del local cuando el pedido es certificado") {
                val cliente = crearClienteAntiguo()
                val pedidoCertificado = crearPedidoCertificado(local, cliente, MailsenderGlobal, RepositoryGlobal)
                pedidoCertificado.observers.forEach { it.notificar(pedidoCertificado) }

                local.inbox.obtenerMensajes().size shouldBe 1
                local.inbox.obtenerMensajes().first().asunto shouldBe "¡Pedido Certificado! - Cliente Premium"
            }

            it("No debe enviar mensaje al inbox si el pedido no es certificado") {
                val cliente = crearCliente()
                val pedidoNoCertificado = crearPedidoConMonto(100.0, local, MailsenderGlobal, RepositoryGlobal)
                pedidoNoCertificado.observers.forEach { it.notificar(pedidoNoCertificado) }

                local.inbox.obtenerMensajes().size shouldBe 0
            }

            it("Debe enviar por mail publicidad de platos acordes a las preferencias del cliente") {
                val cliente = crearCliente()
                var ingredienteFeo = Ingrediente("Carne", 10.0, GrupoAlimentos.PROTEINAS)
                cliente.ingredientesAEvitar.add(ingredienteFeo)

                val platoApto = Plato(
                    nombre = "Plato Apto",
                    descripcion = "Plato para test",
                    valorBase = 100.0,
                    ingredientes = listOf(),
                    local = local,
                    lanzamiento = LocalDate.now(),
                    regaliasAutor = 0.0,
                    descuento = 0.0,
                    porcentajeAcordadoConLocal = 0.0
                )
                val platoNoApto = Plato(
                    nombre = "Plato No Apto",
                    descripcion = "Tiene ingrediente a evitar",
                    valorBase = 100.0,
                    ingredientes = listOf(ingredienteFeo),
                    local = local,
                    lanzamiento = LocalDate.now(),
                    regaliasAutor = 0.0,
                    descuento = 0.0,
                    porcentajeAcordadoConLocal = 0.0
                )

                // *** CAMBIAR ESTAS LÍNEAS PARA USAR EL REPOSITORIO GLOBAL ***
                RepositoryGlobal.create(platoApto)
                RepositoryGlobal.create(platoNoApto)
                // *** FIN DE CAMBIO ***

                val pedido = Pedido.crearPedido(
                    cliente = cliente,
                    plato = listOf(platoApto),
                    local = local,
                    medioDePago = MedioDePago.EFECTIVO,
                    //mailSender = mailSender,
                    //platoRepository = platoRepository,
                )

                pedido.observers.forEach { it.notificar(pedido) }

                val mailEnviado = MailsenderGlobal.log.first()
                print(mailEnviado)
                mailEnviado shouldContain "Recomendaciones en ${local.nombre}"
                mailEnviado shouldContain platoApto.nombre
                mailEnviado shouldNotContain platoNoApto.nombre
            }
        }
    }
})

// Funciones auxiliares para crear objetos de prueba
private fun crearPedidoConMonto(monto: Double, local: Local, mailSender: MailSender, platoRepository: PlatoRepository): Pedido {
    val plato = Plato(
        nombre = "Test",
        descripcion = "Test",
        valorBase = monto,
        ingredientes = listOf(),
        local = local,
        lanzamiento = LocalDate.now(),
        regaliasAutor = 0.0,
        descuento = 0.0,
        porcentajeAcordadoConLocal = 0.0
    )

    return Pedido(
        cliente = crearCliente(),
        plato = listOf(plato),
        local = local,
        delivery = null,
        medioDePago = MedioDePago.EFECTIVO
    )
}

private fun crearPedidoConPlatos(cantidadPlatos: Int, local: Local, mailSender: MailSender, platoRepository: PlatoRepository): Pedido {
    val platos = (1..cantidadPlatos).map {
        Plato(
            nombre = "Plato $it",
            descripcion = "Test",
            valorBase = 100.0,
            ingredientes = listOf(),
            local = local,
            lanzamiento = LocalDate.now(),
            regaliasAutor = 0.0,
            descuento = 0.0,
            porcentajeAcordadoConLocal = 0.0
        )
    }

    return Pedido(
        cliente = crearCliente(),
        plato = platos,
        local = local,
        delivery = null,
        medioDePago = MedioDePago.EFECTIVO
    )
}

private fun crearCliente(): Cliente {
    return Cliente(
        ingredientesFavoritos = mutableSetOf(),
        ingredientesAEvitar = mutableSetOf(),
        distanciaCercana = 10.0,
        tipoDeCliente = ClienteNormal(),
        nombre = "Test",
        apellido = "User",
        username = "test",
        password = "test",
        fechaNacimiento = LocalDate.now().minusYears(20),
        fechaRegistro = LocalDate.now(),
        mail = "test@test.com",
        direccion = Direccion("Test", "123", Point(0, 0))
    )
}

private fun crearPedidoVegano(local: Local, cliente: Cliente = crearCliente(), mailSender: MailSender, platoRepository: PlatoRepository): Pedido {
    val ingredienteVegano = Ingrediente(
        nombre = "Lechuga",
        costo = 50.0,
        grupoAlimentos = GrupoAlimentos.FRUTAS_Y_VERDURAS
    )

    val platoVegano = Plato(
        nombre = "Ensalada Vegana",
        descripcion = "Plato 100% vegano",
        valorBase = 200.0,
        ingredientes = listOf(ingredienteVegano),
        local = local,
        lanzamiento = LocalDate.now(),
        regaliasAutor = 0.0,
        descuento = 0.0,
        porcentajeAcordadoConLocal = 0.0
    )

    return Pedido(
        cliente = cliente,
        plato = listOf(platoVegano),
        local = local,
        delivery = null,
        medioDePago = MedioDePago.EFECTIVO
    )
}

private fun crearClienteAntiguo(): Cliente {
    return Cliente(
        ingredientesFavoritos = mutableSetOf(),
        ingredientesAEvitar = mutableSetOf(),
        distanciaCercana = 10.0,
        tipoDeCliente = ClienteNormal(),
        nombre = "Cliente Antiguo",
        apellido = "Test",
        username = "antiguo",
        password = "test",
        fechaNacimiento = LocalDate.now().minusYears(30),
        fechaRegistro = LocalDate.now().minusYears(2),
        mail = "antiguo@test.com",
        direccion = Direccion("Test", "123", Point(0, 0))
    )
}

private fun crearPedidoCertificado(local: Local, cliente: Cliente, mailSender: MailSender, platoRepository: PlatoRepository): Pedido {
    val valoracionBuena = Valoracion(cliente, local, 4.5)
    local.valoraciones.add(valoracionBuena)

    return Pedido(
        cliente = cliente,
        plato = listOf(
            Plato(
                nombre = "Plato Premium",
                descripcion = "Plato especial certificado",
                valorBase = 500.0,
                ingredientes = listOf(),
                local = local,
                lanzamiento = LocalDate.now(),
                regaliasAutor = 0.0,
                descuento = 0.0,
                porcentajeAcordadoConLocal = 0.0
            )
        ),
        local = local,
        delivery = null,
        medioDePago = MedioDePago.EFECTIVO
    )
}