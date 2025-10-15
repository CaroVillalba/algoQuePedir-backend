package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.domain.Client
import ar.edu.unsam.algo3.dto.AuthDTO
import ar.edu.unsam.algo3.exceptions.BusinessException
import ar.edu.unsam.algo3.service.ClientService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/v1")
@CrossOrigin(origins = ["http://localhost:5173"])


class UsuarioController(private val clientService: ClientService) {
    @GetMapping("/auth/login")
    fun getClient(@RequestParam username: String,@RequestParam password : String): ResponseEntity<AuthDTO> {
        return try {
            val dto = clientService.getClient(username, password)
            ResponseEntity.ok(dto)
        } catch (e: BusinessException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @PostMapping("/auth/register")
    fun createClient(@RequestBody client : Client){
        try {
             clientService.createClient(client)

        } catch (e: BusinessException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        }
    }


}