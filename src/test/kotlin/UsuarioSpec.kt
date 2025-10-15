import ar.edu.unsam.algo3.domain.Cliente
import ar.edu.unsam.algo3.domain.ClienteNormal
import ar.edu.unsam.algo3.domain.Address
import ar.edu.unsam.algo3.domain.GrupoAlimentos
import ar.edu.unsam.algo3.domain.Ingredient
import ar.edu.unsam.algo3.domain.Usuario
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.uqbar.geodds.Point
import java.time.LocalDate

class UsuarioSpec {
    private lateinit var usuario: Usuario
    private lateinit var ingredient1: Ingredient
    private lateinit var ingredient2: Ingredient
    private lateinit var ingredient3: Ingredient
    val fechadehoymenos30 = LocalDate.now().minusYears(30)
    @BeforeEach
    fun setUp() {
        ingredient1 = Ingredient("Tomate", 100.0, group = GrupoAlimentos.FRUTAS_Y_VERDURAS)
        ingredient2 = Ingredient("Carne", 500.0, group = GrupoAlimentos.PROTEINAS)
        ingredient3 = Ingredient("Lechuga", 80.0, group = GrupoAlimentos.FRUTAS_Y_VERDURAS)

        usuario = Cliente(
            nombre = "Juan",
            apellido = "Perez",
            username = "juan123",
            password = "seguro123",
            fechaNacimiento = fechadehoymenos30,
            fechaRegistro = LocalDate.now(),
            "usuario@gmail.com",
            direccion = Address(
                calle = "Calle random en kirguistan",
                coordenadas = Point(40.92963, 73.00132)
            ),
            tipoDeCliente = ClienteNormal(),
            distanciaCercana = 5.0
        )
    }

    @Test
    fun `verificar que la edad se calcula correctamente`() {
        assertEquals(30, usuario.edad, "edad calculada correctamente")
    }
}