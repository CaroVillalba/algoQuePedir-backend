package ar.edu.unsam.algo3.service

import ar.edu.unsam.algo3.domain.Order
import ar.edu.unsam.algo3.domain.OrderRepository
import ar.edu.unsam.algo3.exceptions.BusinessException
import org.springframework.stereotype.Service

@Service
class OrderService(private val pedidoRepository: OrderRepository) {

    fun getAll(): List<Order> = pedidoRepository.getAll()
        ?: throw BusinessException("No sé encontraron pedidos.")

    fun getByState(state: String): List<Order> = pedidoRepository.search(state)
        ?: throw BusinessException("No sé encontraron pedidos.")

    fun getByID(id: Int): Order? = pedidoRepository.getById(id)
        ?: throw BusinessException("No sé encontró el pedido solicitado.")
}
