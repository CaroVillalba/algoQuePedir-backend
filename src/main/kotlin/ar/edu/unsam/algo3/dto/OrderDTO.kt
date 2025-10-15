package ar.edu.unsam.algo3.dto

import ar.edu.unsam.algo3.domain.OrderStatus
import ar.edu.unsam.algo3.domain.PaymentMethod
import java.time.LocalTime

/*ATENCION: MedioDePago es medio distinto a lo que tenemos en el front. Puede ser que haya que agregar algun icono, ¿O no?*/
/*También ver el tema del state, que acá es un Enum*/

data class OrderDTO (
    val comision: Number,
    val increment: Number,
    val id: Number,
    val user: UserOrderDTO,
    val hora: LocalTime?,
    val platos: List<MenuDTO>,
    val direction: DireccionOrderDTO,
    val metodoPago: PaymentMethod,
    val cantidad: Number,
    val total: Number,
    val subtotal: Number,
    val state: OrderStatus
)