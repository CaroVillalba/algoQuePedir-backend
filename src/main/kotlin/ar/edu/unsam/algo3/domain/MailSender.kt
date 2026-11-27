package ar.edu.unsam.algo3.domain

interface MailSender {
    fun sendEmail(recipient: String, subject: String, body: String)
}

open class AuditMailSender: MailSender {
    val log = mutableListOf<String>()

    override fun sendEmail(recipient: String, subject: String, body: String) {
        log.add("Enviado a $recipient: $subject - $body")
    }
}
