package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.domain.ClientRepository
import ar.edu.unsam.algo3.domain.DishRepository
import ar.edu.unsam.algo3.dto.OrderDTO
import ar.edu.unsam.algo3.dto.OrderDetailReactDTO
import ar.edu.unsam.algo3.domain.LocalRepository
import ar.edu.unsam.algo3.dto.CheckoutOrderDTO
import ar.edu.unsam.algo3.exceptions.BusinessException
import ar.edu.unsam.algo3.mapper.OrderMapper
import ar.edu.unsam.algo3.service.LocalService
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
    private val localService: LocalService,
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
    fun get(@PathVariable orderId: Int): OrderDetailReactDTO {
        val order = orderService.getByID(orderId)

        val local = order.local
        /*val distanceKm = order.client.let { localService.getDistanceToClient(local, order.client.id!!) }*/

        val distanceKm = try {
            localService.getDistanceToClient(local, order.client.id!!)
        } catch (e: BusinessException) {
            null    // No hay dirección → distancia no disponible
        }

        return OrderMapper.toOrderDetailDTO(order, distanceKm)
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

    @PatchMapping("/cancel/{orderId}")
    fun cancelOrder(@PathVariable orderId: Int): OrderDTO {
        val updated = orderService.cancelOrder(orderId)
        return OrderMapper.toOrderDTO(updated)
    }

    @DeleteMapping("/{orderId}")
    fun deleteOrder(@PathVariable orderId: Int) {
        orderService.deleteOrder(orderId)
    }


}
