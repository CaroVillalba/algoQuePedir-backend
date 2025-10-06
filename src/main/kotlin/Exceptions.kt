package ar.edu.unsam.algo2

class PedidoInvalidoException(override var message: String): Exception() {
    /* Este Exception es para cuando el pedido usado no es aceptable */
}
class PedidoStateException(override var message: String): Exception() {
    /* Este Exception es para cuando el pedido esta en un estado en el cual no deberia*/
}
class PlatoInvalidoException(override var message: String): Exception() {
    /*  */
}
class MedioDePagoInvalidoException(override var message: String): Exception()
class AsignacionPedidoException(override var message: String): Exception()
class ValoracionException(override var message: String): Exception()
class CuponNoAplicacionException(override var message: String): Exception()