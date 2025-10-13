package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.domain.Ingrediente
import ar.edu.unsam.algo3.dto.IngredienteDTO
import ar.edu.unsam.algo3.dto.MenuDTO
import ar.edu.unsam.algo3.exceptions.BusinessException
import ar.edu.unsam.algo3.service.IngredientsService
import ar.edu.unsam.algo3.service.PlateService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1")
@CrossOrigin(origins = ["http://localhost:5173"])
class IngredientsController (private val ingredientsService: IngredientsService){
    // GET
    @GetMapping("/ingredients")
    fun get() : ResponseEntity<List<IngredienteDTO>> {
        return try {
            val dto = ingredientsService.getAll()
            ResponseEntity.ok(dto)
        } catch (e: BusinessException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }
}