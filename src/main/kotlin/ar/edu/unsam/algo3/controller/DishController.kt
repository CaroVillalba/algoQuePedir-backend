package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.domain.Dish
import ar.edu.unsam.algo3.service.PlateService
import ar.edu.unsam.algo3.dto.MenuDTO
import ar.edu.unsam.algo3.exceptions.BusinessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/v1/dishes")
@CrossOrigin(origins = ["http://localhost:5173"])
class DishController (private val plateService: PlateService){
    // GET
    @GetMapping("/menu")
    fun get() :ResponseEntity<List<MenuDTO>> {
        return try {
            val dto = plateService.getAll()
            ResponseEntity.ok(dto)
        } catch (e: BusinessException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }
    @PostMapping("/edit-plate")
    fun createPlate(@RequestBody dish: Dish){
        try{
            plateService.createPlate(dish)
        }catch (e: BusinessException){
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        }
    }
}