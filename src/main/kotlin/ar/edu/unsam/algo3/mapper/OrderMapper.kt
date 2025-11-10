package ar.edu.unsam.algo3.mapper

import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.dto.*
import ar.edu.unsam.algo3.exceptions.BusinessException
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
            toAddressDTO(pedido.client.address),
            pedido.paymentMethod,
            pedido.dishes.size,
            pedido.getOrderTotal(),
            round(pedido.dishesCost()),
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

    fun toEntity(dto: CheckoutOrderDTO, client: Client, localRepo: LocalRepository, dishRepo: DishRepository): Order {
        val local = localRepo.getById(dto.restaurantId)
            ?: throw BusinessException("El restaurante no existe.")

        val dishes = dto.items.flatMap { item ->
            val dish = dishRepo.getById(item.dishId)
                ?: throw BusinessException("El plato con id ${item.dishId} no existe.")
            List(item.quantity) { dish }
        }

        return Order.createOrder(
            client,
            dishes,
            local,
            dto.paymentMethod
        )
    }

    fun toAddressDTO(address: Address): AddressDTO {
        return AddressDTO(
            address.street,
            address.number,
            address.coordinates
        )
    }

    fun round(value: Double): Double =
        value.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toDouble()
}
