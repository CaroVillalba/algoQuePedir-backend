package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.dto.AuthDTO
import ar.edu.unsam.algo3.exceptions.BusinessException
import ar.edu.unsam.algo3.mapper.AuthMapper
import ar.edu.unsam.algo3.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/v1")
@CrossOrigin(origins = ["http://localhost:5173"])
class UserController(private val userService: UserService) {

    @GetMapping("/auth/login")
    fun getClient(
        @RequestParam username: String,
        @RequestParam password : String
    ): ResponseEntity<AuthDTO> {
        return try {
            val client = userService.getClient(username, password)
            ResponseEntity.ok(AuthMapper.toDTO(client))
        } catch (e: BusinessException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    @PostMapping("/auth/register")
    fun createClient(@RequestBody dto: AuthDTO){
        try {
            val client = AuthMapper.toEntity(dto)
            userService.createClient(client)
            ResponseEntity.status(HttpStatus.CREATED).build()
        } catch (e: BusinessException) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.message)
        }
    }
}