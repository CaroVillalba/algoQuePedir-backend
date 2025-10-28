package ar.edu.unsam.algo3.dto

import ar.edu.unsam.algo3.domain.OrderStatus
import ar.edu.unsam.algo3.domain.PaymentMethod
import java.time.LocalTime

/*ATENCION: MedioDePago es medio distinto a lo que tenemos en el front. Puede ser que haya que agregar algun icono, ¿O no?*/
/*También ver el tema del state, que acá es un Enum*/

data class OrderDTO (
    val comission: Double,
    val increment: Double,
    val id: Int?,
    val user: UserOrderDTO,
    val time: LocalTime?,
    val dishes: List<OrderDetailDTO>,
    val direction: AddressDTO,
    val paymentMethod: PaymentMethod,
    val quantity: Int,
    val total: Double,
    val subtotal: Double,
    val state: OrderStatus
)