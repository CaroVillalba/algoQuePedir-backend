package ar.edu.unsam.algo3.mapper

import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.dto.*
import java.math.RoundingMode

object OrderMapper {
    fun toOrderDTO(pedido: Order): OrderDTO {
        return OrderDTO(
            id = pedido.id,
            user = toUserDTO(pedido.client),
            hora = pedido.pickupTime,
            platos = pedido.dishes.map { toPlateDTO(it) },
            direction = toDirectionDTO(pedido.client.address),
            metodoPago = pedido.paymentMethod,
            cantidad = pedido.dishes.size,
            subtotal = round(pedido.baseTotal()),
            total = pedido.getOrderTotal(),
            comision = round(pedido.deliveryCost()),
            increment = round(pedido.applyTax()),
            state = pedido.status
        )
    }

    fun toUserDTO(user: Client): UserOrderDTO {
        return UserOrderDTO(
            nombre = user.firstName,
            username = user.username
        )
    }

    fun toPlateDTO(dish: Dish): OrderDetailDTO {
        return OrderDetailDTO(
            id = dish.id,
            imageUrl = dish.imageURL,
            name = dish.name,
            description = dish.description,
            price = dish.salePrice().toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toDouble(),
            cant = 1
        )
    }

    fun toDirectionDTO(address: Address): DireccionOrderDTO {
        return DireccionOrderDTO(
            calle = address.street,
            altura = address.number,
            coordenadas = address.coordinates
        )
    }

    fun round(value: Double): Double =
        value.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toDouble()
}
