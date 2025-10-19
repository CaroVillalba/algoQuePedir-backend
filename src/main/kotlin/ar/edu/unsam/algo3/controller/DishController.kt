package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.domain.Dish
import ar.edu.unsam.algo3.domain.Local
import ar.edu.unsam.algo3.domain.LocalRepository
import ar.edu.unsam.algo3.dto.DishResponseDTO
import ar.edu.unsam.algo3.dto.LocalAccountDTO
import ar.edu.unsam.algo3.service.DishService
import ar.edu.unsam.algo3.dto.MenuDTO
import ar.edu.unsam.algo3.dto.PlatoDTO
import ar.edu.unsam.algo3.exceptions.BusinessException
import ar.edu.unsam.algo3.exceptions.ElementDoesNotExistException
import ar.edu.unsam.algo3.mapper.DishMapper
import ar.edu.unsam.algo3.mapper.LocalMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/v1/dishes")
@CrossOrigin(origins = ["http://localhost:5173"])
class DishController (private val dishService: DishService, private val localRepository: LocalRepository){
    // GET
    @GetMapping
    fun get() :ResponseEntity<List<MenuDTO>> {
        return try {
            val menu = dishService.getAll()
            val menuResponse = DishMapper.toMenuDTO(menu) //call to mapper y return MenuDTO
            ResponseEntity.ok(menuResponse)
        } catch (e: BusinessException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }
    @PostMapping
    fun createPlate(@RequestBody dto: PlatoDTO){
        try{
            val localObject: Local = localRepository.getById(dto.local) ?: throw IllegalArgumentException("Local with ID ${dto.local} not found.")
            val dish = DishMapper.toEntity(dto, localObject)
            dishService.createPlate(dish)
        }catch (e: BusinessException){
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        }
    }
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Int): ResponseEntity<DishResponseDTO> {
        val dish = dishService.getById(id)
        return ResponseEntity.ok(DishMapper.toDTO(dish))
        /* catch (e: BusinessException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.message ?: "Error de negocio")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error inesperado: ${e.message}")
        }*/
    }
    // PUT /local/{id}
    @PutMapping("/{id}")
    fun update(@PathVariable id: Int, @RequestBody dto: PlatoDTO): ResponseEntity<Any> {
        return try {
            val oldDish = dishService.getById(id)
            val updatedDish = DishMapper.updateEntity(dto, oldDish)
            dishService.update(updatedDish)
            ResponseEntity.ok("Plato actualizado correctamente")
        } catch (e: ElementDoesNotExistException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(e.message ?: "Local no encontrado")
        } catch (e: BusinessException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(e.message ?: "Datos inválidos")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error inesperado: ${e.message}")
        }
    }
}