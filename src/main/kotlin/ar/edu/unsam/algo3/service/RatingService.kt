package ar.edu.unsam.algo3.service

import ar.edu.unsam.algo3.domain.Client
import ar.edu.unsam.algo3.domain.Rating
import ar.edu.unsam.algo3.domain.RatingRepository
import ar.edu.unsam.algo3.domain.User
import ar.edu.unsam.algo3.dto.PostRatingDTO
import org.springframework.stereotype.Service

@Service
class RatingService (
    ratingRepository: RatingRepository
){
    fun createRating(client: Client, rating: PostRatingDTO) {
        //Busca el local con el id determinado
        val localId = rating.localId
        val pendingRating = client.pendingRatings.find { pendingRating -> pendingRating.local.id == localId }

        client.rate(pendingRating!!.local, rating.rating.toDouble(), rating.description)
    }
}