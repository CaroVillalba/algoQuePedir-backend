package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.dto.OrderDTO
import ar.edu.unsam.algo3.exceptions.BusinessException
import ar.edu.unsam.algo3.service.OrderService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/v1/order")
@CrossOrigin(origins = ["http://localhost:5173"])
class OrderController (private val orderService: OrderService){
    // GET
    @GetMapping("/order")
    fun get() :ResponseEntity<List<OrderDTO>> {
        return try {
            val dto = orderService.getAll()
            ResponseEntity.ok(dto)
        } catch (e: BusinessException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }
}