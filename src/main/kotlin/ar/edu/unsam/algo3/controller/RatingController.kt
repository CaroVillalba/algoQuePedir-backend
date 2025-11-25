package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.dto.PostRatingDTO
import ar.edu.unsam.algo3.dto.UserDTO
import ar.edu.unsam.algo3.mapper.UserMapper
import ar.edu.unsam.algo3.service.RatingService
import ar.edu.unsam.algo3.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/rating")
@CrossOrigin(origins = [
    "http://localhost:5173",
    "http://localhost:5174"
])
class RatingController(private val ratingService: RatingService, private val userService: UserService ) {

    @GetMapping
    fun getUserProfile(username : String ) : ResponseEntity<UserDTO> {
        val client = userService.getUserByUsername(username)
        return ResponseEntity.ok(UserMapper.toDTO(client))
    }

    @PostMapping
    fun postRating(@RequestBody dto: PostRatingDTO): ResponseEntity<Void> {
        val client = userService.getUserByUsername(dto.username)
        ratingService.createRating(client, dto)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }


    @PatchMapping
    fun updateProfile(@RequestBody dto : UserDTO) {
        val existingClient = userService.getUserByUsername(dto.username)
        val updatedClient = UserMapper.toUpdateEntity(dto, existingClient)
        userService.updateProfile(updatedClient)
    }
}