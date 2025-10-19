package ar.edu.unsam.algo3.controller


import ar.edu.unsam.algo3.dto.IngredientDTO
import ar.edu.unsam.algo3.exceptions.BusinessException
import ar.edu.unsam.algo3.exceptions.ElementDoesNotExistException
import ar.edu.unsam.algo3.mapper.IngredientMapper
import ar.edu.unsam.algo3.service.IngredientService

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
class IngredientController(private val ingredientService: IngredientService) {

    // GET /ingredients
    @GetMapping
    fun getAll(): ResponseEntity<List<IngredientDTO>> {
        return try {
            val ingredients = ingredientService.getAll()
            val dtos = ingredients.map { IngredientMapper.toDTO(it) }
            ResponseEntity.ok(dtos)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    // GET /ingredients/{id}
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Int): ResponseEntity<IngredientDTO> {
        return try {
            val ingredient = ingredientService.getByIdEntity(id)  // devuelve Ingredient
            val dto = IngredientMapper.toDTO(ingredient)
            ResponseEntity.ok(dto)
        } catch (e: ElementDoesNotExistException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    // POST /ingredients
    @PostMapping
    fun create(@RequestBody dto: IngredientDTO): ResponseEntity<Void> {
        return try {
            val ingredient = IngredientMapper.toEntity(dto)
            ingredientService.create(ingredient)
            ResponseEntity.status(HttpStatus.CREATED).build()
        } catch (e: BusinessException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    // PUT /ingredients/{id}
    @PutMapping
    fun update(@RequestBody dto: IngredientDTO): ResponseEntity<Void> {
        return try {
            val existing = ingredientService.getByIdEntity(dto.id)
            IngredientMapper.updateEntity(dto, existing)
            ingredientService.update(existing)
            ResponseEntity.ok().build()
        } catch (e: ElementDoesNotExistException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        } catch (e: BusinessException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }

    // DELETE /ingredients/{id}
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int): ResponseEntity<Void> {
        return try {
            ingredientService.deleteById(id)
            ResponseEntity.noContent().build()
        } catch (e: ElementDoesNotExistException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }
}
