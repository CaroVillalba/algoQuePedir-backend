package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.dto.LocalAccountDTO
import ar.edu.unsam.algo3.exceptions.BusinessException
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
    fun getLocal(@PathVariable id: Int): ResponseEntity<LocalAccountDTO> {
        return try {
            val dto = localService.getLocalById(id)
            ResponseEntity.ok(dto)
        } catch (e: BusinessException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    // PUT /local/{id}
    @PutMapping("/{id}")
    fun updateLocal(
        @PathVariable id: Int,
        @RequestBody dto: LocalAccountDTO
    ): ResponseEntity<LocalAccountDTO> {
        return try {
            val updated = localService.updateLocal(id, dto)
            ResponseEntity.ok(updated)
        } catch (e: BusinessException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)
        }
    }
}