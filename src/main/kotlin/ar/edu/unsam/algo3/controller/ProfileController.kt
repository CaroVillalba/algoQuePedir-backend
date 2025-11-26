package ar.edu.unsam.algo3.controller
import ar.edu.unsam.algo3.domain.Client
import ar.edu.unsam.algo3.service.UserService
import ar.edu.unsam.algo3.dto.UserDTO
import ar.edu.unsam.algo3.mapper.AuthMapper
import ar.edu.unsam.algo3.mapper.UserMapper
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/user")
@CrossOrigin(origins = [
    "http://localhost:5173",
    "http://localhost:5174"
])
class ProfileController( private val userService: UserService ) {

    @GetMapping
    fun getUserProfile(username: String): ResponseEntity<UserDTO> {
        val client = userService.getUserByUsername(username)
        return ResponseEntity.ok(UserMapper.toDTO(client))
    }

    @PostMapping
    fun createProfile(@RequestBody dto: UserDTO): ResponseEntity<Void> {
        val user = UserMapper.toEntity(dto)
        userService.createUser(user)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }


    @PatchMapping
    fun updateProfile(@RequestBody dto: UserDTO) {
        println(dto)
        val existingClient = userService.getUserByUsername(dto.username)
        val updatedClient = UserMapper.toUpdateEntity(dto, existingClient)
        println(updatedClient)
        userService.updateProfile(updatedClient)
    }
}
