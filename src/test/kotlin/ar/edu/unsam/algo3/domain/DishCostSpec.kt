
package ar.edu.unsam.algo3.domain

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.uqbar.geodds.Point
import java.time.LocalDate

class DishCostSpec : DescribeSpec({
    describe("Cálculo de costos y descuentos de Plato") {
        lateinit var testLocal: Local
        lateinit var emptyDish: Dish
        lateinit var baseCheeseSandwich: Dish

        beforeEach {
            testLocal = Local(
                "Local de Prueba",
                "https://super-local.png",
                Address(
                    "Ayuda",
                    "123",
                    Point(0, 1)
                ),
                0.0,
                0.0,
                mutableSetOf(PaymentMethod.EFECTIVO, PaymentMethod.CREDITO, PaymentMethod.DEBITO),
                mutableSetOf()
            )

            emptyDish = Dish(
                "https://super-plato-vacio.png",
                "Plato Vacio",
                "Literalmente un plato playo vacio",
                arrayListOf(),
                testLocal,
                0.0,
                LocalDate.now(),
                0.0,
                0.0,
                1.0
            )

            baseCheeseSandwich = Dish(
                "https://super-plato-sanguche.png",
                "Sanguche de Queso Base",
                "Configuración base para tests",
                arrayListOf(
                    Ingredient(
                        "Pan",
                        5.0,
                        FoodGroup.CEREALES_Y_TUBERCULOS
                    ),
                    Ingredient(
                        "Pan",
                        5.0,
                        FoodGroup.CEREALES_Y_TUBERCULOS
                    ),
                    Ingredient(
                        "Queso",
                        30.0,
                        FoodGroup.LACTEOS
                    )
                ),
                testLocal,
                20.0,
                LocalDate.now().minusWeeks(30),
                0.0,
                300.0,
                25.0,
            )
        }

        it("Plato sin ingredientes debe tener costo 0") {
            emptyDish.ingredientCost() shouldBe 0.0
        }

        it("Costo de ingredientes debe sumar correctamente") {
            baseCheeseSandwich.ingredientCost() shouldBe 40.0
        }

        it("Debe aplicar descuento por antigüedad mayor a 20 días") {
            val expectedValue = (300.0 + 300.0 * 0.2 + 40.0) * 0.75
            baseCheeseSandwich.salePrice() shouldBe expectedValue
        }

        it("Debe aplicar descuento especial si es lanzamiento reciente") {
            val newSandwich = createCheeseSandwich(
                LocalDate.now(),
                0.0
            )

            val expectedValue = (300.0 + 300.0 * 0.2 + 40.0) * 0.70
            newSandwich.salePrice() shouldBe expectedValue
        }

        describe("Casos borde de días desde lanzamiento") {
            it("19 días - debe aplicar descuento especial") {
                val borderDish = createCheeseSandwich(
                    LocalDate.now().minusDays(19),
                    0.0
                )

                borderDish.salePrice() shouldBe 356.0
            }

            it("20 días - debe aplicar descuento estándar") {
                val borderDish = createCheeseSandwich(
                    LocalDate.now().minusDays(20),
                    0.0
                )

                borderDish.salePrice() shouldBe 360.0
            }

            it("21 días - debe aplicar descuento estándar") {
                val borderDish = createCheeseSandwich(
                    LocalDate.now().minusDays(21),
                    0.0
                )

                borderDish.salePrice() shouldBe 360.0
            }
        }
    }
}) {
    companion object {
        fun createCheeseSandwich(
            releaseDate: LocalDate,
            discount: Double
        ) = Dish(
            "https://super-plato.png",
            "Sanguche de Queso",
            "Dos panes un queso! Uma delicia",
            arrayListOf(
                Ingredient(
                    "Pan",
                    5.0,
                    FoodGroup.CEREALES_Y_TUBERCULOS
                ),
                Ingredient(
                    "Pan",
                    5.0,
                    FoodGroup.CEREALES_Y_TUBERCULOS
                ),
                Ingredient(
                    "Queso",
                    30.0,
                    FoodGroup.LACTEOS
                )
            ),
            Local(
                "Local Temporal",
                "https://super-local.png",
                Address(
                     "Temp",
                    "1",
                    Point(0, 0)
                ),
                0.0,
                0.0,
                mutableSetOf(),
                mutableSetOf()
            ),
            20.0,
            releaseDate,
            0.0,
            300.0,
            discount,
        )
    }
}