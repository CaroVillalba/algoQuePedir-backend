import ar.edu.unsam.algo2.*
import ar.edu.unsam.algo2.Direccion
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import org.uqbar.geodds.Point
import java.time.LocalDate
import java.time.LocalDateTime

class ProcesosDeAdministracionSpec : DescribeSpec ({
    describe ("Procesos DeAdministracion") {
        lateinit var cuponRepository: CuponRepository
        lateinit var mailSender: MailSender
        lateinit var admin: Administrador
        lateinit var inbox: Inbox
        lateinit var ingredienteRepository: IngredienteRepository
        lateinit var serviceIngredientes: ServiceIngredientes

        beforeEach {
            cuponRepository = CuponRepository()
            mailSender = AuditoriaMailSender()
            admin = Administrador()
            inbox = Inbox()
            ingredienteRepository = IngredienteRepository()

        }

        describe("Proceso de borrado de mensajes") {
            it("debe borrar solo mensajes leídos antiguos") {
                val mensajeViejo = Mensaje(
                    LocalDateTime.now().minusDays(31),
                    "Viejo",
                    "Contenido viejo"
                )
                mensajeViejo.marcarComoLeido()

                val mensajeNuevoLeido = Mensaje(
                    LocalDateTime.now().minusDays(5),
                    "Nuevo leído",
                    "Contenido nuevo"
                )
                mensajeNuevoLeido.marcarComoLeido()

                val mensajeViejoNoLeido = Mensaje(
                    LocalDateTime.now().minusDays(35),
                    "Viejo no leído",
                    "Contenido viejo"
                )

                inbox.agregarMensaje(mensajeViejo)
                inbox.agregarMensaje(mensajeNuevoLeido)
                inbox.agregarMensaje(mensajeViejoNoLeido)

                admin.addProceso(BorrarMensajesLeidos(inbox, 30, mailSender))
                admin.executeProcesos()

                inbox.mensajes.size shouldBe 2
                (mailSender as AuditoriaMailSender).log.any {
                    it.contains("admin@aqp.com.ar") &&
                            it.contains("Proceso de Borrado de Mensajes Leidos")
                } shouldBe true
            }
        }

        describe("Proceso de actualización de ingredientes") {
            it("debe actualizar ingredientes correctamente") {
                serviceIngredientes = object : ServiceIngredientes {
                    override fun getIngredientes(): String {
                        return """[
                            {"id": 1, "nombre": "Tomate", "costo": 100.0, "grupo": "FRUTAS_Y_VERDURAS", "origenAnimal": false},
                            {"id": 2, "nombre": "Lechuga", "costo": 50.0, "grupo": "FRUTAS_Y_VERDURAS", "origenAnimal": false}
                        ]"""
                    }
                }
                var updaterIngredientes = UpdaterIngredientes(serviceIngredientes, ingredienteRepository)

                admin.addProceso(ActualizarIngredientesProceso(updaterIngredientes, mailSender))
                admin.executeProcesos()

                ingredienteRepository.getSize() shouldBe 2
                (mailSender as AuditoriaMailSender).log.any {
                    it.contains("admin@aqp.com.ar") &&
                    it.contains("Proceso de Actualizacion de Ingredientes")
                } shouldBe true
            }
        }

        describe("Proceso de borrado de cupones") {
            it("debe borrar cupones vencidos") {
                cuponRepository.create(CuponTopeMaximo(
                    LocalDate.now().minusYears(1),
                    30,
                    0.5,
                    false,
                    5000.0,
                    0.0
                ))

                admin.addProceso(BorrarCuponesVencidosProceso(cuponRepository, mailSender))
                admin.executeProcesos()

                cuponRepository.getSize() shouldBe 0
                (mailSender as AuditoriaMailSender).log.any {
                    it.contains("admin@aqp.com.ar") &&
                            it.contains("Proceso de Borrado de Cupones Vencidos")
                } shouldBe true
            }

            it("No hay cupones expirados?"){
                repeat(10) {
                    cuponRepository.create(CuponTopeMaximo(
                        LocalDate.now(),
                        30,
                        0.5,
                        false,
                        5000.0,
                        0.0
                    )
                    )
                }

                cuponRepository.getSize() shouldBe 10

                admin.addProceso(
                    BorrarCuponesVencidosProceso(
                        cuponRepository,
                        mailSender
                    )
                )

                admin.executeProcesos()

                cuponRepository.getSize() shouldBe 10
            }
        }

        describe("Proceso de agregar locales masivamente") {
            it("debe agregar locales correctamente") {
                val localRepository = LocalRepository()
                val locales = listOf(
                    Local("Local 1", direccion = Direccion("Calle 1", "123", Point(-3,-3)), mutableSetOf(MedioDePago.EFECTIVO), mutableSetOf()),
                    Local("Local 2", direccion = Direccion("Calle 2", "456", Point(2,2)), mutableSetOf(MedioDePago.EFECTIVO), mutableSetOf())
                )

                admin.addProceso(AgregarLocales(localRepository, locales, mailSender))
                admin.executeProcesos()

                localRepository.search("").size shouldBe 2
                (mailSender as AuditoriaMailSender).log.any {
                    it.contains("admin@aqp.com.ar") &&
                            it.contains("Proceso de Agregado de Locales")
                } shouldBe true
            }
        }
    }
})