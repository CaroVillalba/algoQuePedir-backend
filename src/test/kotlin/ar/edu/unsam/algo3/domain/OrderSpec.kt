
package ar.edu.unsam.algo3.domain
import ar.edu.unsam.algo3.exceptions.InvalidPaymentMethodException
import ar.edu.unsam.algo3.exceptions.InvalidDishException
import org.uqbar.geodds.Point
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.time.LocalDate
import org.junit.jupiter.api.BeforeEach
import java.math.RoundingMode
import java.time.LocalTime

class OrderSpec {
    private lateinit var client: Client
    private lateinit var dishRepository: DishRepository
    private var ingredient1: Ingredient? = null
    private var ingredient2: Ingredient? = null
    private var ingredient3: Ingredient? = null
    private var ingredient4: Ingredient? = null
    private var ingredient5: Ingredient? = null
    private lateinit var dish1: Dish
    private lateinit var dish2: Dish
    private lateinit var dish3: Dish

    private lateinit var restaurant: Local
    private lateinit var otherRestaurant: Local
    private lateinit var lowRatedRestaurant: Local

    @BeforeEach
    fun setUp() {
        restaurant = Local(
            "Comida Rápida",
            "https://super-restaurante.png",
            Address(
                "Calle Falsa",
                "123",
                Point(2, 2)
            ),
            0.0,
            0.0,
            mutableSetOf(PaymentMethod.EFECTIVO, PaymentMethod.CREDITO, PaymentMethod.QR),
            mutableSetOf()
        )

        otherRestaurant = Local(
            "Otra Comida Rápida",
            "https://otro-restaurante.png",
            Address(
                "Otra Calle Falsa",
                "Otra 123",
                Point(2, 2)
            ),
            0.0,
            0.0,
            mutableSetOf(PaymentMethod.EFECTIVO, PaymentMethod.CREDITO, PaymentMethod.QR),
            mutableSetOf()
        )

        dishRepository = DishRepository()
        ingredient1 = Ingredient("Tomate", 100.0, FoodGroup.FRUTAS_Y_VERDURAS,1)
        ingredient2 = Ingredient("Harina", 500.0, FoodGroup.CEREALES_Y_TUBERCULOS,2)
        ingredient3 = Ingredient("Queso", 80.0, FoodGroup.LACTEOS,3)
        ingredient4 = Ingredient("Aceite", 120.0, FoodGroup.GRASAS_Y_ACEITES,4)
        ingredient5 = Ingredient("Lechuga", 110.0, FoodGroup.FRUTAS_Y_VERDURAS,5)

        client = Client(
            "Juan",
            "Perez",
            "juan456",
            "1234",
            LocalDate.of(2002, 5, 24),
            "cliente@gmail.com",
            Address(
                "Calle Verde",
                "420",
                Point(-1, 6)
            ),
            "a",
            mutableSetOf(ingredient1!!, ingredient3!!),
            mutableSetOf(ingredient1!!, ingredient3!!),
            10.0,
            LoyalClient(mutableSetOf(restaurant)),

            )

        //Setea las valoraciones luego de que cliente y local se inicializen
        restaurant.ratings = mutableSetOf(
            Rating(
                client,
                restaurant,
                4.5
            ),
            Rating(
                client,
                restaurant,
                5.0
            ),
            Rating(
                client = client,
                restaurant,
                4.8
            )
        )

        dish1 = Dish(
            "https://super-plato1.png",
            "Pasta",
            "Un plato de pasta clásica y deliciosa",
            listOf(ingredient2!!, ingredient4!!),
            restaurant,
            30.0,
            LocalDate.now(),
            0.0,
            500.0,
            0.0
        )

        dish2 = Dish(
            "https://super-plato2.png",
            "Pizza Gourmet",
            "Pizza clásica con un toque especial del chef",
            listOf(ingredient1!!, ingredient3!!),
            restaurant,
            40.0,
            LocalDate.now().minusMonths(3),
            5.0,
            700.0,
            0.2
        )

        dish3 = Dish(
            "https://super-plato3.png",
            "Ensalada César",
            "Una ensalada fresca con ingredientes de alta calidad",
            listOf(ingredient1!!, ingredient4!!, ingredient5!!),
            restaurant,
            20.0,
            LocalDate.now().minusWeeks(2),
            0.0,
            450.0,
            0.1
        )
    }

