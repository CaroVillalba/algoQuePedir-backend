package ar.edu.unsam.algo3.domain

interface MailSender {
    fun enviarCorreo(destinatario: String, asunto: String, cuerpo: String)
}

open class AuditoriaMailSender: MailSender {
    val log = mutableListOf<String>()

    override fun enviarCorreo(destinatario: String, asunto: String, cuerpo: String) {
        log.add("Enviado a $destinatario: $asunto - $cuerpo")
    }
}