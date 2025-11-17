package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.dto.AuthDTO
import ar.edu.unsam.algo3.dto.UserDTO
import ar.edu.unsam.algo3.exceptions.BusinessException
import ar.edu.unsam.algo3.mapper.AuthMapper
import ar.edu.unsam.algo3.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/v1/auth")
@CrossOrigin(origins = [
    "http://localhost:5173",
    "http://localhost:5174"
])
class UserController(private val userService: UserService) {

    @GetMapping("/login")
    fun getClient(
        @RequestParam username: String,
        @RequestParam password: String
    ): ResponseEntity<AuthDTO> {
        val client = userService.getUser(username, password)
        return ResponseEntity.ok(AuthMapper.toDTO(client))
    }

    @PostMapping("/register")
    fun createClient(@RequestBody dto: UserDTO): ResponseEntity<Void> {
        val client = AuthMapper.toEntity(dto)
        userService.createClient(client)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }
}