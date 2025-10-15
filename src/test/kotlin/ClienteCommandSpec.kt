import ar.edu.unsam.algo3.domain.AuditoriaMailSender
import ar.edu.unsam.algo3.domain.Cliente
import ar.edu.unsam.algo3.domain.CrearPedidoClienteCommand
import ar.edu.unsam.algo3.domain.Address
import ar.edu.unsam.algo3.domain.GrupoAlimentos
import ar.edu.unsam.algo3.domain.Ingredient
import ar.edu.unsam.algo3.domain.Local
import ar.edu.unsam.algo3.domain.MailSender
import ar.edu.unsam.algo3.domain.MedioDePago
import ar.edu.unsam.algo3.domain.Dish
import ar.edu.unsam.algo3.domain.PlatoRepository
import ar.edu.unsam.algo3.domain.PuntuarFijoClienteCommandStrat
import ar.edu.unsam.algo3.domain.PuntuarLocalesClienteCommand
import ar.edu.unsam.algo3.domain.PuntuarPromedioClienteCommandStrat
import ar.edu.unsam.algo3.domain.PuntuarRandomClienteCommandStrat
import ar.edu.unsam.algo3.domain.ValoracionPendiente
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate
import org.uqbar.geodds.Point

class ClienteCommandSpec  : DescribeSpec ({
    describe("Puntuar Locales Cliente Command") {
        lateinit var platorepository: PlatoRepository
        lateinit var ingredient1: Ingredient
        lateinit var mailsender: MailSender
        lateinit var dish: Dish
        lateinit var cliente: Cliente
        lateinit var local1: Local
        lateinit var local2: Local
        lateinit var local3: Local
        beforeTest {
            mailsender = AuditoriaMailSender()
            platorepository = PlatoRepository()
            cliente = Cliente()
            local1 = Local(
                "testLocal",
                Address(
                    "testCalle",
                    "testAltura",
                    Point(0, 0)
                ),
                mutableSetOf(MedioDePago.EFECTIVO),
                mutableSetOf(),
            )
            ingredient1 =
                Ingredient(name = "Tomate", price = 100.0, group = GrupoAlimentos.FRUTAS_Y_VERDURAS)
            dish = Dish(
                nombre = "Pasta",
                descripcion = "Un plato de pasta clásica y deliciosa",
                valorBase = 500.0,
                ingredients = listOf(ingredient1),
                local = local1,
                lanzamiento = LocalDate.now(),
                regaliasAutor = 0.0,
                descuento = 0.0,
                porcentajeAcordadoConLocal = 30.0
            )
            cliente.crearValoracion(
                ValoracionPendiente(
                    local1,
                    LocalDate.now()
                ),
                5.0
            )

             local2= Local(
                 "testLocal",
                 Address(
                     "testCalle",
                     "testAltura",
                     Point(0, 0)
                 ),
                 mutableSetOf(MedioDePago.EFECTIVO),
                 ratings = mutableSetOf(),
             )
            cliente.crearValoracion(
                ValoracionPendiente(
                    local2,
                    LocalDate.now()
                ),
                3.0
            )

             local3 = Local(
                 "testLocal",
                 Address(
                     "testCalle",
                     "testAltura",
                     Point(0, 0)
                 ),
                 mutableSetOf(MedioDePago.EFECTIVO),
                 ratings = mutableSetOf(),
             )
            cliente.crearValoracion(
                ValoracionPendiente(
                    local3,
                    LocalDate.now()
                ),
                1.0
            )

            cliente.valoracionesPendientes.add(ValoracionPendiente(local1))
            cliente.valoracionesPendientes.add(ValoracionPendiente(local2))
            cliente.valoracionesPendientes.add(ValoracionPendiente(local3))
        }

        it ("cliente tiene valoraciones pendientes, tiene programado valorarlas automaticamente"){
            cliente.comandos = mutableListOf(
                PuntuarLocalesClienteCommand(PuntuarFijoClienteCommandStrat(5.0))
            )

            cliente.ejecutarComandos()

            cliente.valoracionesPendientes.size shouldBe 0
        }

        it ("cliente puntua todas las valoraciones con un valor fijo") {
            cliente.comandos = mutableListOf(
                PuntuarLocalesClienteCommand(PuntuarFijoClienteCommandStrat(5.0))
            )

            cliente.ejecutarComandos()

            cliente.valoracionesPendientes.size shouldBe 0
            local1.getPuntuacion() shouldBe 5.0
            local2.getPuntuacion() shouldBe (5.0+3.0)/2.0
            local3.getPuntuacion() shouldBe (5.0+1.0)/2.0
        }

        it ("cliente puntua todas las valoraciones con el promedio del local") {
            cliente.comandos = mutableListOf(
                PuntuarLocalesClienteCommand(PuntuarPromedioClienteCommandStrat())
            )

            cliente.ejecutarComandos()

            cliente.valoracionesPendientes.size shouldBe 0
            local1.getPuntuacion() shouldBe 5.0
            local2.getPuntuacion() shouldBe 3.0
            local3.getPuntuacion() shouldBe 1.0
        }

        it ("cliente puntua todas las valoraciones con un valor aleatorio") {
            cliente.comandos = mutableListOf(
                PuntuarLocalesClienteCommand(PuntuarRandomClienteCommandStrat())
            )

            cliente.ejecutarComandos()

            cliente.valoracionesPendientes.size shouldBe 0
            (local1.getPuntuacion() >= 0.0 && local1.getPuntuacion() <= 5.0) shouldBe true
            (local2.getPuntuacion() >= 0.0 && local2.getPuntuacion() <= 5.0) shouldBe true
            (local3.getPuntuacion() >= 0.0 && local3.getPuntuacion() <= 5.0) shouldBe true
        }

        it("cliente crea un pedido"){
            cliente.comandos = mutableListOf(
                CrearPedidoClienteCommand(
                    mutableListOf(dish),
                    local1,
                    MedioDePago.EFECTIVO,
                    //mailsender,
                    //platorepository
                )
            )

            cliente.ejecutarComandos()
        }
    }
})