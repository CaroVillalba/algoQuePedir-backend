package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.dto.LocalAccountDTO
import ar.edu.unsam.algo3.exceptions.BusinessException
import ar.edu.unsam.algo3.exceptions.ElementDoesNotExistException
import ar.edu.unsam.algo3.mapper.LocalMapper
import ar.edu.unsam.algo3.service.LocalService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/local")
@CrossOrigin(origins = ["http://localhost:5173"])
class LocalController(private val localService: LocalService) {

    // GET /local/{id}
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Int): ResponseEntity<LocalAccountDTO> {
        return try {
            val dto = localService.getById(id)
            ResponseEntity.ok(LocalMapper.toDTO(dto))
        } catch (e: BusinessException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    // PUT /local/{id}
    @PutMapping("/{id}")
    fun update(
        @PathVariable id: Int,
        @RequestBody dto: LocalAccountDTO
    ): ResponseEntity<LocalAccountDTO> {
        return try {
            val local = localService.getById(id)
            val updatedLocal = LocalMapper.toEntity(dto, local)
            localService.update(updatedLocal)
            ResponseEntity.ok().build()
        } catch (e: ElementDoesNotExistException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        } catch (e: BusinessException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).build()
        }
    }
}