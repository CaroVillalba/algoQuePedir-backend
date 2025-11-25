
package ar.edu.unsam.algo3.domain

import ar.edu.unsam.algo3.exceptions.CouponNotApplicableException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.uqbar.geodds.Point
import java.time.LocalDate
import java.time.LocalTime

class CouponSpec : DescribeSpec({
    lateinit var dishRepository: DishRepository
    lateinit var local: Local
    lateinit var order1: Order
    lateinit var order2: Order
    lateinit var client: Client
    lateinit var ingredient1: Ingredient
    lateinit var ingredient2: Ingredient
    lateinit var ingredient3: Ingredient
    lateinit var ingredient4: Ingredient
    lateinit var ingredient5: Ingredient
    lateinit var dish1: Dish
    lateinit var dish2: Dish
    lateinit var specificDayCoupon: SpecificDayCoupon
    lateinit var specificLocalCoupon: Coupon
    lateinit var maxLimitCoupon: MaxCapCoupon

    beforeTest {
        val mailSender = AuditMailSender()
        dishRepository = DishRepository()
        val baseCoupon = BaseCoupon()

        client = Client(
            "Juan",
            "Pérez",
            "juanperez",
            "1234",
            LocalDate.of(1990, 1, 1),
            "cliente@gmail.com",
            LocalDate.now().minusYears(30),
            Address(
                "Calle Falsa",
                "123",
                Point(2, 2)
            ),
            "a",
            mutableSetOf(),
            mutableSetOf(),
            10.0,
            NormalClient()
        )

        ingredient1 = Ingredient("Carne", 120.0, FoodGroup.PROTEINAS, 1)
        ingredient2 = Ingredient("Tocino", 80.0, FoodGroup.GRASAS_Y_ACEITES, 2)
        ingredient3 = Ingredient("Tomate", 100.0, FoodGroup.FRUTAS_Y_VERDURAS,3)
        ingredient4 = Ingredient("Papa", 60.0, FoodGroup.CEREALES_Y_TUBERCULOS,4)
        ingredient5 = Ingredient("Aceite", 70.0, FoodGroup.GRASAS_Y_ACEITES,5)

        local = Local(
            "Comida Rápida",
            "https://super-local.png",
            Address(
                "Calle",
                "456",
                Point(-1, -1)
            ),
            0.0,
            0.0,
            mutableSetOf(PaymentMethod.EFECTIVO, PaymentMethod.CREDITO, PaymentMethod.QR),
            mutableSetOf()
        )

        local.ratings = mutableSetOf(
            Rating(
                client,
                local,
                4.5
            ),
            Rating(
                client,
                local,
                5.0
            ),
            Rating(
                client,
                local,
                4.8
            )
        )

        dish1 = Dish(
            "https://super-plato1.png",
            "Hamburguesa",
            "Hamburguesa gourmet con carne de res premium, tocino crujiente y salsa especial",
            listOf(ingredient1, ingredient2, ingredient3),
            local,
            20.0,
            LocalDate.now().minusDays(2),
            20.0,
            3000.0,
            0.0
        )

        dish2 = Dish(
            "https://super-plato2.png",
            "Papas Fritas",
            "Papas fritas clásicas, crujientes por fuera y suaves por dentro",
            listOf(ingredient4, ingredient5),
            local,
            30.0,
            LocalDate.now().minusDays(3),
            0.0,
            600.0,
            50.0
        )

        order1 = Order(
            client,
            listOf(dish1, dish2),
            OrderStatus.PENDIENTE,
            local,
            null,
            PaymentMethod.EFECTIVO,
            LocalTime.of(10, 0),
            baseCoupon
        )

        order2 = Order(
            client,
            listOf(dish1, dish2),
            OrderStatus.PENDIENTE,
            local,
            null,
            PaymentMethod.EFECTIVO,
            LocalTime.of(10, 0),
            baseCoupon
        )

        specificDayCoupon = SpecificDayCoupon(
            LocalDate.of(2025, 12, 21),
            50,
            0.1,
            false,
            LocalDate.now().dayOfWeek
        )

        specificLocalCoupon = SpecificLocalCoupon(
            LocalDate.of(2025, 12, 30),
            40,
            0.2,
            false,
            listOf(local)
        )

        maxLimitCoupon = MaxCapCoupon(
            LocalDate.of(2025, 12, 28),
            50,
            0.05,
            false,
            200.0,
            0.15
        )
    }

    describe("Funcionalidad de Cupon") {
        it("Los cupones son aplicables") {
            specificDayCoupon.validateApplicability(order1) shouldBe true
            specificLocalCoupon.validateApplicability(order1) shouldBe true
            maxLimitCoupon.validateApplicability(order1) shouldBe true
        }

        it("Aplica descuento en día específico") {
            dish1.releaseDate = LocalDate.now()
            specificDayCoupon.apply(order1)
            order1.getOrderTotalWithCoupon(specificDayCoupon) shouldBe 3484.24
        }

        it("Aplica descuento en día no específico") {
            order1.dishes.forEach { it.releaseDate = LocalDate.now().minusDays(1) }
            specificDayCoupon.apply(order1)
            order1.getOrderTotalWithCoupon(specificDayCoupon) shouldBe 3724.79
        }

        it("Aplica descuento de local específico siendo pedido certificado") {
            specificLocalCoupon.apply(order1)
            order1.getOrderTotalWithCoupon(specificLocalCoupon) shouldBe 2556.24
        }

        it("Aplica descuento de local específico sin ser pedido certificado") {
            local.ratings.clear()
            specificLocalCoupon.apply(order1)
            order1.getOrderTotalWithCoupon(specificLocalCoupon) shouldBe 3056.24
        }

        it("Aplica descuento con tope máximo") {
            maxLimitCoupon.apply(order1)
            order1.getOrderTotalWithCoupon(maxLimitCoupon) shouldBe 4023.04
        }

        it("Aplica descuento sin tope máximo") {
            maxLimitCoupon.maxCap = 1000.0
            maxLimitCoupon.apply(order1)
            order1.getOrderTotalWithCoupon(maxLimitCoupon) shouldBe 3556.24
        }

        it("Cupon no se puede aplicar") {
            specificDayCoupon.wasApplied = true
            shouldThrow<CouponNotApplicableException> {
                order1.applyDiscount(specificDayCoupon)
            }
        }

        it("No rompe si no tiene cupon aplicado") {
            order2.getOrderTotalWithCoupon(specificLocalCoupon) shouldBe 2556.24
        }
    }
})