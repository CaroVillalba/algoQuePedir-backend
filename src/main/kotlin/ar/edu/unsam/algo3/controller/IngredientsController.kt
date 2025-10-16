package ar.edu.unsam.algo3.controller


import ar.edu.unsam.algo3.dto.IngredienteDTO
import ar.edu.unsam.algo3.exceptions.BusinessException
import ar.edu.unsam.algo3.exceptions.ElementDoesNotExistException
import ar.edu.unsam.algo3.service.IngredientsService

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/ingredients")
@CrossOrigin(origins = ["http://localhost:5173"])
class IngredientsController (private val ingredientsService: IngredientsService){

    // GET
    @GetMapping
    fun get() : ResponseEntity<List<IngredienteDTO>> {
        return try {
            val dto = ingredientsService.getAll()
            ResponseEntity.ok(dto)
        } catch (e: BusinessException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    // GET
    @GetMapping("/{id}")
    fun getByIngredientId(@PathVariable("id") id: Int) : ResponseEntity<IngredienteDTO> {
        return try {
            val dto = ingredientsService.getById(id)
            ResponseEntity.ok(dto)
        }
        catch (e: ElementDoesNotExistException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
        catch (e: Exception){
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    @PostMapping
    fun create(@RequestBody ingrediente: IngredienteDTO) : ResponseEntity<Int> {
        ingredientsService.create(ingrediente)
        return ResponseEntity.ok().build()
    }

    @PutMapping
    fun update(@RequestBody ingrediente: IngredienteDTO) : ResponseEntity<Int> {
        ingredientsService.update(ingrediente.id, ingrediente)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable("id") id: Int) : ResponseEntity<Int> {
        ingredientsService.delete(id)
        return ResponseEntity.ok().build()
    }
}