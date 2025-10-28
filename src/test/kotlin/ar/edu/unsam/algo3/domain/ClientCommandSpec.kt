
package ar.edu.unsam.algo3.domain

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import org.uqbar.geodds.Point

class ClientCommandSpec : DescribeSpec({
    describe("Puntuar Locales Cliente Command") {
        lateinit var dishRepository: DishRepository
        lateinit var ingredient1: Ingredient
        lateinit var mailSender: MailSender
        lateinit var dish: Dish
        lateinit var client: Client
        lateinit var local1: Local
        lateinit var local2: Local
        lateinit var local3: Local

        beforeTest {
            mailSender = AuditMailSender()
            dishRepository = DishRepository()
            client = Client()
            local1 = Local(
                "testLocal",
                "https://super-local1.png",
                Address(
                    "testCalle",
                    "testAltura",
                    Point(0, 0)
                ),
                0.0,
                0.0,
                mutableSetOf(PaymentMethod.EFECTIVO),
                mutableSetOf()
            )
            ingredient1 = Ingredient(
                "Tomate",
                100.0,
                FoodGroup.FRUTAS_Y_VERDURAS,
                1
            )
            dish = Dish(
                "https://super-pasta.png",
                "Pasta",
                "Un plato de pasta clásica y deliciosa",
                listOf(ingredient1),
                local1,
                30.0,
                LocalDate.now(),
                0.0,
                500.0,
                0.0
            )
            client.createRating(
                PendingRating(
                    local1,
                    LocalDate.now()
                ),
                5.0
            )

            local2 = Local(
                "testLocal",
                "https://super-local2.png",
                Address(
                    "testCalle",
                    "testAltura",
                    Point(0, 0)
                ),
                0.0,
                0.0,
                mutableSetOf(PaymentMethod.EFECTIVO),
                mutableSetOf(),
            )
            client.createRating(
                PendingRating(
                    local2,
                    LocalDate.now()
                ),
                3.0
            )

            local3 = Local(
                "testLocal",
                "https://super-local2.png",
                Address(
                    "testCalle",
                    "testAltura",
                    Point(0, 0)
                ),
                0.0,
                0.0,
                mutableSetOf(PaymentMethod.EFECTIVO),
                mutableSetOf(),
            )
            client.createRating(
                PendingRating(
                    local3,
                    LocalDate.now()
                ),
                1.0
            )

            client.pendingRatings.add(PendingRating(local1))
            client.pendingRatings.add(PendingRating(local2))
            client.pendingRatings.add(PendingRating(local3))
        }

        it("cliente tiene valoraciones pendientes, tiene programado valorarlas automaticamente") {
            client.commands = mutableListOf(
                RateLocalsClientCommand(FixedScoreClientCommandStrategy(5.0))
            )

            client.executeCommands()

            client.pendingRatings.size shouldBe 0
        }

        it("cliente puntua todas las valoraciones con un valor fijo") {
            client.commands = mutableListOf(
                RateLocalsClientCommand(FixedScoreClientCommandStrategy(5.0))
            )

            client.executeCommands()

            client.pendingRatings.size shouldBe 0
            local1.getScore() shouldBe 5.0
            local2.getScore() shouldBe (5.0+3.0)/2.0
            local3.getScore() shouldBe (5.0+1.0)/2.0
        }

        it("cliente puntua todas las valoraciones con el promedio del local") {
            client.commands = mutableListOf(
                RateLocalsClientCommand(AverageScoreClientCommandStrategy())
            )

            client.executeCommands()

            client.pendingRatings.size shouldBe 0
            local1.getScore() shouldBe 5.0
            local2.getScore() shouldBe 3.0
            local3.getScore() shouldBe 1.0
        }

        it("cliente puntua todas las valoraciones con un valor aleatorio") {
            client.commands = mutableListOf(
                RateLocalsClientCommand(RandomScoreClientCommandStrategy())
            )

            client.executeCommands()

            client.pendingRatings.size shouldBe 0
            (local1.getScore() >= 0.0 && local1.getScore() <= 5.0) shouldBe true
            (local2.getScore() >= 0.0 && local2.getScore() <= 5.0) shouldBe true
            (local3.getScore() >= 0.0 && local3.getScore() <= 5.0) shouldBe true
        }

        it("cliente crea un pedido") {
            client.commands = mutableListOf(
                CreateOrderClientCommand(
                    mutableListOf(dish),
                    local1,
                    PaymentMethod.EFECTIVO,
                )
            )

            client.executeCommands()
        }
    }
})