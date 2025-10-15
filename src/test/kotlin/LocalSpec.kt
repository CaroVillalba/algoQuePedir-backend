import org.uqbar.geodds.*
import ar.edu.unsam.algo3.domain.Local
import ar.edu.unsam.algo3.domain.Cliente
import ar.edu.unsam.algo3.domain.ClienteNormal
import ar.edu.unsam.algo3.domain.Address
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
                direccion = Address(
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
                name = "Restaurante Top",
                address = Address(
                    calle = "Av. Principal",
                    altura = "123",
                    coordenadas = Point(-3, -3)
                ),
                paymentMethods = mutableSetOf(MedioDePago.EFECTIVO, MedioDePago.CREDITO),
                ratings = mutableSetOf()
            )

            localConfiable.ratings = mutableSetOf(
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
                name = "Comida Dudosa",
                address = Address(
                    calle = "Calle Oscura",
                    altura = "666",
                    coordenadas = Point(6, 6)
                ),
                paymentMethods = mutableSetOf(MedioDePago.EFECTIVO),
                ratings = mutableSetOf()
            )

            localNoConfiable.ratings = mutableSetOf(
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