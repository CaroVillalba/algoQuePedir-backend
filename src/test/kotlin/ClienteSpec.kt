package ar.edu.unsam.algo2

import ar.edu.unsam.algo3.domain.AuditoriaMailSender
import ar.edu.unsam.algo3.domain.Cliente
import ar.edu.unsam.algo3.domain.ClienteConservador
import ar.edu.unsam.algo3.domain.ClienteExquisito
import ar.edu.unsam.algo3.domain.ClienteFiel
import ar.edu.unsam.algo3.domain.ClienteImpaciente
import ar.edu.unsam.algo3.domain.ClienteMarketing
import ar.edu.unsam.algo3.domain.ClienteNormal
import ar.edu.unsam.algo3.domain.ClienteRaro
import ar.edu.unsam.algo3.domain.ClienteVegano
import ar.edu.unsam.algo3.domain.CondicionAny
import ar.edu.unsam.algo3.domain.Delivery
import ar.edu.unsam.algo3.domain.Address
import ar.edu.unsam.algo3.domain.EnviaMailPlatos
import ar.edu.unsam.algo3.domain.EstadoPedido
import ar.edu.unsam.algo3.domain.GrupoAlimentos
import ar.edu.unsam.algo3.domain.Ingredient
import ar.edu.unsam.algo3.domain.Local
import ar.edu.unsam.algo3.domain.MedioDePago
import ar.edu.unsam.algo3.domain.Pedido
import ar.edu.unsam.algo3.domain.Dish
import ar.edu.unsam.algo3.domain.PlatoRepository
import ar.edu.unsam.algo3.exceptions.ValoracionException
import ar.edu.unsam.algo3.domain.ValoracionPendiente
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.uqbar.geodds.Point
import org.uqbar.geodds.Polygon
import java.time.LocalDate

