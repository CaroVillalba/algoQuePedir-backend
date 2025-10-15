package ar.edu.unsam.algo3.domain

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import java.time.LocalDate
import org.uqbar.geodds.Point

class AdministrationProcessesSpec : DescribeSpec({
    describe ("Procesos De Administración") {
        lateinit var couponRepository: CouponRepository
        lateinit var mailSender: MailSender
        lateinit var administrator: Administrator
        lateinit var inbox: Inbox
        lateinit var ingredientRepository: IngredientRepository
        lateinit var ingredientService: IngredientsService

        beforeEach {
            couponRepository = CouponRepository()
            mailSender = AuditMailSender()
            administrator = Administrator()
            inbox = Inbox()
            ingredientRepository = IngredientRepository()
        }

        describe("Proceso de borrado de mensajes") {
            it("debe borrar solo mensajes leídos antiguos") {
                val oldMessage = Message(
                    LocalDateTime.now().minusDays(31),
                    "Viejo",
                    "Contenido viejo"
                )
                oldMessage.markAsRead()

                val newReadMessage = Message(
                    LocalDateTime.now().minusDays(5),
                    "Nuevo leído",
                    "Contenido nuevo"
                )
                newReadMessage.markAsRead()

                val oldUnreadMessage = Message(
                    LocalDateTime.now().minusDays(35),
                    "Viejo no leído",
                    "Contenido viejo"
                )

                inbox.addMessage(oldMessage)
                inbox.addMessage(newReadMessage)
                inbox.addMessage(oldUnreadMessage)

                administrator.addProcess(DeleteReadMessagesProcess(inbox, 30, mailSender))
                administrator.executeProcesses()

                inbox.internalMessages.size shouldBe 2
                (mailSender as AuditMailSender).log.any {
                    it.contains("admin@aqp.com.ar") &&
                            it.contains("Proceso de Borrado de Mensajes Leídos")
                } shouldBe true
            }
        }

        describe("Proceso de actualización de ingredientes") {
            it("debe actualizar ingredientes correctamente") {
                ingredientService = object : IngredientsService {
                    override fun getIngredients(): String {
                        return """[
                            {"id": 1, "name": "Tomate", "cost": 100.0, "group": "FRUTAS_Y_VERDURAS", "animalOrigin": false},
                            {"id": 2, "name": "Lechuga", "cost": 50.0, "group": "FRUTAS_Y_VERDURAS", "animalOrigin": false}
                        ]"""
                    }
                }
                val ingredientUpdater = IngredientsUpdater(ingredientService, ingredientRepository)

                administrator.addProcess(UpdateIngredientsProcess(ingredientUpdater, mailSender))
                administrator.executeProcesses()

                ingredientRepository.getSize() shouldBe 2
                (mailSender as AuditMailSender).log.any {
                    it.contains("admin@aqp.com.ar") &&
                            it.contains("Proceso de Actualización de Ingredientes")
                } shouldBe true
            }
        }

        describe("Proceso de borrado de cupones") {
            it("debe borrar cupones vencidos") {
                couponRepository.create(
                    MaxCapCoupon(
                        LocalDate.now().minusYears(1),
                        30,
                        0.5,
                        false,
                        5000.0,
                        0.0
                    )
                )

                administrator.addProcess(DeleteExpiredCouponsProcess(couponRepository, mailSender))
                administrator.executeProcesses()

                couponRepository.getSize() shouldBe 0
                (mailSender as AuditMailSender).log.any {
                    it.contains("admin@aqp.com.ar") &&
                            it.contains("Proceso de Borrado de Cupones Vencidos")
                } shouldBe true
            }

            it("No hay cupones expirados?") {
                repeat(10) {
                    couponRepository.create(
                        MaxCapCoupon(
                            LocalDate.now(),
                            30,
                            0.5,
                            false,
                            5000.0,
                            0.0
                        )
                    )
                }

                couponRepository.getSize() shouldBe 10

                administrator.addProcess(
                    DeleteExpiredCouponsProcess(
                        couponRepository,
                        mailSender
                    )
                )

                administrator.executeProcesses()

                couponRepository.getSize() shouldBe 10
            }
        }

        describe("Proceso de agregar locales masivamente") {
            it("debe agregar locales correctamente") {
                val restaurantRepository = LocalRepository()
                val restaurants = listOf(
                    Local(
                        "Local 1",
                        "https://super-local1.png",
                        Address("Calle 1",
                            "123",
                            Point(-3, -3)
                        ),
                        0.0,
                        0.0,
                        mutableSetOf(PaymentMethod.EFECTIVO),
                        mutableSetOf()
                    ),
                    Local(
                        "Local 2",
                        "https://super-local1.png",
                        Address("Calle 2",
                            "456",
                            Point(2, 2)
                        ),
                        0.0,
                        0.0,
                        mutableSetOf(PaymentMethod.EFECTIVO),
                        mutableSetOf()
                    )
                )

                administrator.addProcess(AddLocalsProcess(restaurantRepository, restaurants, mailSender))
                administrator.executeProcesses()

                restaurantRepository.search("").size shouldBe 2
                (mailSender as AuditMailSender).log.any {
                    it.contains("admin@aqp.com.ar") &&
                            it.contains("Proceso de Agregado de Locales")
                } shouldBe true
            }
        }
    }
})