import ar.edu.unsam.algo3.domain.*
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.uqbar.geodds.Point
import java.time.LocalDate

class ObserverSpec : DescribeSpec({
    describe("Pruebas de Observadores") {
        lateinit var local: Local

        beforeEach {
            local = Local(
                "Local de Prueba",
                "https://super-local.png",
                Address("Calle Test", "123", Point(0, 0)),
                0.0,
                0.0,
                mutableSetOf(PaymentMethod.EFECTIVO),
                mutableSetOf(),
                mutableSetOf(),
                Inbox()
            )

            LocalAuditorObject.localMap = emptyMap()
            GlobalRepository.elements.clear()
            GlobalRepository.nextId = 1
            GlobalMailSender.log.clear()
        }

        describe("Pruebas de Auditores") {
            it("El auditor de monto debe marcar como completado cuando se supera el monto mínimo") {
                val amountAuditor = ReachAmount(1000.0)
                LocalAuditorObject.localMap = mapOf(local to amountAuditor)

                val order = createOrderWithAmount(1500.0, local)
                order.observers.forEach { it.notify(order) }

                LocalAuditorObject.meetsGoal(local) shouldBe true
            }

            it("El auditor de ventas debe marcar como completado cuando hay 5 pedidos con más de 3 platos") {
                val salesAuditor = ReachSales()
                LocalAuditorObject.localMap = mapOf(local to salesAuditor)

                repeat(5) {
                    val order = createOrderWithDishes(4, local)
                    order.observers.forEach { it.notify(order) }
                }

                LocalAuditorObject.meetsGoal(local) shouldBe true
            }

            it("El auditor de meta vegana debe marcar como completado cuando se alcanza la meta") {
                val goalAuditor = ReachGoal(3)
                LocalAuditorObject.localMap = mapOf(local to goalAuditor)

                repeat(3) {
                    val veganOrder = createVeganOrder(local, createClient())
                    veganOrder.observers.forEach { it.notify(veganOrder) }
                }

                LocalAuditorObject.meetsGoal(local) shouldBe true
            }

            it("El auditor combinado debe marcar como completado cuando se cumplen todos los objetivos") {
                val combinedAuditor = Combined(listOf(
                    ReachAmount(1000.0),
                    ReachSales(),
                    ReachGoal(3)
                ))
                LocalAuditorObject.localMap = mapOf(local to combinedAuditor)

                val highAmountOrder = createOrderWithAmount(1200.0, local)
                highAmountOrder.observers.forEach { it.notify(highAmountOrder) }

                repeat(5) {
                    val orderWithDishes = createOrderWithDishes(4, local)
                    orderWithDishes.observers.forEach { it.notify(orderWithDishes) }
                }

                repeat(3) {
                    val veganOrder = createVeganOrder(local, createClient())
                    veganOrder.observers.forEach { it.notify(veganOrder) }
                }

                combinedAuditor.meetsGoal(local) shouldBe true
            }

            it("El auditor combinado debe marcar como no completado cuando falta algún objetivo") {
                val combinedAuditor = Combined(listOf(
                    ReachAmount(1000.0),
                    ReachSales(),
                    ReachGoal(100)
                ))
                LocalAuditorObject.localMap = mapOf(local to combinedAuditor)

                val highAmountOrder = createOrderWithAmount(1200.0, local)
                highAmountOrder.observers.forEach { it.notify(highAmountOrder) }

                repeat(5) {
                    val orderWithDishes = createOrderWithDishes(4, local)
                    orderWithDishes.observers.forEach { it.notify(orderWithDishes) }
                }

                val veganOrder = createVeganOrder(local, createClient())
                veganOrder.observers.forEach { it.notify(veganOrder) }

                combinedAuditor.meetsGoal(local) shouldBe false
            }
        }

        describe("Pruebas de Pedidos") {
            it("Debe actualizar preferencias cuando el pedido es 100% vegano") {
                val client = createClient()
                val veganOrder = createVeganOrder(local, client)
                veganOrder.observers.forEach { it.notify(veganOrder) }

                (client.clientType is ClientAllTypes) shouldBe true
                (client.clientType as ClientAllTypes).types.any { it is VeganClient } shouldBe true
            }

            it("Debe enviar mensaje al buzón del local cuando el pedido está certificado") {
                val client = createLongTermClient()
                val certifiedOrder = createCertifiedOrder(local, client)
                certifiedOrder.observers.forEach { it.notify(certifiedOrder) }

                local.inbox.getMessages().size shouldBe 1
                local.inbox.getMessages().first().subject shouldBe "¡Pedido Certificado! - Cliente Premium"
            }

            it("No debe enviar mensaje al buzón si el pedido no está certificado") {
                val client = createClient()
                val nonCertifiedOrder = createOrderWithAmount(100.0, local)
                nonCertifiedOrder.observers.forEach { it.notify(nonCertifiedOrder) }

                local.inbox.getMessages().size shouldBe 0
            }

            it("Debe enviar por correo publicidad de platos acordes a las preferencias del cliente") {
                val client = createClient()
                val ingredienteNoDeseado = Ingredient("Carne", 10.0, FoodGroup.PROTEINAS,1)
                client.ingredientsToAvoid.add(ingredienteNoDeseado)

                val
                    suitableDish = Dish(
                    "https://suitable-dish.png",
                    "Plato Apto",
                    "Plato para prueba",
                    listOf(),
                    local,
                    0.0,
                    LocalDate.now(),
                    0.0,
                    100.0,
                    0.0
                )
                val unsuitableDish = Dish(
                    "https://unsuitable-dish.png",
                    "Plato No Apto",
                    "Tiene ingrediente a evitar",
                    listOf(ingredienteNoDeseado),
                    local,
                    0.0,
                    LocalDate.now(),
                    0.0,
                    100.0,
                    0.0
                )

                GlobalRepository.create(suitableDish)
                GlobalRepository.create(unsuitableDish)

                val order = Order(
                    client,
                    listOf(suitableDish),
                    OrderStatus.PENDIENTE,
                    local,
                    null,
                    PaymentMethod.EFECTIVO
                )
                val emailObserver = SendDishEmails(GlobalMailSender, GlobalRepository)
                order.observers.add(emailObserver)

                order.observers.forEach { it.notify(order) }
                GlobalMailSender.log.size shouldBe 1

                val emailEnviado = GlobalMailSender.log.first()
                print(emailEnviado)
                emailEnviado shouldContain "Recomendaciones en ${local.name}"
                emailEnviado shouldContain suitableDish.name
                emailEnviado shouldNotContain unsuitableDish.name
            }
        }
    }
})

