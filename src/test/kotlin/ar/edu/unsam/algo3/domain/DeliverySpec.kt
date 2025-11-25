
package ar.edu.unsam.algo3.domain

import ar.edu.unsam.algo3.exceptions.InvalidOrderException
import ar.edu.unsam.algo3.exceptions.OrderStateException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import org.uqbar.geodds.Point
import org.uqbar.geodds.Polygon
import java.time.LocalDate
import java.time.LocalTime

class DeliverySpec : DescribeSpec({
    lateinit var dishRepository: DishRepository
    lateinit var delivery: Delivery
    lateinit var order: Order
    lateinit var client: Client
    lateinit var ingredient1: Ingredient
    lateinit var ingredient2: Ingredient
    lateinit var ingredient3: Ingredient
    lateinit var ingredient4: Ingredient
    lateinit var ingredient5: Ingredient
    lateinit var local: Local
    lateinit var local2: Local
    lateinit var dish1: Dish
    lateinit var dish2: Dish
    lateinit var deliveryConditionsList: MutableList<DeliveryCondition>

    beforeTest {
        dishRepository = DishRepository()
        val mailSender = AuditMailSender()
        val baseCoupon = BaseCoupon()

        client = Client(
            "Juan",
            "Pérez",
            "juanperez",
            "1234",
            LocalDate.of(1990, 1, 1),
            "cliente@mail.com",
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
            Rating(client, local, 4.5),
            Rating(client, local, 5.0),
            Rating(client, local, 4.8)
        )

        local2 = Local(
            "Comida Rápida2",
            "https://super-local2.png",
            Address(
                "Calle2",
                "4562",
                Point(-1, -1)
            ),
            0.0,
            0.0,
            mutableSetOf(PaymentMethod.EFECTIVO, PaymentMethod.CREDITO, PaymentMethod.QR),
            mutableSetOf(
                Rating(client, local, 1.0),
                Rating(client, local, 1.0),
                Rating(client, local, 1.0)
            )
        )

        ingredient1 = Ingredient("Carne", 120.0, FoodGroup.PROTEINAS,1)
        ingredient2 = Ingredient("Tocino", 80.0, FoodGroup.GRASAS_Y_ACEITES,2)
        ingredient3 = Ingredient("Tomate", 100.0, FoodGroup.FRUTAS_Y_VERDURAS,3)
        ingredient4 = Ingredient("Papa", 60.0, FoodGroup.CEREALES_Y_TUBERCULOS,4)
        ingredient5 = Ingredient("Aceite", 70.0, FoodGroup.GRASAS_Y_ACEITES,5)

        dish1 = Dish(
            "https://super-plato1.png",
            "Hamburguesa",
            "Hamburguesa gourmet con carne de res premium, tocino crujiente y salsa especial",
            listOf(ingredient1, ingredient2, ingredient3),
            local,
            20.0,
            LocalDate.now().minusWeeks(2),
            20.0,
            800.0,
            0.0
        )

        dish2 = Dish(
            "https://super-plato2.png",
            "Papas Fritas",
            "Papas fritas clásicas, crujientes por fuera y suaves por dentro",
            listOf(ingredient4, ingredient5),
            local,
            30.0,
            LocalDate.now().minusWeeks(30),
            0.0,
            300.0,
            50.0
        )

        order = Order(
            client,
            listOf(dish1, dish2),
            OrderStatus.PENDIENTE,
            local,
            null,
            PaymentMethod.EFECTIVO,
            LocalTime.of(10, 0),
            baseCoupon
        )

        deliveryConditionsList = mutableListOf()
        val mixedConditions = AnyCondition(deliveryConditionsList)

        delivery = Delivery(
            "PedroDelivery",
            "Gutierrez",
            "pedrolivery",
            "pass1234",
            LocalDate.now().minusYears(30),
            Address(
                "Calle Entrega",
                "789",
                Point(2, 2)
            ),
            Polygon(
                mutableListOf(
                    Point(-2, -2),
                    Point(2, -2),
                    Point(2, 2),
                    Point(-2, 2)
                )
            ),
            mixedConditions
        )
    }

    describe("Gestión de Delivery") {
        it("Debería agregar una condición de entrega correctamente") {
            val minAmountCondition = MinimumAmountCondition(200.0)
            deliveryConditionsList.add(minAmountCondition)
            deliveryConditionsList.shouldContain(minAmountCondition)
        }

        it("Debería aceptar un pedido preparado si cumple todas las condiciones") {
            deliveryConditionsList.add(MinimumAmountCondition(100.0))
            order.status = OrderStatus.PREPARADO
            delivery.takeOrder(order) shouldBe true
        }

        it("Debería lanzar una excepción si el pedido no está preparado") {
            deliveryConditionsList.add(MinimumAmountCondition(50.0))
            order.status = OrderStatus.PENDIENTE
            shouldThrow<OrderStateException> {
                delivery.takeOrder(order)
            }
        }

        it("Debería lanzar una excepción si el pedido no cumple las condiciones") {
            deliveryConditionsList.add(MinimumAmountCondition(99999.0))
            order.status = OrderStatus.PREPARADO
            shouldThrow<InvalidOrderException> {
                delivery.takeOrder(order)
            }
        }

        it("Debería marcar un pedido como entregado correctamente") {
            deliveryConditionsList.add(MinimumAmountCondition(100.0))
            order.status = OrderStatus.PREPARADO
            delivery.takeOrder(order)
            delivery.deliverOrder(order)
            order.status shouldBe OrderStatus.ENTREGADO
        }
    }

    describe("Funcionalidad de Pedido") {
        it("Debería calcular el costo total correctamente") {
            val totalOrder = order.getOrderTotal()

            val dish1DiscountRate = (1.0 - (30.0 - 14.0) / 100.0)
            val dish1Cost = (800.0 + 800.0 * 0.2 + 800.0 * 0.2 + (120.0 + 80.0 + 100.0))
            val dish2Cost = (300.0 + 300.0 * 0.3 + (60.0 + 70.0))
            val dish1WithDiscount = dish1Cost * dish1DiscountRate
            val dish2WithDiscount = dish2Cost * 0.5
            val dishCostWithoutDiscount = dish1Cost + dish2Cost
            val dishCost = dish1WithDiscount + dish2WithDiscount
            val deliveryCost = dishCostWithoutDiscount * 0.1
            val expectedTotal = dishCost + deliveryCost

            totalOrder shouldBe expectedTotal
        }

        it("Debería aplicar un recargo por pago con tarjeta") {
            order.paymentMethod = PaymentMethod.CREDITO

            val totalOrder = order.getOrderTotal()

            val dish1DiscountRate = (1.0 - (30.0 - 14.0) / 100.0)
            val dish1Cost = (800.0 + 800.0 * 0.2 + 800.0 * 0.2 + (120.0 + 80.0 + 100.0))
            val dish2Cost = (300.0 + 300.0 * 0.3 + (60.0 + 70.0))
            val dish1WithDiscount = dish1Cost * dish1DiscountRate
            val dish2WithDiscount = dish2Cost * 0.5
            val dishCostWithoutDiscount = dish1Cost + dish2Cost
            val dishCost = dish1WithDiscount + dish2WithDiscount
            val deliveryCost = dishCostWithoutDiscount * 0.1
            val expectedTotal = (dishCost + deliveryCost) * 1.05

            totalOrder shouldBe expectedTotal
        }

        it("Debería permitir marcar un pedido como preparado") {
            order.markAsPrepared()
            order.status shouldBe OrderStatus.PREPARADO
        }

        it("Debería solo permitir marcar un pedido como entregado si está preparado") {
            shouldThrow<OrderStateException> {
                order.markAsDelivered()
            }

            order.status = OrderStatus.PREPARADO
            order.markAsDelivered()
            order.status shouldBe OrderStatus.ENTREGADO
        }

        it("Debería no permitir marcar un pedido como entregado si no está preparado") {
            order.status = OrderStatus.PENDIENTE
            shouldThrow<OrderStateException> {
                order.markAsDelivered()
            }
            order.status shouldBe OrderStatus.PENDIENTE
        }
    }

    describe("Condiciones delivery") {
        it("Un delivery con condicion Rango horario solo acepta pedidos de dentro de su horario") {
            deliveryConditionsList.add(TimeRangeCondition(LocalTime.of(9, 0), LocalTime.of(17, 0)))
            order.markAsPrepared()
            order.pickupTime = LocalTime.of(14, 0)
            delivery.takeOrder(order)
        }

        it("Un delivery con condicion Rango horario NO acepta pedidos fuera de su horario") {
            deliveryConditionsList.add(TimeRangeCondition(LocalTime.of(9, 0), LocalTime.of(17, 0)))
            order.markAsPrepared()
            order.pickupTime = LocalTime.of(23, 0)
            shouldThrow<InvalidOrderException> {
                delivery.takeOrder(order)
            }
        }

        it("Un delivery con condicion Monto Minimo solo acepta pedidos si es dentro de un monto minimo") {
            deliveryConditionsList.add(MinimumAmountCondition(100.0))
            order.markAsPrepared()
            delivery.takeOrder(order)
        }

        it("Un delivery con condicion Monto Minimo NO acepta pedidos si no alcanza el monto minimo") {
            deliveryConditionsList.add(MinimumAmountCondition(1000000000.0))
            order.markAsPrepared()
            shouldThrow<InvalidOrderException> {
                delivery.takeOrder(order)
            }
        }

        it("Un delivery con condicion Local Especifico solo acepta pedidos que sean de un local") {
            deliveryConditionsList.add(SpecificLocalCondition(mutableListOf(local)))
            order.markAsPrepared()
            delivery.takeOrder(order)
        }

        it("Un delivery con condicion Local Especifico NO acepta pedidos si no son de ese local") {
            deliveryConditionsList.add(SpecificLocalCondition(mutableListOf(local2)))
            order.markAsPrepared()
            shouldThrow<InvalidOrderException> {
                delivery.takeOrder(order)
            }
        }

        it("Un delivery con condicion Pedido Certificado solo acepta pedidos certificados") {
            deliveryConditionsList.add(CertifiedOrderCondition())
            order.markAsPrepared()
            delivery.takeOrder(order)
        }

        it("Un delivery con condicion Pedido Certificado NO acepta pedidos no certificados") {
            deliveryConditionsList.add(CertifiedOrderCondition())
            order.local = local2
            order.markAsPrepared()
            shouldThrow<InvalidOrderException> {
                delivery.takeOrder(order)
            }
        }

        it("Condicion any: la condicion debe cumplir con alguna de las condiciones de su lista") {
            deliveryConditionsList.addAll(
                listOf(
                    CertifiedOrderCondition(),
                    SpecificLocalCondition(mutableListOf(local))
                )
            )
            order.markAsPrepared()
            delivery.takeOrder(order)
        }

        it("Condicion any: la condicion debe cumplir con alguna de las condiciones de su lista (parte 2)") {
            deliveryConditionsList.clear()
            deliveryConditionsList.addAll(
                listOf(
                    CertifiedOrderCondition(),
                    SpecificLocalCondition(mutableListOf(local2))
                )
            )
            order.local = local2
            order.markAsPrepared()
            delivery.takeOrder(order)
        }
    }
})