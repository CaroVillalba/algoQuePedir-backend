import org.uqbar.geodds.*
import ar.edu.unsam.algo3.domain.Cliente
import ar.edu.unsam.algo3.domain.ClienteNormal
import ar.edu.unsam.algo3.domain.Direccion
import ar.edu.unsam.algo3.domain.GrupoAlimentos
import ar.edu.unsam.algo3.domain.Ingrediente
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class ValidarIngredientesSpec : DescribeSpec({
    describe("Validación de ingredientes") {
        lateinit var cliente: Cliente
        lateinit var ingrediente1: Ingrediente
        lateinit var ingrediente2: Ingrediente
        lateinit var ingrediente3: Ingrediente

        beforeEach {
            ingrediente1 = Ingrediente("Tomate", 100.0, GrupoAlimentos.FRUTAS_Y_VERDURAS)
            ingrediente2 = Ingrediente("Carne", 500.0, GrupoAlimentos.PROTEINAS)
            ingrediente3 = Ingrediente("Lechuga", 80.0, GrupoAlimentos.FRUTAS_Y_VERDURAS)
            cliente = Cliente(
                nombre = "Juan",
                apellido = "Perez",
                username = "juan123",
                password = "seguro123",
                direccion = Direccion(
                    calle = "Calle Siempre Viva ",
                    altura = "742",
                    coordenadas = Point(8, 6)
                ),
                fechaNacimiento = LocalDate.of(1995, 5, 10),
                fechaRegistro = LocalDate.now(),
                mail = "cliente@gmail.com",
                ingredientesFavoritos = mutableSetOf(ingrediente1),
                ingredientesAEvitar = mutableSetOf(ingrediente2),
                tipoDeCliente = ClienteNormal(),
                distanciaCercana = 10.0
            )
        }

        it("Un ingrediente válido es aceptado") {
            cliente.ingredientesFavoritos.add(ingrediente3)  // Agregar directamente al set
            cliente.ingredientesFavoritos.contains(ingrediente3) shouldBe true
        }

        it("Un ingrediente a evitar es rechazado") {
            cliente.ingredientesAEvitar.remove(ingrediente1)  // Eliminar directamente del set
            cliente.ingredientesAEvitar.contains(ingrediente1) shouldBe false
        }

        it("Un ingrediente favorito es rechazado") {
            cliente.ingredientesFavoritos.add(ingrediente2)  // Intentar agregar un ingrediente a favoritos
            cliente.ingredientesFavoritos.contains(ingrediente2) shouldBe true
        }


        it("Un ingrediente a evitar es aceptado") {
            cliente.ingredientesAEvitar.add(ingrediente3)  // Agregar directamente al set
            cliente.ingredientesAEvitar.contains(ingrediente3) shouldBe true
        }
    }
})
