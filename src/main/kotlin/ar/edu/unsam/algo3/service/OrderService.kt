package ar.edu.unsam.algo3.service


import ar.edu.unsam.algo3.domain.Client
import ar.edu.unsam.algo3.domain.Address
import ar.edu.unsam.algo3.domain.Order
import ar.edu.unsam.algo3.domain.OrderRepository
import ar.edu.unsam.algo3.domain.Dish
import ar.edu.unsam.algo3.dto.DireccionOrderDTO
import ar.edu.unsam.algo3.dto.MenuDTO
import ar.edu.unsam.algo3.dto.OrderDTO
import ar.edu.unsam.algo3.dto.UserOrderDTO
import org.springframework.stereotype.Service
import java.math.RoundingMode
import kotlin.String


@Service
class OrderService ( private val pedidoRepository: OrderRepository){
    fun getAll(): List<OrderDTO> {
        return pedidoRepository.getAll().map { toOrderDTO(it) }
    }

    fun getByState(state: String): List<OrderDTO> {
        return pedidoRepository.search(state).map { toOrderDTO(it) }
    }

    fun getByID(id: Int): OrderDTO? {
        val pedido = pedidoRepository.getById(id) ?: return null
        return toOrderDTO(pedido)
    }

    private fun toOrderDTO(pedido: Order): OrderDTO {
        return OrderDTO(
            id = pedido.id,
            user = toUserDTO(pedido.client),
            hora = pedido.pickupTime,
            platos = pedido.dishes.map { toPlateDTO(it) },
            direction = toDirectionDTO(pedido.client.address),
            metodoPago = pedido.paymentMethod,
            cantidad = pedido.dishes.size,
            subtotal = pedido.baseTotal(),
            total = pedido.getOrderTotal(),
            comision = pedido.deliveryCost(),
            increment = pedido.applyTax(),
            state = pedido.status
        )
    }

    fun toUserDTO(user: Client): UserOrderDTO {
        return UserOrderDTO(
            nombre = user.firstName,
            username = user.username
        )
        }

    /*ATENCION!!: Esto se puede usar en el PlateService tmb, es lo mismo. */
        fun toPlateDTO(dish: Dish): MenuDTO {
            return MenuDTO(
                id = dish.id,
                imageUrl = dish.imageURL,
                name = dish.name,
                description = dish.description,
                price = dish.salePrice().toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toDouble()
            )
        }

     /*ATENCION!!: Acá puede haber un problema con las coordenadas... RESUELTO: Creando otro DTO de direccion (hablar con el grupo)*/
    fun toDirectionDTO(address: Address): DireccionOrderDTO {
        return DireccionOrderDTO(
            calle = address.street,
            altura = address.number,
            coordenadas = address.coordinates
        )
    }


}


