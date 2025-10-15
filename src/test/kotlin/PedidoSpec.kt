
import ar.edu.unsam.algo3.domain.AuditoriaMailSender
import ar.edu.unsam.algo3.domain.Cliente
import ar.edu.unsam.algo3.domain.ClienteFiel
import ar.edu.unsam.algo3.domain.Cupon
import ar.edu.unsam.algo3.domain.CuponBase
import ar.edu.unsam.algo3.domain.Address
import ar.edu.unsam.algo3.domain.EstadoPedido
import ar.edu.unsam.algo3.domain.GrupoAlimentos
import ar.edu.unsam.algo3.domain.Ingredient
import ar.edu.unsam.algo3.domain.Local
import ar.edu.unsam.algo3.domain.MedioDePago
import ar.edu.unsam.algo3.exceptions.MedioDePagoInvalidoException
import ar.edu.unsam.algo3.domain.Pedido
import ar.edu.unsam.algo3.domain.Dish
import ar.edu.unsam.algo3.exceptions.PlatoInvalidoException
import ar.edu.unsam.algo3.domain.PlatoRepository
import ar.edu.unsam.algo3.domain.Valoracion
import org.uqbar.geodds.Point
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import org.junit.jupiter.api.BeforeEach
import java.math.RoundingMode
import java.time.LocalTime

class PedidoSpec {
    private lateinit var cliente: Cliente
    private lateinit var platoRepository: PlatoRepository
    private var ingredient1: Ingredient? = null
    private var ingredient2: Ingredient? = null
    private var ingredient3: Ingredient? = null
    private var ingredient4: Ingredient? = null
    private var ingredient5: Ingredient? = null
    private lateinit var dish1: Dish
    private lateinit var dish2: Dish
    private lateinit var dish3: Dish

    private lateinit var local: Local
    private lateinit var localOtro: Local
    private lateinit var localConBajaCalificacion: Local

    @BeforeEach
    fun setUp() {
        local = Local(
            name = "Comida Rápida",
            address = Address(
                calle = "Calle Falsa",
                altura = "123",
                coordenadas = Point(2, 2)
            ),
            paymentMethods = mutableSetOf(MedioDePago.EFECTIVO, MedioDePago.CREDITO, MedioDePago.QR),
            ratings = mutableSetOf()
        )

        localOtro = Local(
            name = "Otra Comida Rápida",
            address = Address(
                calle = "Otra Calle Falsa",
                altura = "Otra 123",
                coordenadas = Point(2, 2)
            ),

            paymentMethods = mutableSetOf(MedioDePago.EFECTIVO, MedioDePago.CREDITO, MedioDePago.QR),
            ratings = mutableSetOf()
        )
        platoRepository = PlatoRepository()
        ingredient1 = Ingredient(name = "Tomate", price = 100.0, group = GrupoAlimentos.FRUTAS_Y_VERDURAS)
        ingredient2 =
            Ingredient(name = "Harina", price = 500.0, group = GrupoAlimentos.CEREALES_Y_TUBERCULOS)
        ingredient3 = Ingredient(name = "Queso", price = 80.0, group = GrupoAlimentos.LACTEOS)
        ingredient4 = Ingredient(name = "Aceite", price = 120.0, group = GrupoAlimentos.GRASAS_Y_ACEITES)
        ingredient5 = Ingredient(name = "Lechuga", price = 110.0, group = GrupoAlimentos.FRUTAS_Y_VERDURAS)


        cliente = Cliente(
            nombre = "Juan",
            apellido = "Perez",
            username = "juan456",
            password = "1234",
            fechaNacimiento = LocalDate.of(2002, 5, 24),
            fechaRegistro = LocalDate.now().minusYears(2),
            "cliente@gmail.com",
            direccion = Address(
                calle = "Calle Verde",
                altura = "420",
                coordenadas = Point(-1, 6)
            ),
            distanciaCercana = 10.0,
            tipoDeCliente = ClienteFiel(mutableSetOf(local)),
            ingredientesAEvitar = mutableSetOf(ingredient1!!, ingredient3!!),
        )

        //Setea las valoraciones luego de que cliente y local se inicializen
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
            nombre = "Pasta",
            descripcion = "Un plato de pasta clásica y deliciosa",
            valorBase = 500.0,
            ingredients = listOf(ingredient2!!, ingredient4!!),
            local = local,
            lanzamiento = LocalDate.now(),
            regaliasAutor = 0.0,
            descuento = 0.0,
            porcentajeAcordadoConLocal = 30.0
        )

