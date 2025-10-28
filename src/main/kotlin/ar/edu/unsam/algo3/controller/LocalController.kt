package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.dto.LocalAccountDTO
import ar.edu.unsam.algo3.exceptions.BusinessException
import ar.edu.unsam.algo3.exceptions.ElementDoesNotExistException
import ar.edu.unsam.algo3.mapper.LocalMapper
import ar.edu.unsam.algo3.service.LocalService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/local")
@CrossOrigin(origins = ["http://localhost:5173"])
class LocalController(private val localService: LocalService) {

    // GET /local/{id}
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Int): LocalAccountDTO {
        val local = localService.getById(id)
        return LocalMapper.toDTO(local)
    }

    // PUT /local/{id}
    @PutMapping("/{id}")
    fun update(@PathVariable id: Int, @RequestBody dto: LocalAccountDTO) {
        val local = localService.getById(id)
        val updatedLocal = LocalMapper.toEntity(dto, local)
        localService.update(updatedLocal)
    }
}
