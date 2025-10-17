package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.dto.OrderDTO
import ar.edu.unsam.algo3.exceptions.BusinessException
import ar.edu.unsam.algo3.mapper.OrderMapper
import ar.edu.unsam.algo3.service.OrderService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/order")
@CrossOrigin(origins = ["http://localhost:5173"])
class OrderController(private val orderService: OrderService) {

    @GetMapping("/order")
    fun getByState(@RequestParam(required = false) state: String?): ResponseEntity<List<OrderDTO>> {
        return try {
            val pedidos = if (state.isNullOrBlank() || state == "Todos") {
                orderService.getAll()
            } else {
                orderService.getByState(state)
            }
            val dtoList = pedidos.map { OrderMapper.toOrderDTO(it) }
            ResponseEntity.ok(dtoList)
        } catch (e: BusinessException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @GetMapping("/orderDetail/{orderId}")
    fun get(@PathVariable orderId: Int): ResponseEntity<OrderDTO> {
        val pedido = orderService.getByID(orderId)
        return ResponseEntity.ok(OrderMapper.toOrderDTO(pedido))
    }
}