        dish2 = Dish(
            nombre = "Pizza Gourmet",
            descripcion = "Pizza clásica con un toque especial del chef",
            valorBase = 700.0,
            ingredients = listOf(ingredient1!!, ingredient3!!),
            local = local,
            lanzamiento = LocalDate.now().minusMonths(3),
            regaliasAutor = 15.0,
            descuento = 0.2,
            porcentajeAcordadoConLocal = 40.0
        )

        dish3 = Dish(
            nombre = "Ensalada César",
            descripcion = "Una ensalada fresca con ingredientes de alta calidad",
            valorBase = 450.0,
            ingredients = listOf(ingredient1!!, ingredient4!!, ingredient5!!),
            local = local,
            lanzamiento = LocalDate.now().minusWeeks(2),
            regaliasAutor = 0.0,
            descuento = 0.1,
            porcentajeAcordadoConLocal = 20.0
        )
    }


    @Test
    fun `validarPlatos lanza excepcion si un plato no pertenece al local`() {
        var mailSender = AuditoriaMailSender()
        var cupon = CuponBase()
        val exception = assertThrows(PlatoInvalidoException::class.java) {
            val p = Pedido(
                cliente = cliente,
                plato = listOf(dish1, dish3),
                local = localOtro,
                delivery = null,
                medioDePago = MedioDePago.EFECTIVO,
                horaRetiro = LocalTime.of(10, 0),
                cuponAplicado = cupon
            )

            p.marcarComoPreparado()
            p.marcarComoEntregado()
        }
        assertEquals("El plato Pasta no pertenece al local.", exception.message)
    }

    @Test
    fun `validarPlatos lanza excepcion si un plato contiene ingrediente a evitar`() {
        val exception = assertThrows(PlatoInvalidoException::class.java) {
            var mailSender = AuditoriaMailSender()
            var cupon = CuponBase()
            val p = Pedido(
                cliente = cliente,
                plato = listOf(dish2),
                local = local,
                delivery = null,
                medioDePago = MedioDePago.EFECTIVO,
                horaRetiro = LocalTime.of(10, 0),
                cuponAplicado = cupon
            )

            p.marcarComoPreparado()
            p.marcarComoEntregado()
        }
        assertEquals("El plato Pizza Gourmet contiene ingrediente(s) que el cliente evita.", exception.message)
    }

    @Test
    fun `validarMedioDePago lanza excepcion si el medio de pago no es valido`() {
        var mailSender = AuditoriaMailSender()
        var cupon = CuponBase()
        val exception = assertThrows(MedioDePagoInvalidoException::class.java) {
            val p = Pedido(
                cliente = cliente,
                plato = listOf(dish1),
                local = local,
                delivery = null,
                medioDePago = MedioDePago.BITCOIN,
                horaRetiro = LocalTime.of(10, 0),
                cuponAplicado = cupon
            )
            p.marcarComoPreparado()
            p.marcarComoEntregado()
        }
        assertEquals("El medio de pago BITCOIN no es válido para este local.", exception.message)
    }

    @Test
    fun `esPedidoCertificado devuelve true si cumple condiciones`() {
        var mailSender = AuditoriaMailSender()
        var cupon = CuponBase()
        val pedido = Pedido(
            cliente = cliente,
            plato = listOf(dish1),
            local = local,
            delivery = null,
            medioDePago = MedioDePago.EFECTIVO,
            horaRetiro = LocalTime.of(10, 0),
            cuponAplicado = cupon
        )
        assertTrue(pedido.esPedidoCertificado())
    }

    @Test
    fun `esPedidoCertificado devuelve false si no cumple antiguedad`() {
        var mailSender = AuditoriaMailSender()
        var cupon = CuponBase()
        val nuevoCliente = Cliente(
            nombre = "Pedro",
            apellido = "Gomez",
            fechaRegistro = LocalDate.now().minusMonths(6),
            username = "pedro123",
            password = "password123",
            fechaNacimiento = LocalDate.of(1990, 1, 1),
            mail = "clientenuevo@gmail.com",
            direccion = Address(
                calle = "Calle Verde",
                altura = "420",
                coordenadas = Point(-1, 6)
            ),
            distanciaCercana = 0.0,
            tipoDeCliente = ClienteFiel(mutableSetOf(local)),
            ingredientesAEvitar = mutableSetOf(),
        )
        val pedido = Pedido(
            cliente = nuevoCliente,
            plato = listOf(dish1),
            local = local,
            delivery = null,
            medioDePago = MedioDePago.EFECTIVO,
            horaRetiro = LocalTime.of(10, 0),
            cuponAplicado = cupon
        )
        assertFalse(pedido.esPedidoCertificado())
    }

    @Test
    fun `esPedidoCertificado devuelve false si valoraciones promedio fuera del rango`() {
        var mailSender = AuditoriaMailSender()
        var cupon = CuponBase()
        localConBajaCalificacion = Local(
            name = "Comida Rápida",
            address = Address(
                calle = "Calle Sin Nombre",
                altura = "456",
                coordenadas = Point(-2, -8)
            ),
            ratings = mutableSetOf(
                Valoracion(
                    cliente = cliente,
                    local = local,
                    puntuacion = 3.0
                ),
                Valoracion(
                    cliente = cliente,
                    local = local,
                    puntuacion = 3.5
                ),
                Valoracion(
                    cliente = cliente,
                    local = local,
                    puntuacion = 2.5
                ),
            ),
            paymentMethods = mutableSetOf(MedioDePago.EFECTIVO, MedioDePago.CREDITO)
        )

        var dishPastaOscura = Dish(
            nombre = "Pasta Oscura",
            descripcion = "Un plato de pasta clásica y deliciosa, pero oscura",
            valorBase = 500.0,
            ingredients = listOf(ingredient2!!, ingredient4!!),
            local = localConBajaCalificacion,
            lanzamiento = LocalDate.now(),
            regaliasAutor = 0.0,
            descuento = 0.0,
            porcentajeAcordadoConLocal = 30.0
        )

        val pedido = Pedido(
            cliente = cliente,
            plato = listOf(dishPastaOscura),
            local = localConBajaCalificacion,
            delivery = null,
            medioDePago = MedioDePago.EFECTIVO,
            horaRetiro = LocalTime.of(10, 0),
            cuponAplicado = cupon
        )
        assertFalse(pedido.esPedidoCertificado())
    }

    @Test
    fun `totalDelPedido calcula correctamente costo con efectivo`() {
        var mailSender = AuditoriaMailSender()
        var cupon = CuponBase()
        cliente.ingredientesAEvitar.clear()
        val pedido = Pedido(
            cliente = cliente,
            plato = listOf(dish1, dish2),
            local = local,
            delivery = null,
            medioDePago = MedioDePago.EFECTIVO,
            horaRetiro = LocalTime.of(10, 0),
            cuponAplicado = cupon
        )
        assertEquals(2404.97, pedido.totalDelPedido())
    }

    @Test
    fun `totalDelPedido calcula correctamente costo con tarjeta`() {
        var mailSender = AuditoriaMailSender()
        var cupon = CuponBase()
        cliente.ingredientesAEvitar.clear()
        val pedido = Pedido(
            cliente = cliente,
            plato = listOf(dish1, dish2),
            local = local,
            delivery = null,
            medioDePago = MedioDePago.CREDITO,
            horaRetiro = LocalTime.of(10, 0),
            cuponAplicado = cupon
        )
        pedido.plato.forEach{p -> println(p.valorDeVenta())}
        assertEquals(2525.22, pedido.totalDelPedido())
    }

    @Test
    fun `cambiar estados del pedido`() {
        var mailSender = AuditoriaMailSender()
        var cupon = CuponBase()
        val pedido = Pedido(
            cliente = cliente,
            plato = listOf(dish1),
            local = local,
            delivery = null,
            medioDePago = MedioDePago.EFECTIVO,
            horaRetiro = LocalTime.of(10, 0),
            cuponAplicado = cupon
        )

        pedido.marcarComoPreparado()
        assertEquals(EstadoPedido.PREPARADO, pedido.estado)

        pedido.marcarComoEntregado()
        assertEquals(EstadoPedido.ENTREGADO, pedido.estado)

    }

    @Test
    fun `total del pedido aplicando cupones`() {
        var mailSender = AuditoriaMailSender()
        var cupon1 = CuponBase()
        cliente.ingredientesAEvitar.clear()
        val pedido = Pedido(
            cliente = cliente,
            plato = listOf(dish1, dish2),
            local = local,
            delivery = null,
            medioDePago = MedioDePago.EFECTIVO,
            horaRetiro = LocalTime.of(10, 0),
            cuponAplicado = cupon1
        )
        val cupon = object : Cupon(
            fechaEmision = LocalDate.of(2025, 4, 20),
            duracion = 10,
            porcentajeBaseDescuento = 0.1, // 10% de descuento
            fueAplicado = false
        ) {
            override fun esAplicableEspecifico(pedido: Pedido): Boolean = true
            override fun calcularDescuentoEspecifico(pedido: Pedido): Double = 0.0
        }

        val totalSinCupon = pedido.totalDelPedido()
        val totalConCupon = pedido.totalDelPedidoConCupon(cupon)

        assertEquals((totalSinCupon * 0.9).toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toDouble(), totalConCupon)
    }
}
