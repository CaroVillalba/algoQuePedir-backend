import ar.edu.unsam.algo3.domain.Address
import ar.edu.unsam.algo3.domain.GrupoAlimentos
import ar.edu.unsam.algo3.domain.Ingredient
import ar.edu.unsam.algo3.domain.Local
import ar.edu.unsam.algo3.domain.MedioDePago
import ar.edu.unsam.algo3.domain.Dish
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.uqbar.geodds.Point
import java.time.LocalDate

class CostoPlatoSpec : DescribeSpec({
    describe("Cálculo de costos y descuentos de Plato") {
        lateinit var localTest: Local
        lateinit var dishVacio: Dish
        lateinit var sangucheQuesoBase: Dish

        beforeEach {
            localTest = Local(
                name = "Local de Prueba",
                address = Address(
                    calle = "Ayuda",
                    altura = "123",
                    coordenadas = Point(0, 1)
                ),
                paymentMethods = mutableSetOf(MedioDePago.EFECTIVO, MedioDePago.CREDITO, MedioDePago.DEBITO),
                ratings = mutableSetOf()
            )

            dishVacio = Dish(
                nombre = "Plato Vacio",
                descripcion = "Literalmente un plato playo vacio",
                ingredients = arrayListOf(),
                local = localTest,
                lanzamiento = LocalDate.now(),
                regaliasAutor = 0.0,
                valorBase = 0.0,
                porcentajeAcordadoConLocal = 0.0,
                descuento = 1.0
            )

            sangucheQuesoBase = Dish(
                nombre = "Sanguche de Queso Base",
                descripcion = "Configuración base para tests",
                ingredients = arrayListOf(
                    Ingredient(
                        name = "Pan",
                        price = 5.0,
                        group = GrupoAlimentos.CEREALES_Y_TUBERCULOS
                    ),
                    Ingredient(
                        name = "Pan",
                        price = 5.0,
                        group = GrupoAlimentos.CEREALES_Y_TUBERCULOS
                    ),
                    Ingredient(
                        name = "Queso",
                        price = 30.0,
                        group = GrupoAlimentos.LACTEOS
                    )
                ),
                local = localTest,
                lanzamiento = LocalDate.now().minusWeeks(30),
                regaliasAutor = 0.0,
                valorBase = 300.0,
                descuento = 25.0,
                porcentajeAcordadoConLocal = 20.0
            )
        }

        it("Plato sin ingredientes debe tener costo 0") {
            dishVacio.costoDeIngredientes() shouldBe 0.0
        }

        it("Costo de ingredientes debe sumar correctamente") {
            sangucheQuesoBase.costoDeIngredientes() shouldBe 40.0
        }

        it("Debe aplicar descuento por antigüedad mayor a 20 días") {
            val valorEsperado = (300.0 + 300.0 * 0.2 + 40.0) * 0.75
            sangucheQuesoBase.valorDeVenta() shouldBe valorEsperado
        }

        it("Debe aplicar descuento especial si es lanzamiento reciente") {
            val sangucheNuevo = crearSangucheQueso(
                lanzamiento = LocalDate.now(),
                descuento = 0.0
            )

            val valorEsperado = (300.0 + 300.0 * 0.2 + 40.0) * 0.70
            sangucheNuevo.valorDeVenta() shouldBe valorEsperado
        }

        describe("Casos borde de días desde lanzamiento") {
            it("19 días - debe aplicar descuento especial") {
                val platoBorde = crearSangucheQueso(
                    lanzamiento = LocalDate.now().minusDays(19),
                    descuento = 0.0
                )

                platoBorde.valorDeVenta() shouldBe 356.0
            }

            it("20 días - debe aplicar descuento estándar") {
                val platoBorde = crearSangucheQueso(
                    lanzamiento = LocalDate.now().minusDays(20),
                    descuento = 0.0
                )

                platoBorde.valorDeVenta() shouldBe 360.0
            }

            it("21 días - debe aplicar descuento estándar") {
                val platoBorde = crearSangucheQueso(
                    lanzamiento = LocalDate.now().minusDays(21),
                    descuento = 0.0
                )

                platoBorde.valorDeVenta() shouldBe 360.0
            }
        }
    }
}) {
    companion object {
        fun crearSangucheQueso(
            lanzamiento: LocalDate,
            descuento: Double
        ) = Dish(
            nombre = "Sanguche de Queso",
            descripcion = "Dos panes un queso! Uma delicia",
            ingredients = arrayListOf(
                Ingredient(
                    name = "Pan",
                    price = 5.0,
                    group = GrupoAlimentos.CEREALES_Y_TUBERCULOS
                ),
                Ingredient(
                    name = "Pan",
                    price = 5.0,
                    group = GrupoAlimentos.CEREALES_Y_TUBERCULOS
                ),
                Ingredient(
                    name = "Queso",
                    price = 30.0,
                    group = GrupoAlimentos.LACTEOS
                )
            ),
            local = Local(
                name = "Local Temporal",
                address = Address(
                    calle = "Temp",
                    altura = "1",
                    coordenadas = Point(0, 0)
                ),
                paymentMethods = mutableSetOf(),
                ratings = mutableSetOf()
            ),
            lanzamiento = lanzamiento,
            regaliasAutor = 0.0,
            valorBase = 300.0,
            descuento = descuento,
            porcentajeAcordadoConLocal = 20.0
        )
    }
}