// Funciones auxiliares
private fun createOrderWithAmount(amount: Double, local: Local): Order {
    val dish = Dish(
        "https://super-plato.png",
        "Plato de Prueba",
        "Plato para prueba",
        listOf(),
        local,
        0.0,
        LocalDate.now(),
        0.0,
        amount,
        0.0
    )

    return Order(
        createClient(),
        listOf(dish),
        OrderStatus.PENDIENTE,
        local,
        null,
        PaymentMethod.EFECTIVO
    )
}

private fun createOrderWithDishes(dishCount: Int, local: Local): Order {
    val dishes = (1..dishCount).map {
        Dish(
            "https://super-plato.png",
            "Plato $it",
            "Plato para prueba",
            listOf(),
            local,
            0.0,
            LocalDate.now(),
            0.0,
            100.0,
            0.0,
        )
    }

    return Order(
        createClient(),
        dishes,
        OrderStatus.PENDIENTE,
        local,
        null,
        PaymentMethod.EFECTIVO
    )
}

private fun createClient(): Client {
    return Client(
        "Usuario",
        "Prueba",
        "usuario_prueba",
        "clave123",
        LocalDate.now().minusYears(20),

        "usuario@prueba.com",
        Address(
            "Calle Prueba",
            "123",
            Point(0, 0)
        ),
        "a",
        mutableSetOf(),
        mutableSetOf(),
        10.0,
        NormalClient(),
    )
}

private fun createVeganOrder(local: Local, client: Client = createClient()): Order {
    val ingredienteVegano = Ingredient(
        "Lechuga",
        50.0,
        FoodGroup.FRUTAS_Y_VERDURAS,
        2
    )

    val platoVegano = Dish(
        "https://super-vegano.png",
        "Ensalada Vegana",
        "Plato 100% vegano",
        listOf(ingredienteVegano),
        local,
        0.0,
        LocalDate.now(),
        0.0,
        200.0,
        0.0
    )

    return Order(
        client,
        listOf(platoVegano),
        OrderStatus.PENDIENTE,
        local,
        null,
        PaymentMethod.EFECTIVO
    )
}

private fun createLongTermClient(): Client {
    return Client(
        "Cliente",
        "Antiguo",
        "cliente_antiguo",
        "clave123",
        LocalDate.now().minusYears(30),
        "cliente.antiguo@prueba.com",
        Address(
            "Calle Cliente",
            "123",
            Point(0, 0)
        ),
        "a",
        mutableSetOf(),
        mutableSetOf(),
        10.0,
        NormalClient(),
    )
}

private fun createCertifiedOrder(local: Local, client: Client): Order {
    val valoracionBuena = Rating(client, local, 4.5)
    local.ratings.add(valoracionBuena)

    return Order(
        client,
        listOf(
            Dish(
                "https://super-premium.png",
                "Plato Premium",
                "Plato especial certificado",
                listOf(),
                local,
                0.0,
                LocalDate.now(),
                0.0,
                500.0,
                0.0,
            )
        ),
        OrderStatus.PENDIENTE,
        local,
        null,
        PaymentMethod.EFECTIVO
    )
}