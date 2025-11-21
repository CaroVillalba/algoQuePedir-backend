package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.domain.Address
import ar.edu.unsam.algo3.domain.Client
import ar.edu.unsam.algo3.domain.DishRepository
import ar.edu.unsam.algo3.dto.OrderDTO
import ar.edu.unsam.algo3.domain.LocalRepository
import ar.edu.unsam.algo3.dto.CheckoutOrderDTO
import ar.edu.unsam.algo3.mapper.OrderMapper
import ar.edu.unsam.algo3.service.OrderService
import org.springframework.web.bind.annotation.*
import org.uqbar.geodds.Point
import java.time.LocalTime

@RestController
@RequestMapping("/v1/order")
@CrossOrigin(origins = [
    "http://localhost:5173",
    "http://localhost:5174"
])
class OrderController(private val orderService: OrderService,  private val localRepository: LocalRepository,  private val dishRepository: DishRepository) {

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
    fun createOrder(@RequestBody dto: CheckoutOrderDTO) {
        //mock para poder crear un pedido
        //TODO: incorporar con usuarios
        //TODO: Incorporar bien el pedido al hacer el post (que todos los campos sean correctos, ahora algunos los pone como null)
        val fakeClient = Client(
            firstName = "Usuario",
            lastName = "Demo",
            username = "demoUser",
            password = "1234",
            address = Address("Calle Falsa", "123", Point(-34.6, -58.4))
        )

        val order = OrderMapper.toEntity(dto, fakeClient, localRepository, dishRepository)
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
