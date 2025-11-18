package ar.edu.unsam.algo3.domain

import ar.edu.unsam.algo3.exceptions.RatingException
import org.uqbar.geodds.Point
import java.time.LocalDate

class Client(
    firstName: String = "",
    lastName: String = "",
    username: String = "",
    password: String = "",
    registrationDate: LocalDate = LocalDate.now(),
    email: String = "",
    var address: Address = Address("", "", Point(0, 0)),
    val location : String = "",
    val favoriteIngredients: MutableSet<Ingredient> = mutableSetOf(),
    val ingredientsToAvoid: MutableSet<Ingredient> = mutableSetOf(),
    var nearbyDistance: Double = 5.0,
    var clientType: ClientType = NormalClient(),
    val pendingRatings: MutableSet<PendingRating> = mutableSetOf()
) : User(firstName, lastName, username, password, registrationDate, email) {

    var commands = mutableListOf<ClientCommand>()

    fun createOrder(
        dishes: List<Dish>,
        local: Local,
        paymentMethod: PaymentMethod
    ) {
        Order.Companion.createOrder(
            this,
            dishes,
            local,
            paymentMethod
        )
    }

    fun addFavoriteIngredient(ingredient: Ingredient) {
        if (ingredientsToAvoid.contains(ingredient)) {
            println("Error: El ingrediente ${ingredient.name} está en la lista de ingredientes a evitar.")
        } else favoriteIngredients.add(ingredient)
    }

    fun addIngredientToAvoid(ingredient: Ingredient) {
        if (favoriteIngredients.contains(ingredient)) {
            println("Error: El ingrediente ${ingredient.name} está en la lista de ingredientes favoritos.")
        } else ingredientsToAvoid.add(ingredient)
    }

    fun isDishSuitableForClient(dish: Dish): Boolean {
        return clientType.isDishSuitable(dish, this) && validateIngredients(dish)
    }

    fun createRating(pendingRating: PendingRating, value: Double) {
        if (!pendingRating.canRate()) {
            throw RatingException("No se puede puntuar este local. La fecha límite ha expirado.")
        }
        pendingRating.local.createRating(this, value)
        println("Se calificó el local exitosamente.")
    }

    fun rate(local: Local, rating: Double) {
        val pendingRating = pendingRatings.find { it.local == local }

        if (pendingRating == null) {
            throw RatingException("No se encontró ninguna valoración pendiente.")
        }

        createRating(pendingRating, rating)
        removePendingRating(pendingRating)
    }

    fun removePendingRating(pendingRating: PendingRating) {
        this.pendingRatings.remove(pendingRating)
    }

    fun confirmOrder(order: Order) {
        val local = order.local
        val existingRating = pendingRatings.find { it.local == local }

        if (existingRating != null) {
            // Si existe, actualizamos la fecha del último pedido
            existingRating.lastOrderDate = LocalDate.now()
        } else {
            // Si no existe, creamos una nueva valoración
            val newRating = PendingRating(local)
            pendingRatings.add(newRating)
        }
    }

    fun distanceTo(local: Local): Double {
        return this.address.coordinates.distance(local.address.coordinates)
    }

    fun isLocalNearby(local: Local): Boolean = distanceTo(local) <= nearbyDistance

    fun validateIngredients(dish: Dish): Boolean {
        return dish.ingredients.none { it in ingredientsToAvoid }
    }

    fun executeCommands() {
        commands.forEach { command -> command.execute(this) }
    }

    fun addCommand(command: ClientCommand) {
        commands.add(command)
    }
}

// Interfaz principal para definir tipos de cliente
interface ClientType {
    fun isDishSuitable(dish: Dish, client: Client): Boolean
}

// Marca opcional para identificar composites de ClientType
interface CompositeClientType : ClientType

// Composite AND: el plato debe ser adecuado según todos los subtipos
class ClientAllTypes(
    val types: List<ClientType>
) : CompositeClientType {
    override fun isDishSuitable(dish: Dish, client: Client): Boolean =
        types.all { it.isDishSuitable(dish, client) }
}

// Implementaciones concretas de ClientType
class NormalClient : ClientType {
    override fun isDishSuitable(dish: Dish, client: Client): Boolean = true
}

class VeganClient : ClientType {
    override fun isDishSuitable(dish: Dish, client: Client): Boolean =
        !dish.ingredients.any { it.group.isAnimalOrigin }
}

class PickyClient : ClientType {
    override fun isDishSuitable(dish: Dish, client: Client): Boolean =
        dish.isAuthorDish()
}

class ConservativeClient : ClientType {
    override fun isDishSuitable(dish: Dish, client: Client): Boolean =
        dish.ingredients.all { it in client.favoriteIngredients }
}

class LoyalClient(private val preferredLocals: Set<Local>) : ClientType {
    override fun isDishSuitable(dish: Dish, client: Client): Boolean =
        dish.local in preferredLocals
}

class MarketingClient(private val keywords: Set<String>) : ClientType {
    override fun isDishSuitable(dish: Dish, client: Client): Boolean =
        keywords.any { key ->
            dish.description.contains(key, ignoreCase = true)
        }
}

class ImpatientClient : ClientType {
    override fun isDishSuitable(dish: Dish, client: Client): Boolean =
        client.isLocalNearby(dish.local)
}

class OddClient : ClientType {
    private val picky = PickyClient()
    private val conservative = ConservativeClient()

    override fun isDishSuitable(dish: Dish, client: Client): Boolean {
        val typeToUse: ClientType =
            if (client.age % 2 == 0) picky
            else conservative
        return typeToUse.isDishSuitable(dish, client)
    }
}
