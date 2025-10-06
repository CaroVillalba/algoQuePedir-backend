package ar.edu.unsam.algo2

interface MailSender {
    fun enviarCorreo(destinatario: String, asunto: String, cuerpo: String)
}

open class AuditoriaMailSender: MailSender {
    val log = mutableListOf<String>()

    override fun enviarCorreo(destinatario: String, asunto: String, cuerpo: String) {
        log.add("Enviado a $destinatario: $asunto - $cuerpo")
    }
}