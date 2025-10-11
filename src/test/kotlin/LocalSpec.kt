import org.uqbar.geodds.*
import ar.edu.unsam.algo3.domain.Local
import ar.edu.unsam.algo3.domain.Cliente
import ar.edu.unsam.algo3.domain.ClienteNormal
import ar.edu.unsam.algo3.domain.Direccion
import ar.edu.unsam.algo3.domain.MedioDePago
import ar.edu.unsam.algo3.domain.Valoracion
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class LocalSpec : DescribeSpec( {
    lateinit var localConfiable: Local
    lateinit var localNoConfiable: Local

    describe("test Local") {
        beforeEach {
            var cliente = Cliente(
                nombre = "Juan",
                apellido = "Pérez",
                username = "juanperez",
                password = "1234",
                fechaNacimiento = LocalDate.of(1990, 1, 1),
                fechaRegistro = LocalDate.of(2020, 1, 1),
                "cliente@gmail.com",
                direccion = Direccion(
                    calle = "Calle Falsa",
                    altura = "123",
                    coordenadas = Point(2, 2)
                ),
                ingredientesFavoritos = mutableSetOf(),
                ingredientesAEvitar = mutableSetOf(),
                distanciaCercana = 10.0,
                tipoDeCliente = ClienteNormal(),
            )

            localConfiable = Local(
                nombre = "Restaurante Top",
                direccion = Direccion(
                    calle = "Av. Principal",
                    altura = "123",
                    coordenadas = Point(-3, -3)
                ),
                metodosDePago = mutableSetOf(MedioDePago.EFECTIVO, MedioDePago.CREDITO),
                valoraciones = mutableSetOf()
            )

            localConfiable.valoraciones = mutableSetOf(
                Valoracion(
                    cliente = cliente,
                    local = localConfiable,
                    puntuacion = 4.5
                ),
                Valoracion(
                    cliente = cliente,
                    local = localConfiable,
                    puntuacion = 5.0
                ),
                Valoracion(
                    cliente = cliente,
                    local = localConfiable,
                    puntuacion = 4.8
                )
            )

            localNoConfiable = Local(
                nombre = "Comida Dudosa",
                direccion = Direccion(
                    calle = "Calle Oscura",
                    altura = "666",
                    coordenadas = Point(6, 6)
                ),
                metodosDePago = mutableSetOf(MedioDePago.EFECTIVO),
                valoraciones = mutableSetOf()
            )

            localNoConfiable.valoraciones = mutableSetOf(
                Valoracion(
                    cliente = cliente,
                    local = localNoConfiable,
                    puntuacion = 2.0
                ),
                Valoracion(
                    cliente = cliente,
                    local = localNoConfiable,
                    puntuacion = 3.5
                ),
                Valoracion(
                    cliente = cliente,
                    local = localNoConfiable,
                    puntuacion = 3.0
                )
            )
        }

        it("Un local con puntuación mayor o igual a 4.0 es confiable") {
            localConfiable.getEsConfiable() shouldBe true
        }

        it("Un local con puntuación menor a 4.0 no es confiable") {
            localNoConfiable.getEsConfiable() shouldBe false
        }

        it("Calcula correctamente la puntuación promedio del local") {
            localConfiable.getPuntuacion() shouldBe 4.766666666666667
            localNoConfiable.getPuntuacion() shouldBe 2.8333333333333335
        }
    }
})