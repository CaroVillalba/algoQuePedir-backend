package ar.edu.unsam.algo3.domain

interface ClientCommand {
    fun execute(client: Client)
}

interface ScoreCalculationStrategy {
    fun calculateScore(local: Local): Double
}

class RateLocalsClientCommand(var scoreStrategy: ScoreCalculationStrategy) : ClientCommand {
    override fun execute(client: Client) {
        val validRatings = client.pendingRatings.filter { it.canRate() }.toList()
        validRatings.forEach { rating ->
            client.rate(rating.local, scoreStrategy.calculateScore(rating.local), "Automated Rating")
        }
    }
}

class FixedScoreClientCommandStrategy(var score: Double) : ScoreCalculationStrategy {
    override fun calculateScore(local: Local): Double {
        return score
    }
}

class AverageScoreClientCommandStrategy() : ScoreCalculationStrategy {
    override fun calculateScore(local: Local): Double {
        return local.getScore()
    }
}

class RandomScoreClientCommandStrategy() : ScoreCalculationStrategy {
    override fun calculateScore(local: Local): Double {
        return (0..5).random().toDouble()
    }
}

class CreateOrderClientCommand(
    var dishes: List<Dish>,
    var local: Local,
    var paymentMethod: PaymentMethod,
    //var mailSender: MailSender,
    //var dishRepository: DishRepository
) : ClientCommand {
    override fun execute(client: Client) {
        client.createOrder(
            dishes,
            local,
            paymentMethod
        )
    }
}
