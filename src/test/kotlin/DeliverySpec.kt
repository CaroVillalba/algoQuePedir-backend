import org.uqbar.geodds.*
import ar.edu.unsam.algo3.domain.AuditoriaMailSender
import ar.edu.unsam.algo3.domain.Cliente
import ar.edu.unsam.algo3.domain.ClienteNormal
import ar.edu.unsam.algo3.domain.CondicionAny
import ar.edu.unsam.algo3.domain.CondicionEntrega
import ar.edu.unsam.algo3.domain.CondicionLocalEspecifico
import ar.edu.unsam.algo3.domain.CondicionMontoMinimo
import ar.edu.unsam.algo3.domain.CondicionPedidoCertificado
import ar.edu.unsam.algo3.domain.CondicionRangoHorario
import ar.edu.unsam.algo3.domain.CuponBase
import ar.edu.unsam.algo3.domain.Delivery
import ar.edu.unsam.algo3.domain.Address
import ar.edu.unsam.algo3.domain.EstadoPedido
import ar.edu.unsam.algo3.domain.GrupoAlimentos
import ar.edu.unsam.algo3.domain.Ingredient
import ar.edu.unsam.algo3.domain.Local
import ar.edu.unsam.algo3.domain.MedioDePago
import ar.edu.unsam.algo3.domain.Pedido
import ar.edu.unsam.algo3.exceptions.PedidoInvalidoException
import ar.edu.unsam.algo3.exceptions.PedidoStateException
import ar.edu.unsam.algo3.domain.Dish
import ar.edu.unsam.algo3.domain.PlatoRepository
import ar.edu.unsam.algo3.domain.Valoracion
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.assertions.throwables.shouldThrow
import java.time.LocalDate
import java.time.LocalTime

