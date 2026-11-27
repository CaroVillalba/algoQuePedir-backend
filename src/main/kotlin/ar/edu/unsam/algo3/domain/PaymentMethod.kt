package ar.edu.unsam.algo3.domain

enum class PaymentMethod {
    EFECTIVO {
        override fun tax(): Double =  1.0
    },
    DEBITO,
    CREDITO,
    TRANSFERENCIA,
    QR,
    BITCOIN;

    open fun tax(): Double = 1.05
}