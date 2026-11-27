package ar.edu.unsam.algo3.controller


import ar.edu.unsam.algo3.dto.IngredientDTO
import ar.edu.unsam.algo3.mapper.IngredientMapper
import ar.edu.unsam.algo3.service.IngredientService

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
@CrossOrigin(origins = [
    "http://localhost:5173",
    "http://localhost:5174"
])
class IngredientController(private val ingredientService: IngredientService) {

    // GET /ingredients
    @GetMapping
    fun getAll(): List<IngredientDTO> {
        return ingredientService.getAll().map { IngredientMapper.toDTO(it) }
    }

    // GET /ingredients/{id}
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Int): IngredientDTO {
        val ingredient = ingredientService.getByIdEntity(id)
        return IngredientMapper.toDTO(ingredient)
    }

    // POST /ingredients
    @PostMapping
    fun create(@RequestBody dto: IngredientDTO) {
        val ingredient = IngredientMapper.toEntity(dto)
        ingredientService.create(ingredient)
    }

    // PUT /ingredients/{id}
    @PutMapping
    fun update(@RequestBody dto: IngredientDTO) {
        val existing = ingredientService.getByIdEntity(dto.id)
        val updated = IngredientMapper.updateEntity(dto, existing)
        ingredientService.update(updated)
    }

    // DELETE /ingredients/{id}
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Int) {
        ingredientService.deleteById(id)
    }
}
