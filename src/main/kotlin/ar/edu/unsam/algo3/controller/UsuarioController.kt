package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.dto.AuthDTO
import ar.edu.unsam.algo3.dto.LocalAccountDTO
import ar.edu.unsam.algo3.exceptions.BusinessException
import ar.edu.unsam.algo3.service.ClienteService
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


class UsuarioController(private val clienteService: ClienteService) {
    // GET /usuario/{id}
    @GetMapping("/{id}")
    fun getCliente(@PathVariable id: Int): ResponseEntity<AuthDTO> {
        return try {
            val dto = clienteService.getClienteById(id)
            ResponseEntity.ok(dto)
        } catch (e: BusinessException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

}