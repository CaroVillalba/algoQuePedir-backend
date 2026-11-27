package ar.edu.unsam.algo3.dto

data class LocalCheckoutDTO (
    val id: Int?,
    val name: String,
    val imgURL: String,
    val freeDelivery: Boolean,
    val paymentMethods: List<PaymentMethodTaxDTO>,
    val distanceKm: Double?,
)