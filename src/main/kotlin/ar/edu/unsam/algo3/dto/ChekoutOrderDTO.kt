package ar.edu.unsam.algo3.dto

import ar.edu.unsam.algo3.domain.PaymentMethod

data class CheckoutOrderDTO(
    val restaurantId: Int,
    val items: List<OrderItemDTO>,
    val paymentMethod: PaymentMethod
)

data class OrderItemDTO(
    val dishId: Int,
    val quantity: Int
)
