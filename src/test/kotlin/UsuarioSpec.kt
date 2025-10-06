import ar.edu.unsam.algo2.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.uqbar.geodds.Point
import java.time.LocalDate

class UsuarioSpec {
    private lateinit var usuario: Usuario
    private lateinit var ingrediente1: Ingrediente
    private lateinit var ingrediente2: Ingrediente
    private lateinit var ingrediente3: Ingrediente
    val fechadehoymenos30 = LocalDate.now().minusYears(30)
    @BeforeEach
    fun setUp() {
        ingrediente1 = Ingrediente("Tomate", 100.0, grupoAlimentos = GrupoAlimentos.FRUTAS_Y_VERDURAS)
        ingrediente2 = Ingrediente("Carne", 500.0,  grupoAlimentos = GrupoAlimentos.PROTEINAS)
        ingrediente3 = Ingrediente("Lechuga", 80.0, grupoAlimentos = GrupoAlimentos.FRUTAS_Y_VERDURAS)

        usuario = Cliente(
            nombre = "Juan",
            apellido = "Perez",
            username = "juan123",
            password = "seguro123",
            fechaNacimiento = fechadehoymenos30,
            fechaRegistro = LocalDate.now(),
            "usuario@gmail.com",
            direccion = Direccion(
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