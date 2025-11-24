
package ar.edu.unsam.algo2

import ar.edu.unsam.algo3.domain.Address
import ar.edu.unsam.algo3.domain.AnyCondition
import ar.edu.unsam.algo3.domain.AuditMailSender
import ar.edu.unsam.algo3.domain.Client
import ar.edu.unsam.algo3.domain.ConservativeClient
import ar.edu.unsam.algo3.domain.Delivery
import ar.edu.unsam.algo3.domain.Dish
import ar.edu.unsam.algo3.domain.DishRepository
import ar.edu.unsam.algo3.domain.FoodGroup
import ar.edu.unsam.algo3.domain.ImpatientClient
import ar.edu.unsam.algo3.domain.Ingredient
import ar.edu.unsam.algo3.domain.Local
import ar.edu.unsam.algo3.domain.LoyalClient
import ar.edu.unsam.algo3.domain.MarketingClient
import ar.edu.unsam.algo3.domain.NormalClient
import ar.edu.unsam.algo3.domain.OddClient
import ar.edu.unsam.algo3.domain.Order
import ar.edu.unsam.algo3.domain.OrderStatus
import ar.edu.unsam.algo3.domain.PaymentMethod
import ar.edu.unsam.algo3.domain.PendingRating
import ar.edu.unsam.algo3.domain.PickyClient
import ar.edu.unsam.algo3.domain.SendDishEmails
import ar.edu.unsam.algo3.domain.VeganClient
import ar.edu.unsam.algo3.exceptions.RatingException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.uqbar.geodds.Point
import org.uqbar.geodds.Polygon
import java.time.LocalDate

