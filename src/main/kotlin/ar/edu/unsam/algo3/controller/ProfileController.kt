package ar.edu.unsam.algo3.controller

import org.springframework.stereotype.Service
import ar.edu.unsam.algo3.exceptions.EmptyFieldsException
import ar.edu.unsam.algo3.exceptions.BusinessException
import ar.edu.unsam.algo3.service.UserService
import ar.edu.unsam.algo3.dto.UserDTO
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

    @PostMapping
    fun createProfile(@RequestBody dto : UserDTO): ResponseEntity<Void> {
        val user = UserMapper.toEntity(dto)
        userService.createUser(user)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

}