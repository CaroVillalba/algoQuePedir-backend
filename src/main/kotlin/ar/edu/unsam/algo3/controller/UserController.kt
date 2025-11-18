package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.dto.AuthDTO
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
    fun getUser(
        @RequestParam username: String,
        @RequestParam password: String
    ): ResponseEntity<AuthDTO> {
        val admin = userService.getUser(username, password)
        return ResponseEntity.ok(AuthMapper.toDTO(admin))
    }

    @PostMapping("/register")
    fun createUser(@RequestBody dto: AuthDTO): ResponseEntity<Void> {
        val admin = AuthMapper.toAdmin(dto)
        userService.createUser(admin)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PutMapping("/{userId}/assign-local/{localId}")
    fun assignLocal(
        @PathVariable userId: Int,
        @PathVariable localId: Int
    ): ResponseEntity<String> {

        userService.assignLocalToAdmin(userId, localId)

        return ResponseEntity.ok("Local asignado correctamente")
    }
}