package ar.edu.unsam.algo3.service


import ar.edu.unsam.algo3.domain.Cliente
import ar.edu.unsam.algo3.domain.Direccion
import ar.edu.unsam.algo3.domain.EstadoPedido
import ar.edu.unsam.algo3.domain.Pedido
import ar.edu.unsam.algo3.domain.PedidoRepository
import ar.edu.unsam.algo3.domain.Plato
import ar.edu.unsam.algo3.dto.DireccionDTO
import ar.edu.unsam.algo3.dto.DireccionOrderDTO
import ar.edu.unsam.algo3.dto.MenuDTO
import ar.edu.unsam.algo3.dto.OrderDTO
import ar.edu.unsam.algo3.dto.UserOrderDTO
import org.springframework.stereotype.Service
import java.math.RoundingMode
import kotlin.String


@Service
class OrderService ( private val pedidoRepository: PedidoRepository){
    fun getAll(): List<OrderDTO> {
        return pedidoRepository.getAll().map { toOrderDTO(it) }
    }

    fun getByID(id: Int): OrderDTO? {
        val pedido = pedidoRepository.getById(id) ?: return null
        return toOrderDTO(pedido)
    }

    private fun toOrderDTO(pedido: Pedido): OrderDTO {
        return OrderDTO(
            id = pedido.id,
            user = toUserDTO(pedido.cliente),
            hora = pedido.horaRetiro,
            platos = pedido.plato.map { toPlateDTO(it) },
            direction = toDirectionDTO(pedido.cliente.direccion),
            metodoPago = pedido.medioDePago,
            cantidad = pedido.plato.size,
            subtotal = pedido.totalBase(),
            total = pedido.totalDelPedido(),
            comision = pedido.costoDelivery(),
            increment = pedido.aplicarImpuesto(),
            state = pedido.estado
        )
    }

    fun toUserDTO(user: Cliente): UserOrderDTO {
        return UserOrderDTO(
            nombre = user.nombre,
            username = user.username
        )
        }

    /*ATENCION!!: Esto se puede usar en el PlateService tmb, es lo mismo. */
        fun toPlateDTO(plato: Plato): MenuDTO {
            return MenuDTO(
                imageUrl = plato.imageURL,
                name = plato.nombre,
                description = plato.descripcion,
                price = plato.valorDeVenta().toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toDouble()
            )
        }

     /*ATENCION!!: Acá puede haber un problema con las coordenadas... RESUELTO: Creando otro DTO de direccion (hablar con el grupo)*/
    fun toDirectionDTO(direccion: Direccion): DireccionOrderDTO {
        return DireccionOrderDTO(
            calle = direccion.calle,
            altura = direccion.altura,
            coordenadas = direccion.coordenadas
        )
    }


}


