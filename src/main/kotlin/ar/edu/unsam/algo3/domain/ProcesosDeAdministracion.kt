package ar.edu.unsam.algo3.domain

import java.time.LocalDateTime

//Clase que administra la aplicacion
//Este tiene la capacidad de añadir procesos a un programa, y ejecutarlos todos
//en un momento determinado.
class Administrador {
    var procesos =  mutableListOf<ProcesoConMail>()

    fun addProceso(proceso: ProcesoConMail) {
        procesos.add(proceso)
    }

    fun executeProcesos() {
        procesos.forEach {proceso -> proceso.execute()}
    }
}

abstract class ProcesoConMail (val mailSender : MailSender) {
    open var nombreProceso = "Proceso"

    fun execute() {
        procesar()
        mandarCorreo()
    }
    abstract fun procesar()
    fun mandarCorreo() {
        val destinatario = "admin@aqp.com.ar"
        val asunto = nombreProceso //https://stackoverflow.com/questions/32684739/kotlin-get-type-as-string
        val cuerpo = "Se realizó el proceso: $asunto"
        mailSender.enviarCorreo(destinatario,asunto,cuerpo)
    }
}

class AgregarLocales (
    val repo: LocalRepository,
    private val listaLocales: List<Local>,
    mailSender: MailSender
): ProcesoConMail(mailSender) {
    override var nombreProceso: String = "Proceso de Agregado de Locales"
    override fun procesar() {
        listaLocales.forEach {
            repo.create(it)
        }
    }
}

class BorrarCuponesVencidosProceso(
    var cuponRepository: CuponRepository,
    mailSender: MailSender
):  ProcesoConMail(mailSender){

    override var nombreProceso = "Proceso de Borrado de Cupones Vencidos"

    override fun procesar(){
        var keys = cuponRepository.getIds()
        for (key in keys){
            var cupon = cuponRepository.getById(key)!!
            if (cupon.estaExpirado() && !cupon.fueAplicado) {
                println("El cupon "+cupon+"expiro para siempre ooooohhh borrandoo....")
                cuponRepository.delete(cupon)
            }
        }
    }
}

class ActualizarIngredientesProceso(
    var updater: UpdaterIngredientes,
    mailSender: MailSender
): ProcesoConMail(mailSender){

    override var nombreProceso = "Proceso de Actualizacion de Ingredientes"

    override fun procesar(){
        updater.updateIngredientes()
    }
}

class BorrarMensajesLeidos(
    val inbox: Inbox,
    val dias: Long,
    mailSender: MailSender
): ProcesoConMail(mailSender) {
    override var nombreProceso = "Proceso de Borrado de Mensajes Leidos"

    override fun procesar(){
        borrarMensajesAntiguosLeidos(dias)
    }

    fun borrarMensajesAntiguosLeidos(diasAntiguedad: Long) {
        val fechaLimite = LocalDateTime.now().minusDays(diasAntiguedad)
        inbox.mensajes.removeIf { mensaje ->
            mensaje.estaLeido() && mensaje.fechaEmision.isBefore(fechaLimite)
        }
    }
}