package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.domain.Local
import ar.edu.unsam.algo3.domain.LocalRepository
import ar.edu.unsam.algo3.dto.DishDTO
import ar.edu.unsam.algo3.service.DishService
import ar.edu.unsam.algo3.dto.MenuDTO
import ar.edu.unsam.algo3.exceptions.ElementDoesNotExistException
import ar.edu.unsam.algo3.mapper.DishMapper
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/v1/dishes")
@CrossOrigin(origins = [
    "http://localhost:5173",
    "http://localhost:5174"
])
class DishController (private val dishService: DishService, private val localRepository: LocalRepository){
    // GET
    @GetMapping("/local/{localId}")
    fun getByLocal(@PathVariable localId: Int): List<MenuDTO> {
        val dishes = dishService.getByLocal(localId)
        return DishMapper.toMenuDTO(dishes)
    }

    @PostMapping
    fun createDish(@RequestBody dto: DishDTO) {
        val localObject: Local = localRepository.getById(dto.local)
            ?: throw ElementDoesNotExistException("Local con ID ${dto.local} no encontrado")
        val dish = DishMapper.toEntity(dto, localObject)
        dishService.createDish(dish)
    }

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Int): DishDTO {
        val dish = dishService.getById(id)
        return DishMapper.toDTO(dish, id)
    }

    // PUT /local/{id}
    @PutMapping("/{id}")
    fun update(@PathVariable id: Int, @RequestBody dto: DishDTO) {
        val oldDish = dishService.getById(id)
        val updatedDish = DishMapper.updateEntity(dto, oldDish)
        dishService.update(updatedDish)
    }
}