package ar.edu.unsam.algo3.mapper

import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.dto.*
import java.math.RoundingMode

object OrderMapper {
    fun toOrderDTO(pedido: Order): OrderDTO {
        return OrderDTO(
            round(pedido.deliveryCost()),
            round(pedido.applyTax()),
            pedido.id,
            toUserDTO(pedido.client),
            pedido.pickupTime,
            pedido.dishes.distinctBy { it.id }
                .map { toPlateDTO(it, pedido.dishes) },
            toDirectionDTO(pedido.client.address),
            pedido.paymentMethod,
            pedido.dishes.size,
            round(pedido.baseTotal()),
            pedido.getOrderTotal(),
            pedido.status
        )
    }

    fun toUserDTO(user: Client): UserOrderDTO {
        return UserOrderDTO(
            user.firstName,
             user.username
        )
    }

    fun toPlateDTO(dish: Dish, dishesInOrder: List<Dish>): OrderDetailDTO {
        val quantity = dishesInOrder.count { it.id == dish.id }
        return OrderDetailDTO(
            dish.id,
            dish.imageURL,
            dish.name,
            dish.description,
            round(dish.salePrice()),
            quantity
        )
    }


    fun toDirectionDTO(address: Address): DireccionOrderDTO {
        return DireccionOrderDTO(
            address.street,
            address.number,
            address.coordinates
        )
    }

    fun round(value: Double): Double =
        value.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toDouble()
}
