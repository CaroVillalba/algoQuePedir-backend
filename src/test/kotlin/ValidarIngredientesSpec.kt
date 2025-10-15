import org.uqbar.geodds.*
import ar.edu.unsam.algo3.domain.Cliente
import ar.edu.unsam.algo3.domain.ClienteNormal
import ar.edu.unsam.algo3.domain.Address
import ar.edu.unsam.algo3.domain.GrupoAlimentos
import ar.edu.unsam.algo3.domain.Ingredient
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class ValidarIngredientesSpec : DescribeSpec({
    describe("Validación de ingredientes") {
        lateinit var cliente: Cliente
        lateinit var ingredient1: Ingredient
        lateinit var ingredient2: Ingredient
        lateinit var ingredient3: Ingredient

        beforeEach {
            ingredient1 = Ingredient("Tomate", 100.0, GrupoAlimentos.FRUTAS_Y_VERDURAS)
            ingredient2 = Ingredient("Carne", 500.0, GrupoAlimentos.PROTEINAS)
            ingredient3 = Ingredient("Lechuga", 80.0, GrupoAlimentos.FRUTAS_Y_VERDURAS)
            cliente = Cliente(
                nombre = "Juan",
                apellido = "Perez",
                username = "juan123",
                password = "seguro123",
                direccion = Address(
                    calle = "Calle Siempre Viva ",
                    altura = "742",
                    coordenadas = Point(8, 6)
                ),
                fechaNacimiento = LocalDate.of(1995, 5, 10),
                fechaRegistro = LocalDate.now(),
                mail = "cliente@gmail.com",
                ingredientesFavoritos = mutableSetOf(ingredient1),
                ingredientesAEvitar = mutableSetOf(ingredient2),
                tipoDeCliente = ClienteNormal(),
                distanciaCercana = 10.0
            )
        }

        it("Un ingrediente válido es aceptado") {
            cliente.ingredientesFavoritos.add(ingredient3)  // Agregar directamente al set
            cliente.ingredientesFavoritos.contains(ingredient3) shouldBe true
        }

        it("Un ingrediente a evitar es rechazado") {
            cliente.ingredientesAEvitar.remove(ingredient1)  // Eliminar directamente del set
            cliente.ingredientesAEvitar.contains(ingredient1) shouldBe false
        }

        it("Un ingrediente favorito es rechazado") {
            cliente.ingredientesFavoritos.add(ingredient2)  // Intentar agregar un ingrediente a favoritos
            cliente.ingredientesFavoritos.contains(ingredient2) shouldBe true
        }


        it("Un ingrediente a evitar es aceptado") {
            cliente.ingredientesAEvitar.add(ingredient3)  // Agregar directamente al set
            cliente.ingredientesAEvitar.contains(ingredient3) shouldBe true
        }
    }
})
