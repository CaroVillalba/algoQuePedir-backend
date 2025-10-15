import ar.edu.unsam.algo3.domain.AuditoriaMailSender
import ar.edu.unsam.algo3.domain.Cliente
import ar.edu.unsam.algo3.domain.ClienteNormal
import ar.edu.unsam.algo3.domain.Cupon
import ar.edu.unsam.algo3.domain.CuponBase
import ar.edu.unsam.algo3.domain.CuponDiaEspecifico
import ar.edu.unsam.algo3.domain.CuponLocalEspecifico
import ar.edu.unsam.algo3.exceptions.CuponNoAplicacionException
import ar.edu.unsam.algo3.domain.CuponTopeMaximo
import ar.edu.unsam.algo3.domain.Address
import ar.edu.unsam.algo3.domain.EstadoPedido
import ar.edu.unsam.algo3.domain.GrupoAlimentos
import ar.edu.unsam.algo3.domain.Ingredient
import ar.edu.unsam.algo3.domain.Local
import ar.edu.unsam.algo3.domain.MedioDePago
import ar.edu.unsam.algo3.domain.Pedido
import ar.edu.unsam.algo3.domain.Dish
import ar.edu.unsam.algo3.domain.PlatoRepository
import ar.edu.unsam.algo3.domain.Valoracion
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.uqbar.geodds.Point
import java.time.LocalDate
import java.time.LocalTime

class CuponSpec : DescribeSpec({
    lateinit var platoRepository: PlatoRepository
    lateinit var local: Local
    lateinit var pedido: Pedido
    lateinit var pedido2: Pedido
    lateinit var cliente: Cliente
    lateinit var ingredient1: Ingredient
    lateinit var ingredient2: Ingredient
    lateinit var ingredient3: Ingredient
    lateinit var ingredient4: Ingredient
    lateinit var ingredient5: Ingredient
    lateinit var dish1: Dish
    lateinit var dish2: Dish
    lateinit var cupon1: CuponDiaEspecifico
    lateinit var cupon2: Cupon
    lateinit var cupon3: CuponTopeMaximo

    beforeTest {
        var mailSender = AuditoriaMailSender()
        platoRepository = PlatoRepository()
        var cupon = CuponBase()
        platoRepository = PlatoRepository()
        cliente = Cliente(
            nombre = "Juan",
            apellido = "Pérez",
            username = "juanperez",
            password = "1234",
            fechaNacimiento = LocalDate.of(1990, 1, 1),
            fechaRegistro = LocalDate.of(2020, 1, 1),
            "cliente@gmail.com",
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

        ingredient1 = Ingredient("Carne", 120.0, GrupoAlimentos.PROTEINAS)
        ingredient2 = Ingredient("Tocino", 80.0, GrupoAlimentos.GRASAS_Y_ACEITES) //ejem, panceta
        ingredient3 = Ingredient("Tomate", 100.0, GrupoAlimentos.FRUTAS_Y_VERDURAS)
        ingredient4 = Ingredient("Papa", 60.0, GrupoAlimentos.CEREALES_Y_TUBERCULOS)
        ingredient5 = Ingredient("Aceite", 70.0, GrupoAlimentos.GRASAS_Y_ACEITES)

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

        dish1 = Dish(
            nombre = "Hamburguesa",
            descripcion = "Hamburguesa gourmet con carne de res premium, tocino crujiente y salsa especial",
            ingredients = listOf(ingredient1, ingredient2, ingredient3),
            local = local,
            porcentajeAcordadoConLocal = 20.0,
            lanzamiento = LocalDate.now().minusDays(2),
            regaliasAutor = 20.0,
            valorBase = 3000.0,
            descuento = 0.0
        )

        dish2 = Dish(
            nombre = "Papas Fritas",
            descripcion = "Papas fritas clásicas, crujientes por fuera y suaves por dentro",
            ingredients = listOf(ingredient4, ingredient5),
            local = local,
            porcentajeAcordadoConLocal = 30.0,
            lanzamiento = LocalDate.now().minusDays(3),
            regaliasAutor = 0.0,
            valorBase = 600.0,
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

        pedido2 = Pedido(
            cliente = cliente,
            plato = listOf(dish1, dish2),
            estado = EstadoPedido.PENDIENTE,
            local = local,
            delivery = null,
            medioDePago = MedioDePago.EFECTIVO,
            horaRetiro = LocalTime.of(10, 0),
            cuponAplicado = cupon
        )

        cupon1 = CuponDiaEspecifico(
            LocalDate.of(2025, 5, 21),
            50,
            0.1,
            false,
            LocalDate.now().dayOfWeek
        )

        cupon2 = CuponLocalEspecifico(
            LocalDate.of(2025, 5, 30),
            40,
            0.2,
            false,
            listOf(local)
        )

        cupon3 = CuponTopeMaximo(
            LocalDate.of(2025, 5, 28),
            50,
            0.05,
            false,
            200.0,
            0.15
        )
    }

    describe("Funcionalidad de Cupon") {

        it("Los cupones son aplicables") {
            cupon1.validarQueAplica(pedido) shouldBe true
            cupon2.validarQueAplica(pedido) shouldBe true
            cupon3.validarQueAplica(pedido) shouldBe true
        }

        it("Aplica descuento en día específico") {
            //Debe coincidir el dia del cupon con el de algun plato
            //Total pedido: 4355.30
            //Porcentaje Descuento = 10% + 10% = 20%
            //Descuento = 4355.30 * 0.20 = 871.06
            //Total con descuento = 4355.30 - 871.06 = 3484.24

            dish1.lanzamiento=LocalDate.now();
            cupon1.aplicar(pedido)
            pedido.totalDelPedidoConCupon(cupon1) shouldBe 3484.24
        }

        it("Aplica descuento en día no específico") {
            //NO Debe coincidir el dia del cupon con el de ningun plato
            //Total pedido: 4382.10
            //Porcentaje Descuento = 10% + 5% = 15%
            //Descuento = 4382.10 * 0.15 = 657.31
            //Total con descuento = 4382.10 - 657.31 = 3724.79

            pedido.plato.forEach { it.lanzamiento=LocalDate.now().minusDays(1) }
            cupon1.aplicar(pedido)
            pedido.totalDelPedidoConCupon(cupon1) shouldBe 3724.79
        }

        it("Aplica descuento de local específico siendo pedido certificado") {
            cupon2.aplicar(pedido)
            pedido.totalDelPedidoConCupon(cupon2) shouldBe 2556.24
        }

        it("Aplica descuento de local específico sin ser pedido certificado") {
            local.ratings.clear() // El pedido ya no es certificado
            cupon2.aplicar(pedido)
            pedido.totalDelPedidoConCupon(cupon2) shouldBe 3056.24
        }

        it("Aplica descuento con tope máximo") {
            cupon3.aplicar(pedido)
            pedido.totalDelPedidoConCupon(cupon3) shouldBe 4023.04
        }

        it("Aplica descuento sin tope máximo") {
            cupon3.topeMaximo = 1000.0
            cupon3.aplicar(pedido)
            pedido.totalDelPedidoConCupon(cupon3) shouldBe 3556.24
        }

        it("Cupon no se puede aplicar"){
            cupon1.fueAplicado = true
            shouldThrow<CuponNoAplicacionException> {
                pedido.aplicarDescuento(cupon1)
            }
        }

        it("No rompe si no tiene cupon aplicado"){
            pedido2.totalDelPedidoConCupon(cupon2) shouldBe 2556.24
        }
    }
})