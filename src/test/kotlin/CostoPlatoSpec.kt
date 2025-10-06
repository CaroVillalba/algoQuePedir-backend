import ar.edu.unsam.algo2.*
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import org.uqbar.geodds.Point
import java.time.LocalDate

class CostoPlatoSpec : DescribeSpec({
    describe("Cálculo de costos y descuentos de Plato") {
        lateinit var localTest: Local
        lateinit var platoVacio: Plato
        lateinit var sangucheQuesoBase: Plato

        beforeEach {
            localTest = Local(
                nombre = "Local de Prueba",
                direccion = Direccion(
                    calle = "Ayuda",
                    altura = "123",
                    coordenadas = Point(0, 1)
                ),
                metodosDePago = mutableSetOf(MedioDePago.EFECTIVO, MedioDePago.CREDITO, MedioDePago.DEBITO),
                valoraciones = mutableSetOf()
            )

            platoVacio = Plato(
                nombre = "Plato Vacio",
                descripcion = "Literalmente un plato playo vacio",
                ingredientes = arrayListOf(),
                local = localTest,
                lanzamiento = LocalDate.now(),
                regaliasAutor = 0.0,
                valorBase = 0.0,
                porcentajeAcordadoConLocal = 0.0,
                descuento = 1.0
            )

            sangucheQuesoBase = Plato(
                nombre = "Sanguche de Queso Base",
                descripcion = "Configuración base para tests",
                ingredientes = arrayListOf(
                    Ingrediente(
                        nombre = "Pan",
                        costo = 5.0,
                        grupoAlimentos = GrupoAlimentos.CEREALES_Y_TUBERCULOS
                    ),
                    Ingrediente(
                        nombre = "Pan",
                        costo = 5.0,
                        grupoAlimentos = GrupoAlimentos.CEREALES_Y_TUBERCULOS
                    ),
                    Ingrediente(
                        nombre = "Queso",
                        costo = 30.0,
                        grupoAlimentos = GrupoAlimentos.LACTEOS
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
            platoVacio.costoDeIngredientes() shouldBe 0.0
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
        ) = Plato(
            nombre = "Sanguche de Queso",
            descripcion = "Dos panes un queso! Uma delicia",
            ingredientes = arrayListOf(
                Ingrediente(
                    nombre = "Pan",
                    costo = 5.0,
                    grupoAlimentos = GrupoAlimentos.CEREALES_Y_TUBERCULOS
                ),
                Ingrediente(
                    nombre = "Pan",
                    costo = 5.0,
                    grupoAlimentos = GrupoAlimentos.CEREALES_Y_TUBERCULOS
                ),
                Ingrediente(
                    nombre = "Queso",
                    costo = 30.0,
                    grupoAlimentos = GrupoAlimentos.LACTEOS
                )
            ),
            local = Local(
                nombre = "Local Temporal",
                direccion = Direccion(
                    calle = "Temp",
                    altura = "1",
                    coordenadas = Point(0, 0)
                ),
                metodosDePago = mutableSetOf(),
                valoraciones = mutableSetOf()
            ),
            lanzamiento = lanzamiento,
            regaliasAutor = 0.0,
            valorBase = 300.0,
            descuento = descuento,
            porcentajeAcordadoConLocal = 20.0
        )
    }
}