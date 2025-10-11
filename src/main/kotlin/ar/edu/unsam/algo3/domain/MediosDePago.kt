package ar.edu.unsam.algo3.domain

enum class MedioDePago {
    EFECTIVO {
        override fun impuesto(): Double =  1.0
    },
    DEBITO,
    CREDITO,
    TRANSFERENCIA,
    QR,
    BITCOIN;

    open fun impuesto(): Double = 1.05
}