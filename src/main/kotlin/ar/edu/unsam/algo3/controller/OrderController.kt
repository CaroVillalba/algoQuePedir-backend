package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.dto.OrderDTO
import ar.edu.unsam.algo3.mapper.OrderMapper
import ar.edu.unsam.algo3.service.OrderService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/order")
@CrossOrigin(origins = ["http://localhost:5173"])
class OrderController(private val orderService: OrderService) {

    @GetMapping
    fun getByState(@RequestParam(required = false) state: String?): List<OrderDTO> {
        val orders = if (state.isNullOrBlank() || state == "Todos") {
            orderService.getAll()
        } else {
            orderService.getByState(state)
        }
        return orders.map { OrderMapper.toOrderDTO(it) }
    }
    // TODO: Sacar orderDetail
    @GetMapping("/orderDetail/{orderId}")
    fun get(@PathVariable orderId: Int): OrderDTO {
        val order = orderService.getByID(orderId)
        return OrderMapper.toOrderDTO(order)
    }

    // TODO: El orderID debería venir en el body
    @PatchMapping("/{orderId}")
    fun advanceOrderState(@PathVariable orderId: Int): OrderDTO {
        val updatedOrder = orderService.updateOrderState(orderId)
        return OrderMapper.toOrderDTO(updatedOrder)
    }

    @DeleteMapping("/{orderId}")
    fun deleteOrder(@PathVariable orderId: Int) {
        orderService.deleteOrder(orderId)
    }
}