    @Test
    fun `validateDishes lanza excepcion si un plato no pertenece al local`() {
        var mailSender = AuditMailSender()
        var coupon = BaseCoupon()
        val exception = assertThrows(InvalidDishException::class.java) {
            val order = Order(
                client,
                listOf(dish1, dish3),
                OrderStatus.PENDIENTE,
                otherRestaurant,
                null,
                PaymentMethod.EFECTIVO,
                LocalTime.of(10, 0),
                coupon
            )

            order.markAsPrepared()
            order.markAsDelivered()
        }
        assertEquals("El plato Pasta no pertenece al local.", exception.message)
    }

    @Test
    fun `validateDishes lanza excepcion si un plato contiene ingrediente a evitar`() {
        val exception = assertThrows(InvalidDishException::class.java) {
            var mailSender = AuditMailSender()
            var coupon = BaseCoupon()
            val order = Order(
                client,
                listOf(dish2),
                OrderStatus.PENDIENTE,
                restaurant,
                null,
                PaymentMethod.EFECTIVO,
                LocalTime.of(10, 0),
                coupon
            )

            order.markAsPrepared()
            order.markAsDelivered()
        }
        assertEquals("El plato Pizza Gourmet contiene ingrediente(s) que el cliente evita.", exception.message)
    }

    @Test
    fun `validatePaymentMethod lanza excepcion si el medio de pago no es valido`() {
        var mailSender = AuditMailSender()
        var coupon = BaseCoupon()
        val exception = assertThrows(InvalidPaymentMethodException::class.java) {
            val order = Order(
                client,
                listOf(dish1),
                OrderStatus.PENDIENTE,
                restaurant,
                null,
                PaymentMethod.BITCOIN,
                LocalTime.of(10, 0),
                coupon
            )
            order.markAsPrepared()
            order.markAsDelivered()
        }
        assertEquals("El medio de pago BITCOIN no es válido para este local.", exception.message)
    }

    @Test
    fun `isCertifiedOrder devuelve true si cumple condiciones`() {
        var mailSender = AuditMailSender()
        var coupon = BaseCoupon()
        val order = Order(
            client,
            listOf(dish1),
            OrderStatus.PENDIENTE,
            restaurant,
            null,
            PaymentMethod.EFECTIVO,
            LocalTime.of(10, 0),
            coupon
        )
        assertTrue(order.isCertifiedOrder())
    }

    @Test
    fun `isCertifiedOrder devuelve false si no cumple antiguedad`() {
        var mailSender = AuditMailSender()
        var coupon = BaseCoupon()
        val newClient = Client(
            "Pedro",
            "Gomez",
            "pedro123",
            "password123",
            LocalDate.of(1990, 1, 1),
            "clientenuevo@gmail.com",
            Address(
                "Calle Verde",
                "420",
                Point(-1, 6)
            ),
            "a",
            mutableSetOf(),
            mutableSetOf(),
            0.0,
            LoyalClient(mutableSetOf(restaurant)),
        )
        val order = Order(
            newClient,
            listOf(dish1),
            OrderStatus.PENDIENTE,
            restaurant,
            null,
            PaymentMethod.EFECTIVO,
            LocalTime.of(10, 0),
            coupon
        )
        assertFalse(order.isCertifiedOrder())
    }