class ClienteSpec: DescribeSpec ({
    var platoRepository: PlatoRepository = PlatoRepository()
    describe("Un cliente vegano no") {


        var cliente = Cliente(
            ingredientesFavoritos = mutableSetOf(),
            ingredientesAEvitar = mutableSetOf(),
            distanciaCercana = 500.0,
            tipoDeCliente = ClienteNormal(),
            nombre = "a",
            apellido = "a",
            username = "a",
            password = "a",
            fechaNacimiento = LocalDate.now(),
            fechaRegistro = LocalDate.now(),
            mail = "cliente@gmail.com",
            direccion = Address(
                calle = "Calle Siempre Viva ",
                altura = "742",
                coordenadas = Point(0, 0)
            )
        )

        var local = Local(
            "Local de Prueba",
            address = Address(
                calle = "Calle Siempre Viva ",
                altura = "742",
                coordenadas = Point(1, 1)
            ),

            mutableSetOf(MedioDePago.EFECTIVO),
            mutableSetOf()
        )
        var dish = Dish(
            nombre = "Plato origen animal",
            descripcion = " plato con ingredientes de origen animal",
            ingredients = arrayListOf<Ingredient>(
                Ingredient(
                    name = "carne",
                    price = 0.0,
                    group = GrupoAlimentos.PROTEINAS
                ),
                Ingredient(
                    name = "Papa",
                    price = 0.0,
                    group = GrupoAlimentos.FRUTAS_Y_VERDURAS
                )

            ),
            local = local,
            porcentajeAcordadoConLocal = 0.0,
            lanzamiento = LocalDate.now(),
            regaliasAutor = 0.0,
            valorBase = 0.0,
            descuento = 0.0
        )

        val mockMailSender = AuditoriaMailSender()

        var pedido = Pedido.crearPedido(
            cliente = cliente,
            plato = listOf(dish),
            local = local,
            medioDePago = MedioDePago.EFECTIVO,
            //mailSender = mockMailSender,
            //platoRepository = platoRepository
        )

        it("El cliente recibe spam"){
            val enviaMailPlatosObserver = pedido.observers.filterIsInstance<EnviaMailPlatos>().firstOrNull()

            enviaMailPlatosObserver shouldNotBe null

            enviaMailPlatosObserver?.mailSender?.enviarCorreo(cliente.mail, "Promo", "cuerpo")
        }

        var delivery = Delivery(
            nombre = "PedroDelivery",
            apellido = "Gutierrez",
            fechaNacimiento = LocalDate.now().minusYears(30),
            username = "pedrolivery",
            password = "pass1234",
            address = Address(calle = "Calle Entrega", altura = "789", coordenadas = Point(2, 2)),
            zonaDeTrabajo = Polygon(
                mutableListOf(
                    Point(-2, -2),
                    Point(2, -2),
                    Point(2, 2),
                    Point(-2, 2)
                )
            ),
            condicionesEntrega = CondicionAny(mutableListOf()),

            )
        //Setea las valoraciones luego de que cliente y local se inicializen
        //var valo = ValoracionPendiente(local)
        //cliente.valoracionesPendientes.add(valo)

        it("El cliente no puede puntuar si no tiene valoraciones pendientes"){
            shouldThrow<ValoracionException> {
                cliente.puntuar(local, 5.0)
            }
        }

//        it("El pedido fue entregado"){
//            cliente.tipoDeCliente = ClienteNormal()
//
//            delivery.tomarPedido(pedido)
//            delivery.entregarPedido(pedido)   //Lo muevo aca asi testeo que no pueda puntuar si no tiene valoraciones pendientes
//
//            pedido.estado shouldBe EstadoPedido.ENTREGADO
//        }

        it("cliente tiene una valoracion pendiente"){
            cliente.confirmarPedido(pedido)
            cliente.valoracionesPendientes.isNotEmpty() shouldBe true
        }

        it("cuando un cliente puntua un local se le elimina la valoracion pendiente"){
            cliente.confirmarPedido(pedido)
            cliente.puntuar(local, 5.0)
            cliente.valoracionesPendientes.isEmpty() shouldBe true
        }

        it("el cliente no puede puntuar cuando la valoracion expiro"){
            var valo = ValoracionPendiente(local, LocalDate.now().minusDays(8))
            cliente.valoracionesPendientes.add(valo)
            shouldThrow<ValoracionException> {
                cliente.puntuar(local, 5.0)
            }
        }
    }

    describe("Tipos de cliente") {
        var ingredientCarne = Ingredient(
            name = "Carne",
            price = 0.0,
            group = GrupoAlimentos.PROTEINAS
        )
        var ingredientPapa = Ingredient(
            name = "Papa",
            price = 0.0,
            group = GrupoAlimentos.FRUTAS_Y_VERDURAS
        )
        var ingredientTomate = Ingredient(
            name = "tomate",
            price = 0.0,
            group = GrupoAlimentos.FRUTAS_Y_VERDURAS
        )

        var cliente = Cliente(
            ingredientesFavoritos = mutableSetOf(ingredientTomate),
            ingredientesAEvitar = mutableSetOf(),
            distanciaCercana = 5.0,
            tipoDeCliente = ClienteVegano(),
            nombre = "a",
            apellido = "a",
            username = "a",
            password = "a",
            fechaNacimiento = LocalDate.now(),
            fechaRegistro = LocalDate.now(),
            mail = "cliente@gmail.com",
            direccion = Address(
                calle = "Calle Siempre Viva ",
                altura = "742",
                coordenadas = Point(0.0, 0.0)
            )
        )
        var local1 = Local(
            "Local de Prueba",
            address = Address(
                calle = "Calle Siempre Viva ",
                altura = "742",
                coordenadas = Point(0.0, 0.001)
            ),
            mutableSetOf(MedioDePago.EFECTIVO),
            mutableSetOf()
        )

        var local2 = Local(
            "Local Nuevisimo que no le va a gustar a cliente fiel",
            address = Address(
                calle = "Calle Siempre Muerta ",
                altura = "7427536",
                coordenadas = Point(25.0, 25.0)
            ),
            mutableSetOf(MedioDePago.EFECTIVO),
            mutableSetOf()
        )

        var dish = Dish(
            nombre = "Plato origen animal",
            descripcion = "plato con ingredientes de origen animal",
            ingredients = arrayListOf<Ingredient>(
                ingredientCarne,
                ingredientPapa

            ),
            local = local1,
            porcentajeAcordadoConLocal = 0.0,
            lanzamiento = LocalDate.now(),
            regaliasAutor = 0.0,
            valorBase = 0.0,
            descuento = 0.0
        )

        val dishDeAutor = Dish(
            nombre = "Tomate de autor",
            descripcion = "Un tomate que lo invento un autor. Totalmente esternocleidomastoideo",
            ingredients = arrayListOf<Ingredient>(
                ingredientTomate
            ),
            local = local2,
            porcentajeAcordadoConLocal = 0.0,
            lanzamiento = LocalDate.now(),
            regaliasAutor = 1.0,
            valorBase = 0.0,
            descuento = 0.0
        )

        var pedido = Pedido(
            cliente = cliente,
            plato = listOf(dish),
            estado = EstadoPedido.PREPARADO,
            local = local1,
            delivery = null,
            medioDePago = MedioDePago.EFECTIVO,
            horaRetiro = null
        )

        it("Cliente vegano: Solo acepta platos sin carne etc") {
            cliente.esPlatoAdecuadoParaCliente(dishDeAutor) shouldBe true
        }

        it("ClienteVegano: El plato no puede ser apto para veganos"){
            cliente.esPlatoAdecuadoParaCliente(dish) shouldBe false
        }

        it("ClienteExquisito: Acepta platos Solo De Autor"){
            cliente.tipoDeCliente = ClienteExquisito()
            cliente.esPlatoAdecuadoParaCliente(dishDeAutor) shouldBe true
        }

        it("ClienteExquisito: No acepta un plato que no es de autor"){
            cliente.tipoDeCliente = ClienteExquisito()
            cliente.esPlatoAdecuadoParaCliente(dish) shouldBe false
        }

        it("ClienteFiel: Solo acepta platos de local1"){
            cliente.tipoDeCliente = ClienteFiel(mutableSetOf(local1))
            cliente.esPlatoAdecuadoParaCliente(dish) shouldBe true
        }

        it("ClienteFiel: No acepta platos de local2"){
            cliente.tipoDeCliente = ClienteFiel(mutableSetOf(local1))
            cliente.esPlatoAdecuadoParaCliente(dishDeAutor) shouldBe false
        }

        it("ClienteConservador: Acepta solo los que tengan solo Tomate"){
            cliente.tipoDeCliente = ClienteConservador()
            cliente.esPlatoAdecuadoParaCliente(dishDeAutor) shouldBe true
        }

        it("ClienteConservador: No acepta los platos que tengan cosas que NO SEAN TOMATE AMO EL TOMATE DAME SOLO TOMATE"){
            cliente.tipoDeCliente = ClienteConservador()
            cliente.esPlatoAdecuadoParaCliente(dish) shouldBe false
        }

        it("ClienteMarketing: solo admite productos que tengan la palabra esternocleidomastoideo en la descripcion"){
            cliente.tipoDeCliente = ClienteMarketing(mutableSetOf("esternocleidomastoideo"))
            cliente.esPlatoAdecuadoParaCliente(dishDeAutor) shouldBe true
        }

        it("ClienteMarketing: no acepta productos que no tengan la palabra esternocleidomastoideo en la descripcion"){
            cliente.tipoDeCliente = ClienteMarketing(mutableSetOf("esternocleidomastoideo"))
            cliente.esPlatoAdecuadoParaCliente(dish) shouldBe false
        }

        it("ClienteImpaciente: solo admite platos de locales cercanos"){
            cliente.tipoDeCliente = ClienteImpaciente()
            cliente.esPlatoAdecuadoParaCliente(dish) shouldBe true
        }

        it("ClienteMarketing: no admite platos de locales lejanos"){
            cliente.tipoDeCliente = ClienteImpaciente()
            cliente.esPlatoAdecuadoParaCliente(dishDeAutor) shouldBe false
        }

        it("ClienteRaro: es exquisito si tiene edad par. Acepta platos solo de autor.") {
            cliente.tipoDeCliente = ClienteRaro()
            cliente.fechaNacimiento = LocalDate.now()

            cliente.esPlatoAdecuadoParaCliente(dishDeAutor) shouldBe true
        }

        it("ClienteRaro: es exquisito si tiene edad par. No acepta platos que no sean de autor.") {
            cliente.tipoDeCliente = ClienteRaro()
            cliente.fechaNacimiento = LocalDate.now()

            cliente.esPlatoAdecuadoParaCliente(dish) shouldBe false
        }

        it("ClienteRaro: es conservador si tiene edad impar.") {
            cliente.tipoDeCliente = ClienteRaro()
            cliente.fechaNacimiento = LocalDate.now().minusYears(1)

            cliente.esPlatoAdecuadoParaCliente(dishDeAutor) shouldBe true
        }

        it("ClienteRaro: es conservador si tiene edad impar.") {
            cliente.tipoDeCliente = ClienteRaro()
            cliente.fechaNacimiento = LocalDate.now().minusYears(1)

            cliente.esPlatoAdecuadoParaCliente(dish) shouldBe false
        }
    }
})