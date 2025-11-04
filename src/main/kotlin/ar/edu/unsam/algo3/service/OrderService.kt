package ar.edu.unsam.algo3.service

import ar.edu.unsam.algo3.domain.Order
import ar.edu.unsam.algo3.domain.OrderRepository
import ar.edu.unsam.algo3.domain.OrderStatus
import ar.edu.unsam.algo3.exceptions.BusinessException
import org.springframework.stereotype.Service

@Service
class OrderService(private val orderRepository: OrderRepository) {

    fun getAll(): List<Order> = orderRepository.getAll()

    fun getByState(state: String): List<Order> = orderRepository.search(state)
        ?: throw BusinessException("El estado solicitado no existe.")

    fun getByID(id: Int): Order = orderRepository.getById(id)
        ?: throw BusinessException("El pedido solicitado no existe.")

    fun create(order: Order) {
        orderRepository.create(order)
    }

    fun updateOrderState(orderId: Int): Order {
        val order = orderRepository.getById(orderId)
            ?: throw BusinessException("Pedido no encontrado")

        order.status = when (order.status) {
            OrderStatus.PENDIENTE -> OrderStatus.PREPARADO
            OrderStatus.PREPARADO -> OrderStatus.ENTREGADO
            else -> throw BusinessException("No se puede actualizar el estado de este pedido")
        }

        orderRepository.update(order)
        return order
    }

    fun deleteOrder(orderId: Int) {
        val order = orderRepository.getById(orderId)
            ?: throw BusinessException("El pedido que intentás eliminar no existe.")

        orderRepository.delete(order)
    }
}