    @Test
    fun `isCertifiedOrder devuelve false si valoraciones promedio fuera del rango`() {
        var mailSender = AuditMailSender()
        var coupon = BaseCoupon()
        lowRatedRestaurant = Local(
            "Local Baja Calificación",
            "https://mal--restaurante.png",
            Address(
                "Calle Sin Nombre",
                "456",
                Point(-2, -8)
            ),
            0.0,
            0.0,
            mutableSetOf(PaymentMethod.EFECTIVO, PaymentMethod.CREDITO) ,
            mutableSetOf(
                Rating(
                    client,
                    restaurant,
                    3.0
                ),
                Rating(
                    client,
                    restaurant,
                    3.5
                ),
                Rating(
                    client,
                    restaurant,
                    2.5
                ),
            ),
        )

        var lowRatedDish = Dish(
            "https://bad-dish.png",
            "Pasta Oscura",
            "Un plato de pasta clásica y deliciosa, pero oscura",
            listOf(ingredient2!!, ingredient4!!),
            lowRatedRestaurant,
            30.0,
            LocalDate.now(),
            0.0,
            500.0,
            0.0,
        )

        val order = Order(
            client,
            listOf(lowRatedDish),
            OrderStatus.PENDIENTE,
            lowRatedRestaurant,
            null,
            PaymentMethod.EFECTIVO,
            LocalTime.of(10, 0),
            coupon
        )
        assertFalse(order.isCertifiedOrder())
    }

    @Test
    fun `getOrderTotal calcula correctamente costo con efectivo`() {
        var mailSender = AuditMailSender()
        var coupon = BaseCoupon()
        client.ingredientsToAvoid.clear()
        val order = Order(
            client,
            listOf(dish1, dish2),
            OrderStatus.PENDIENTE,
            restaurant,
            null,
            PaymentMethod.EFECTIVO,
            LocalTime.of(10, 0),
            coupon
        )
        assertEquals(2328.11, order.getOrderTotal())
    }

    @Test
    fun `getOrderTotal calcula correctamente costo con tarjeta`() {
        var mailSender = AuditMailSender()
        var coupon = BaseCoupon()
        client.ingredientsToAvoid.clear()
        val order = Order(
            client,
            listOf(dish1, dish2),
            OrderStatus.PENDIENTE,
            restaurant,
            null,
            PaymentMethod.CREDITO,
            LocalTime.of(10, 0),
            coupon
        )
        order.dishes.forEach { d -> println(d.salePrice()) }
        assertEquals(2444.52, order.getOrderTotal())
    }

    @Test
    fun `cambiar estados del pedido`() {
        var mailSender = AuditMailSender()
        var coupon = BaseCoupon()
        val order = Order(
            client,
            listOf(dish1),
            OrderStatus.PENDIENTE,
            restaurant,
            null,
            PaymentMethod.EFECTIVO,
            LocalTime.of(10, 0),
            coupon
        )

        order.markAsPrepared()
        assertEquals(OrderStatus.PREPARADO, order.status)

        order.markAsDelivered()
        assertEquals(OrderStatus.ENTREGADO, order.status)
    }

    @Test
    fun `total del pedido aplicando cupones`() {
        var mailSender = AuditMailSender()
        var coupon1 = BaseCoupon()
        client.ingredientsToAvoid.clear()
        val order = Order(
            client,
            listOf(dish1, dish2),
            OrderStatus.PENDIENTE,
            restaurant,
            null,
            PaymentMethod.EFECTIVO,
            LocalTime.of(10, 0),
            coupon1
        )
        val coupon = object : Coupon(
            LocalDate.of(2025, 4, 20),
            10,
            0.1, // 10% de descuento
            false
        ) {
            override fun isSpecificallyApplicable(order: Order): Boolean = true
            override fun calculateSpecificDiscount(order: Order): Double = 0.0
        }

        val totalWithoutCoupon = order.getOrderTotal()
        val totalWithCoupon = order.getOrderTotalWithCoupon(coupon)

        assertEquals(
            (totalWithoutCoupon * 0.9).toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toDouble(),
            totalWithCoupon
        )
    }
}