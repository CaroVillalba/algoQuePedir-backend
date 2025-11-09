package ar.edu.unsam.algo3.domain

class Local(
    var name: String,
    var imgURL: String = "",
    var address: Address,
    var appCommission: Double = 0.0,
    var authorCommission: Double = 0.0,
    var paymentMethods: MutableSet<PaymentMethod>,
    var ratings: MutableSet<Rating> = mutableSetOf(),
    var orders: MutableSet<Order> = mutableSetOf(),
    val inbox: Inbox = Inbox()
) : Entity() {

    fun isReliable(): Boolean = getScore() >= 4.0

    fun getScore(): Double {
        return if (ratings.isNotEmpty()) ratings.map { it.score }.average() else 0.0
    }

    fun createRating(client: Client, score: Double) {
        val rating = Rating(client, this, score)
        ratings.add(rating)
    }
}