class ClientSpec: DescribeSpec ({
    var dishRepository: DishRepository = DishRepository()
    describe("Un cliente vegano no") {
        var client = Client(
            "a",
            "a",
            "a",
            "a",
            LocalDate.now(),
            "cliente@gmail.com",
            Address(
                "Calle Siempre Viva",
                "742",
                Point(0, 0)
            ),
            "a",
            mutableSetOf(),
            mutableSetOf(),
            500.0,
            NormalClient(),
        )

        var local = Local(
            "Local de Prueba",
            "https://super-local.png",
            Address(
                "Calle Siempre Viva",
                "742",
                Point(1, 1)
            ),
            0.0,
            0.0,
            mutableSetOf(PaymentMethod.EFECTIVO),
            mutableSetOf()
        )

        var dish = Dish(
            "https://super-plato.png",
            "Plato origen animal",
            "plato con ingredientes de origen animal",
            arrayListOf(
                Ingredient(
                    "carne",
                    0.0,
                    FoodGroup.PROTEINAS,
                    1
                ),
                Ingredient(
                    "Papa",
                    0.0,
                    FoodGroup.FRUTAS_Y_VERDURAS,
                    2
                )
            ),
            local,
            0.0,
            LocalDate.now(),
            0.0,
            0.0,
            0.0
        )

        val mockMailSender = AuditMailSender()

        var order = Order.createOrder(
            client,
            listOf(dish),
            local,
            PaymentMethod.EFECTIVO
        )

        it("El cliente recibe spam") {
            val sendDishEmailObserver = order.observers.filterIsInstance<SendDishEmails>().firstOrNull()
            sendDishEmailObserver shouldNotBe null
            sendDishEmailObserver?.mailSender?.sendEmail(client.email, "Promo", "cuerpo")
        }

        var delivery = Delivery(
            "PedroDelivery",
            "Gutierrez",
            "pedrolivery",
            "pass1234",
            LocalDate.now().minusYears(30),
            Address(
                "Calle Entrega",
                "789",
                Point(2, 2)),
            Polygon(
                mutableListOf(
                    Point(-2, -2),
                    Point(2, -2),
                    Point(2, 2),
                    Point(-2, 2)
                )
            ),
            AnyCondition(mutableListOf())
        )

        it("El cliente no puede puntuar si no tiene valoraciones pendientes") {
            shouldThrow<RatingException> {
                client.rate(local, 5.0)
            }
        }

        it("cliente tiene una valoracion pendiente") {
            client.confirmOrder(order)
            client.pendingRatings.isNotEmpty() shouldBe true
        }

        it("cuando un cliente puntua un local se le elimina la valoracion pendiente") {
            client.confirmOrder(order)
            client.rate(local, 5.0)
            client.pendingRatings.isEmpty() shouldBe true
        }

        it("el cliente no puede puntuar cuando la valoracion expiro") {
            var rating = PendingRating(local, LocalDate.now().minusDays(8))
            client.pendingRatings.add(rating)
            shouldThrow<RatingException> {
                client.rate(local, 5.0)
            }
        }
    }

    describe("Tipos de cliente") {
        var meatIngredient = Ingredient(
            "Carne",
            0.0,
            FoodGroup.PROTEINAS,
            1
        )
        var potatoIngredient = Ingredient(
            "Papa",
            0.0,
            FoodGroup.FRUTAS_Y_VERDURAS,
            2
        )
        var tomatoIngredient = Ingredient(
            "tomate",
            0.0,
            FoodGroup.FRUTAS_Y_VERDURAS,
            3
        )

        var client = Client(
            "a",
            "a",
            "a",
            "a",
            LocalDate.now(),
            "cliente@gmail.com",
            Address(
                "Calle Siempre Viva",
                "742",
                Point(0.0, 0.0)
            ),
            "a",
            mutableSetOf(tomatoIngredient),
            mutableSetOf(),
            5.0,
            VeganClient(),
        )

        var local1 = Local(
            "Local de Prueba",
            "https://super-local1.png",
            Address(
                "Calle Siempre Viva",
                "742",
                Point(0.0, 0.001)
            ),
            0.0,
            0.0,
            mutableSetOf(PaymentMethod.EFECTIVO),
            mutableSetOf()
        )

        var local2 = Local(
            "Local Nuevisimo que no le va a gustar a cliente fiel",
            "https://super-local2.png",
            Address(
                "Calle Siempre Muerta",
                "7427536",
                Point(25.0, 25.0)
            ),
            0.0,
            0.0,
            mutableSetOf(PaymentMethod.EFECTIVO),
            mutableSetOf()
        )

        var dish = Dish(
            "https://super-plato.png",
            "Plato origen animal",
            "plato con ingredientes de origen animal",
            arrayListOf(
                meatIngredient,
                potatoIngredient
            ),
            local1,
            0.0,
            LocalDate.now(),
            0.0,
            0.0,
            0.0
        )

        val authorDish = Dish(
            "https://super-plato-de-autor.png",
            "Tomate de autor",
            "Un tomate que lo invento un autor. Totalmente esternocleidomastoideo",
            arrayListOf(
                tomatoIngredient
            ),
            local2,
            0.0,
            LocalDate.now(),
            1.0,
            0.0,
            0.0
        )

        var order = Order(
            client,
            listOf(dish),
            OrderStatus.PREPARADO,
            local1,
            null,
            PaymentMethod.EFECTIVO,
            null
        )

        it("Cliente vegano: Solo acepta platos sin carne etc") {
            client.isDishSuitableForClient(authorDish) shouldBe true
        }

        it("ClienteVegano: El plato no puede ser apto para veganos") {
            client.isDishSuitableForClient(dish) shouldBe false
        }

        it("ClienteExquisito: Acepta platos Solo De Autor") {
            client.clientType = PickyClient()
            client.isDishSuitableForClient(authorDish) shouldBe true
        }

        it("ClienteExquisito: No acepta un plato que no es de autor") {
            client.clientType = PickyClient()
            client.isDishSuitableForClient(dish) shouldBe false
        }

        it("ClienteFiel: Solo acepta platos de local1") {
            client.clientType = LoyalClient(mutableSetOf(local1))
            client.isDishSuitableForClient(dish) shouldBe true
        }

        it("ClienteFiel: No acepta platos de local2") {
            client.clientType = LoyalClient(mutableSetOf(local1))
            client.isDishSuitableForClient(authorDish) shouldBe false
        }

        it("ClienteConservador: Acepta solo los que tengan solo Tomate") {
            client.clientType = ConservativeClient()
            client.isDishSuitableForClient(authorDish) shouldBe true
        }

        it("ClienteConservador: No acepta los platos que tengan cosas que NO SEAN TOMATE AMO EL TOMATE DAME SOLO TOMATE") {
            client.clientType = ConservativeClient()
            client.isDishSuitableForClient(dish) shouldBe false
        }

        it("ClienteMarketing: solo admite productos que tengan la palabra esternocleidomastoideo en la descripcion") {
            client.clientType = MarketingClient(mutableSetOf("esternocleidomastoideo"))
            client.isDishSuitableForClient(authorDish) shouldBe true
        }

        it("ClienteMarketing: no acepta productos que no tengan la palabra esternocleidomastoideo en la descripcion") {
            client.clientType = MarketingClient(mutableSetOf("esternocleidomastoideo"))
            client.isDishSuitableForClient(dish) shouldBe false
        }

        it("ClienteImpaciente: solo admite platos de locales cercanos") {
            client.clientType = ImpatientClient()
            client.isDishSuitableForClient(dish) shouldBe true
        }

        it("ClienteMarketing: no admite platos de locales lejanos") {
            client.clientType = ImpatientClient()
            client.isDishSuitableForClient(authorDish) shouldBe false
        }

        it("ClienteRaro: es exquisito si tiene edad par. Acepta platos solo de autor.") {
            client.clientType = OddClient()
            client.birthDate = LocalDate.now()
            client.isDishSuitableForClient(authorDish) shouldBe true
        }

        it("ClienteRaro: es exquisito si tiene edad par. No acepta platos que no sean de autor.") {
            client.clientType = OddClient()
            client.birthDate = LocalDate.now()
            client.isDishSuitableForClient(dish) shouldBe false
        }

        it("ClienteRaro: es conservador si tiene edad impar.") {
            client.clientType = OddClient()
            client.birthDate = LocalDate.now().minusYears(1)
            client.isDishSuitableForClient(authorDish) shouldBe true
        }

        it("ClienteRaro: es conservador si tiene edad impar.") {
            client.clientType = OddClient()
            client.birthDate = LocalDate.now().minusYears(1)
            client.isDishSuitableForClient(dish) shouldBe false
        }
    }
})