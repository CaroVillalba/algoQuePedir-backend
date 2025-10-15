package ar.edu.unsam.algo3.domain

import java.time.LocalDateTime
import ar.edu.unsam.algo3.domain.IngredientsUpdater

//Clase que administra la aplicacion
//Este tiene la capacidad de añadir procesos a un programa, y ejecutarlos todos
//en un momento determinado.
class Administrator {
    var processes = mutableListOf<ProcessWithMail>()

    fun addProcess(process: ProcessWithMail) {
        processes.add(process)
    }

    fun executeProcesses() {
        processes.forEach { process -> process.execute() }
    }
}

abstract class ProcessWithMail(val mailSender: MailSender) {
    open var processName = "Proceso"

    fun execute() {
        process()
        sendMail()
    }

    abstract fun process()

    fun sendMail() {
        val recipient = "admin@aqp.com.ar"
        val subject = processName //https://stackoverflow.com/questions/32684739/kotlin-get-type-as-string
        val body = "Se realizó el proceso: $subject"
        mailSender.sendEmail(recipient, subject, body)
    }
}

class AddLocalsProcess(
    val repo: LocalRepository,
    private val localList: List<Local>,
    mailSender: MailSender
) : ProcessWithMail(mailSender) {
    override var processName: String = "Proceso de Agregado de Locales"

    override fun process() {
        localList.forEach {
            repo.create(it)
        }
    }
}

class DeleteExpiredCouponsProcess(
    var couponRepository: CouponRepository,
    mailSender: MailSender
) : ProcessWithMail(mailSender) {

    override var processName = "Proceso de Borrado de Cupones Vencidos"

    override fun process() {
        val keys = couponRepository.getIds()
        for (key in keys) {
            val coupon = couponRepository.getById(key)!!
            if (coupon.isExpired() && !coupon.wasApplied) {
                println("El cupon " + coupon + " expiro para siempre ooooohhh borrandoo....")
                couponRepository.delete(coupon)
            }
        }
    }
}

class UpdateIngredientsProcess(
    var updater: IngredientsUpdater,
    mailSender: MailSender
) : ProcessWithMail(mailSender) {

    override var processName = "Proceso de Actualizacion de Ingredientes"

    override fun process() {
        updater.updateIngredients()
    }
}

class DeleteReadMessagesProcess(
    val inbox: Inbox,
    val days: Long,
    mailSender: MailSender
) : ProcessWithMail(mailSender) {

    override var processName = "Proceso de Borrado de Mensajes Leidos"

    override fun process() {
        deleteOldReadMessages(days)
    }

    fun deleteOldReadMessages(daysOld: Long) {
        val limitDate = LocalDateTime.now().minusDays(daysOld)
        inbox.messages.removeIf { mensaje ->
            mensaje.isRead() && mensaje.sentAt.isBefore(limitDate)
        }
    }
}
