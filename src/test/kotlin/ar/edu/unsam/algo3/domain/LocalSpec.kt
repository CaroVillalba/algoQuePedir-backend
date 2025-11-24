
package ar.edu.unsam.algo3.domain

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.uqbar.geodds.Point
import java.time.LocalDate

class LocalSpec : DescribeSpec({
    lateinit var reliableLocal: Local
    lateinit var unreliableLocal: Local

    describe("Local Tests") {
        beforeEach {
            val client = Client(
                "Juan",
                "Pérez",
                "juanperez",
                "1234",
                LocalDate.of(1990, 1, 1),

                "cliente@gmail.com",
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

            reliableLocal = Local(
                "Restaurante Top",
                "https://reliable-local.png",
                Address(
                    "Av. Principal",
                    "123",
                    Point(-3, -3)
                ),
                0.0,
                0.0,
                mutableSetOf(PaymentMethod.EFECTIVO, PaymentMethod.CREDITO),
                mutableSetOf()
            )

            reliableLocal.ratings = mutableSetOf(
                Rating(
                    client,
                    reliableLocal,
                    4.5
                ),
                Rating(
                    client,
                    reliableLocal,
                    5.0
                ),
                Rating(
                    client,
                    reliableLocal,
                    4.8
                )
            )

            unreliableLocal = Local(
                "Comida Dudosa",
                "https://unreliable-local.png",
                Address(
                    "Calle Oscura",
                    "666",
                    Point(6, 6)
                ),
                0.0,
                0.0,
                mutableSetOf(PaymentMethod.EFECTIVO),
                mutableSetOf()
            )

            unreliableLocal.ratings = mutableSetOf(
                Rating(
                    client,
                    unreliableLocal,
                    2.0
                ),
                Rating(
                    client,
                    unreliableLocal,
                    3.5
                ),
                Rating(
                    client,
                    unreliableLocal,
                    3.0
                )
            )
        }

        it("Un local con puntuación mayor o igual a 4.0 es confiable") {
            reliableLocal.isReliable() shouldBe true
        }

        it("Un local con puntuación menor a 4.0 no es confiable") {
            unreliableLocal.isReliable() shouldBe false
        }

        it("Calcula correctamente la puntuación promedio del local") {
            reliableLocal.getScore() shouldBe 4.766666666666667
            unreliableLocal.getScore() shouldBe 2.8333333333333335
        }
    }
})