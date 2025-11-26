package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.dto.PostRatingDTO
import ar.edu.unsam.algo3.dto.UserDTO
import ar.edu.unsam.algo3.mapper.RatingMapper
import ar.edu.unsam.algo3.mapper.UserMapper
import ar.edu.unsam.algo3.service.LocalService
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
class RatingController(private val ratingService: RatingService, private val userService: UserService, private val localService: LocalService) {

    @GetMapping("/local/{localId}")
    fun getLocalRatings(@PathVariable localId: Int) : ResponseEntity<List<PostRatingDTO>> {
        val local = localService.getById(localId)
        val ratings = local.ratings
        return ResponseEntity.ok(ratings.map { RatingMapper.toDTO(it)})
    }

    @PostMapping
    fun postRating(@RequestBody dto: PostRatingDTO): ResponseEntity<Void> {
        val client = userService.getUserByUsername(dto.username)
        ratingService.createRating(client, dto)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }
}