class DeliverySpec : DescribeSpec({
    lateinit var platoRepository: PlatoRepository
    lateinit var delivery: Delivery
    lateinit var pedido: Pedido
    lateinit var cliente: Cliente
    lateinit var ingredient1: Ingredient
    lateinit var ingredient2: Ingredient
    lateinit var ingredient3: Ingredient
    lateinit var ingredient4: Ingredient
    lateinit var ingredient5: Ingredient
    lateinit var local: Local
    lateinit var local2: Local
    lateinit var dish1: Dish
    lateinit var dish2: Dish
    lateinit var condicionesList: MutableList<CondicionEntrega>

    beforeTest {
        // Configuración inicial
        platoRepository = PlatoRepository()
        var mailSender = AuditoriaMailSender()
        var cupon = CuponBase()
        cliente = Cliente(
            nombre = "Juan",
            apellido = "Pérez",
            username = "juanperez",
            password = "1234",
            fechaNacimiento = LocalDate.of(1990, 1, 1),
            fechaRegistro = LocalDate.of(2020, 1, 1),
            "cliente@mail.com",
            direccion = Address(
                calle = "Calle Falsa",
                altura = "123",
                coordenadas = Point(2, 2)
            ),
            ingredientesFavoritos = mutableSetOf(),
            ingredientesAEvitar = mutableSetOf(),
            distanciaCercana = 10.0,
            tipoDeCliente = ClienteNormal(),
        )

        local = Local(
            name = "Comida Rápida",
            address = Address(
                calle = "Calle",
                altura = "456",
                coordenadas = Point(-1, -1)
            ),
            paymentMethods = mutableSetOf(MedioDePago.EFECTIVO, MedioDePago.CREDITO, MedioDePago.QR),
            ratings = mutableSetOf()
        )

        local.ratings = mutableSetOf(
            Valoracion(
                cliente = cliente,
                local = local,
                puntuacion = 4.5
            ),
            Valoracion(
                cliente = cliente,
                local = local,
                puntuacion = 5.0
            ),
            Valoracion(
                cliente = cliente,
                local = local,
                puntuacion = 4.8
            )
        )

        local2 = Local(
            name = "Comida Rápida2",
            address = Address(
                calle = "Calle2",
                altura = "4562",
                coordenadas = Point(-1, -1)
            ),
            paymentMethods = mutableSetOf(MedioDePago.EFECTIVO, MedioDePago.CREDITO, MedioDePago.QR),
            ratings = mutableSetOf()
        )

        local2.ratings = mutableSetOf(
            Valoracion(
                cliente = cliente,
                local = local,
                puntuacion = 1.0
            ),
            Valoracion(
                cliente = cliente,
                local = local,
                puntuacion = 1.0
            ),
            Valoracion(
                cliente = cliente,
                local = local,
                puntuacion = 1.0
            )
        )

        ingredient1 = Ingredient("Carne", 120.0, GrupoAlimentos.PROTEINAS)
        ingredient2 = Ingredient("Tocino", 80.0, GrupoAlimentos.GRASAS_Y_ACEITES) //ejem, panceta
        ingredient3 = Ingredient("Tomate", 100.0, GrupoAlimentos.FRUTAS_Y_VERDURAS)
        ingredient4 = Ingredient("Papa", 60.0, GrupoAlimentos.CEREALES_Y_TUBERCULOS)
        ingredient5 = Ingredient("Aceite", 70.0, GrupoAlimentos.GRASAS_Y_ACEITES)

        dish1 = Dish(
            nombre = "Hamburguesa",
            descripcion = "Hamburguesa gourmet con carne de res premium, tocino crujiente y salsa especial",
            ingredients = listOf(ingredient1, ingredient2, ingredient3),
            local = local,
            porcentajeAcordadoConLocal = 20.0,
            lanzamiento = LocalDate.now().minusWeeks(2),
            regaliasAutor = 20.0,
            valorBase = 800.0,
            descuento = 0.0
        )
        dish2 = Dish(
            nombre = "Papas Fritas",
            descripcion = "Papas fritas clásicas, crujientes por fuera y suaves por dentro",
            ingredients = listOf(ingredient4, ingredient5),
            local = local,
            porcentajeAcordadoConLocal = 30.0,
            lanzamiento = LocalDate.now().minusWeeks(30),
            regaliasAutor = 0.0,
            valorBase = 300.0,
            descuento = 50.0
        )

        pedido = Pedido(
            cliente = cliente,
            plato = listOf(dish1, dish2),
            estado = EstadoPedido.PENDIENTE,
            local = local,
            delivery = null,
            medioDePago = MedioDePago.EFECTIVO,
            horaRetiro = LocalTime.of(10, 0),
            cuponAplicado = cupon
        )

        condicionesList = mutableListOf()
        val condicionesMixtas = CondicionAny(condicionesList)
        delivery = Delivery(
            nombre = "PedroDelivery",
            apellido = "Gutierrez",
            username = "pedrolivery",
            password = "pass1234",
            fechaNacimiento = LocalDate.now().minusYears(30),
            address = Address(calle = "Calle Entrega", altura = "789", coordenadas = Point(2, 2)),
            zonaDeTrabajo = Polygon(
                mutableListOf(
                    Point(-2, -2),
                    Point(2, -2),
                    Point(2, 2),
                    Point(-2, 2)
                )
            ),
            condicionesEntrega = condicionesMixtas,
        )
    }

    describe("Gestión de Delivery") {

        it("Debería agregar una condición de entrega correctamente") {
            val condicionMontoMinimo = CondicionMontoMinimo(200.0)
            condicionesList.add(condicionMontoMinimo)

            condicionesList.shouldContain(condicionMontoMinimo)
        }

        it("Debería aceptar un pedido preparado si cumple todas las condiciones") {
            condicionesList.add(CondicionMontoMinimo(100.0))

            pedido.estado = EstadoPedido.PREPARADO
            delivery.tomarPedido(pedido) shouldBe true
        }

        it("Debería lanzar una excepción si el pedido no está preparado") {
            condicionesList.add(CondicionMontoMinimo(50.0))

            pedido.estado = EstadoPedido.PENDIENTE

            shouldThrow<PedidoStateException> {
                delivery.tomarPedido(pedido)
            }
        }

        it("Debería lanzar una excepción si el pedido no cumple las condiciones") {
            condicionesList.add(CondicionMontoMinimo(99999.0)) // Pedido no alcanza el monto
            pedido.estado = EstadoPedido.PREPARADO

            shouldThrow<PedidoInvalidoException> {
                delivery.tomarPedido(pedido)
            }
        }

        it("Debería marcar un pedido como entregado correctamente") {
            condicionesList.add(CondicionMontoMinimo(100.0))
            pedido.estado = EstadoPedido.PREPARADO
            delivery.tomarPedido(pedido)

            delivery.entregarPedido(pedido)

            pedido.estado shouldBe EstadoPedido.ENTREGADO
        }
    }

    describe("Funcionalidad de Pedido") {

        it("Debería calcular el costo total correctamente") {
            val totalPedido = pedido.totalDelPedido()

            val descuentoCostoPlato1 = (1.0 - (30.0-14.0)/100.0)
            val costoPlato1: Double = (800.0 + 800.0*0.2 + 800.0*0.2 + (120.0+80.0+100.0))
            val costoPlato2: Double = (300.0 + 300.0*0.3 + (60.0 + 70.0))
            val costoPlato1ConDescuento = costoPlato1 * descuentoCostoPlato1
            val costoPlato2ConDescuento = costoPlato2 * 0.5
            val costoPlatosSinDescuento = costoPlato1 + costoPlato2
            val costoPlatos: Double = costoPlato1ConDescuento + costoPlato2ConDescuento
            val costoDelivery: Double = costoPlatosSinDescuento * 0.1
            val totalEsperado: Double = costoPlatos + costoDelivery

            totalPedido shouldBe totalEsperado
        }

        it("Debería aplicar un recargo por pago con tarjeta") {
            pedido.medioDePago = MedioDePago.CREDITO

            val totalPedido = pedido.totalDelPedido()

            val descuentoCostoPlato1 = (1.0 - (30.0-14.0)/100.0)
            val costoPlato1: Double = (800.0 + 800.0*0.2 + 800.0*0.2 + (120.0+80.0+100.0))
            val costoPlato2: Double = (300.0 + 300.0*0.3 + (60.0 + 70.0))
            val costoPlato1ConDescuento = costoPlato1 * descuentoCostoPlato1
            val costoPlato2ConDescuento = costoPlato2 * 0.5
            val costoPlatosSinDescuento = costoPlato1 + costoPlato2
            val costoPlatos: Double = costoPlato1ConDescuento + costoPlato2ConDescuento
            val costoDelivery: Double = costoPlatosSinDescuento * 0.1
            val totalEsperado: Double = (costoPlatos + costoDelivery) * 1.05

            totalPedido shouldBe totalEsperado
        }

        it("Debería permitir marcar un pedido como preparado") {
            pedido.marcarComoPreparado()

            pedido.estado shouldBe EstadoPedido.PREPARADO
        }

        it("Debería solo permitir marcar un pedido como entregado si está preparado") {
            shouldThrow<PedidoStateException> {
                pedido.marcarComoEntregado()
            }

            pedido.estado = EstadoPedido.PREPARADO
            pedido.marcarComoEntregado()

            pedido.estado shouldBe EstadoPedido.ENTREGADO
        }

        it("Debería no permitir marcar un pedido como entregado si no está preparado") {
            pedido.estado = EstadoPedido.PENDIENTE

            shouldThrow<PedidoStateException> {
                pedido.marcarComoEntregado()
            }

            pedido.estado shouldBe EstadoPedido.PENDIENTE
        }
    }

    describe("Condiciones delivery"){
        it ("Un delivery con condicion Rango horario solo acepta pedidos de dentro de su horario") {
            condicionesList.add(CondicionRangoHorario(LocalTime.of(9, 0), LocalTime.of(17, 0)))
            pedido.marcarComoPreparado()
            pedido.horaRetiro = LocalTime.of(14,0)

            delivery.tomarPedido(pedido)
        }

        it ("Un delivery con condicion Rango horario NO acepta pedidos fuera de su horario") {
            condicionesList.add(CondicionRangoHorario(LocalTime.of(9, 0), LocalTime.of(17, 0)))
            pedido.marcarComoPreparado()
            pedido.horaRetiro = LocalTime.of(23,0)

            shouldThrow<PedidoInvalidoException>{
                delivery.tomarPedido(pedido)
            }
        }

        it ("Un delivery con condicion Monto Minimo solo acepta pedidos si es dentro de un monto minimo") {
            condicionesList.add(CondicionMontoMinimo(100.0))
            pedido.marcarComoPreparado()

            delivery.tomarPedido(pedido)
        }

        it ("Un delivery con condicion Monto Minimo NO acepta pedidos si no alcanza el monto minimo") {
            condicionesList.add(CondicionMontoMinimo(1000000000.0))
            pedido.marcarComoPreparado()

            shouldThrow<PedidoInvalidoException>{
                delivery.tomarPedido(pedido)
            }
        }

        it ("Un delivery con condicion Local Especifico solo acepta pedidos que sean de un local") {
            condicionesList.add(CondicionLocalEspecifico(mutableListOf(local)))

            pedido.marcarComoPreparado()

            delivery.tomarPedido(pedido)
        }

        it ("Un delivery con condicion Local Especifico NO acepta pedidos si no son de ese local") {
            condicionesList.add(CondicionLocalEspecifico(mutableListOf(local2)))

            pedido.marcarComoPreparado()

            shouldThrow<PedidoInvalidoException>{
                delivery.tomarPedido(pedido)
            }
        }

        it ("Un delivery con condicion Pedido Certificado solo acepta pedidos certificados") {
            condicionesList.add(CondicionPedidoCertificado())

            pedido.marcarComoPreparado()

            delivery.tomarPedido(pedido)
        }

        it ("Un delivery con condicion Pedido Certificado NO acepta pedidos no certificados") {
            condicionesList.add(CondicionPedidoCertificado())

            pedido.local = local2
            pedido.marcarComoPreparado()

            shouldThrow<PedidoInvalidoException>{
                delivery.tomarPedido(pedido)
            }
        }

        it ("Condicion any: la condicion debe cumplir con alguna de las condiciones de su lista") {
            condicionesList.addAll(listOf(
                CondicionPedidoCertificado(),
                CondicionLocalEspecifico(mutableListOf(local))
            ))

            pedido.marcarComoPreparado()
            delivery.tomarPedido(pedido) //El pedido es certificado por que es de local 1
        }

        it ("Condicion any: la condicion debe cumplir con alguna de las condiciones de su lista (parte 2)") {
            condicionesList.clear()
            condicionesList.addAll(listOf(
                CondicionPedidoCertificado(),
                CondicionLocalEspecifico(mutableListOf(local2))
            ))
            pedido.local = local2
            pedido.marcarComoPreparado()
            delivery.tomarPedido(pedido)
        }
    }
})
