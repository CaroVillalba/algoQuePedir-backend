package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.domain.Cliente
import ar.edu.unsam.algo3.dto.AuthDTO
import ar.edu.unsam.algo3.dto.LocalAccountDTO
import ar.edu.unsam.algo3.exceptions.BusinessException
import ar.edu.unsam.algo3.service.ClienteService
import ar.edu.unsam.algo3.service.LocalService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/v1")
@CrossOrigin(origins = ["http://localhost:5173"])


class UsuarioController(private val clienteService: ClienteService) {
    @GetMapping("/auth/login")
    fun getCliente(@RequestParam username: String,@RequestParam password : String): ResponseEntity<AuthDTO> {
        return try {
            val dto = clienteService.getCliente(username, password)
            ResponseEntity.ok(dto)
        } catch (e: BusinessException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @PostMapping("/auth/register")
    fun createCliente(@RequestBody client : Cliente){
        try {
             clienteService.createCliente(client)

        } catch (e: BusinessException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        }
    }


}