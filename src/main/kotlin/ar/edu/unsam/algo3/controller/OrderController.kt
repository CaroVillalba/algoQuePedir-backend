package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.domain.ClientRepository
import ar.edu.unsam.algo3.domain.DishRepository
import ar.edu.unsam.algo3.dto.OrderDTO
import ar.edu.unsam.algo3.domain.LocalRepository
import ar.edu.unsam.algo3.dto.CheckoutOrderDTO
import ar.edu.unsam.algo3.mapper.OrderMapper
import ar.edu.unsam.algo3.service.OrderService
import ar.edu.unsam.algo3.service.UserService
import org.springframework.web.bind.annotation.*
import java.time.LocalTime

@RestController
@RequestMapping("/v1/order")
@CrossOrigin(origins = [
    "http://localhost:5173",
    "http://localhost:5174"
])
class OrderController(
    private val orderService: OrderService,
    private val clientService: UserService,
    private val localRepository: LocalRepository,
    private val dishRepository: DishRepository,
) {

    @GetMapping
    fun getByState(@RequestParam(required = false) state: String?): List<OrderDTO> {
        val orders = if (state.isNullOrBlank() || state == "Todos") {
            orderService.getAll()
        } else {
            orderService.getByState(state)
        }
        return orders.map { OrderMapper.toOrderDTO(it) }
    }

    @GetMapping("/{orderId}")
    fun get(@PathVariable orderId: Int): OrderDTO {
        val order = orderService.getByID(orderId)
        return OrderMapper.toOrderDTO(order)
    }

    @PostMapping
    fun createOrder(@RequestParam clientId: Int, @RequestBody dto: CheckoutOrderDTO) {
        val client = clientService.getClientById(clientId)

        val order = OrderMapper.toEntity(
            dto,
            client,
            localRepository,
            dishRepository
        )

        order.pickupTime = LocalTime.now()

        orderService.create(order)
    }

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
