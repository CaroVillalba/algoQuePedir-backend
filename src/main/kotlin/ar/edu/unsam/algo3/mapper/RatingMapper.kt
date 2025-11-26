package ar.edu.unsam.algo3.mapper

import ar.edu.unsam.algo3.domain.Rating
import ar.edu.unsam.algo3.dto.PostRatingDTO

object RatingMapper {
    fun toDTO(rating: Rating): PostRatingDTO {
        return PostRatingDTO(
            rating.client.username,
            rating.local.id!!,
            rating.score.toInt(),
            rating.description
        )
    }
}