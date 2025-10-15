package ar.edu.unsam.algo3.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

interface OrderObserver {
    fun notify(order: Order)
}

class SendDishEmails(internal val mailSender: MailSender, private val dishRepository: DishRepository): OrderObserver {
    override fun notify(order: Order) {
        val repo = dishRepository
        val localDishes = repo.search("").filter { it.local == order.local }
        println(localDishes)
        val recommendedDishes = localDishes
            .filter { order.client.isDishSuitableForClient(it) }
            .shuffled()
        println(recommendedDishes)
        if (recommendedDishes.isNotEmpty()) {
            val messageContent = recommendedDishes.joinToString("\n") { "- ${it.name}" }
            val subject = "Recomendaciones en ${order.local.name}"
            val body = "Platos para ti:\n$messageContent"

            val mailSent = mailSender.sendEmail(
                order.client.email,
                subject,
                body
            )
            println(mailSent)
        }
    }
}

object LocalAuditorObject: OrderObserver {
    var localMap: Map<Local, LocalAuditor> = mapOf()
    private val compliantLocals = mutableSetOf<Local>()

    override fun notify(order: Order) {
        val auditor = localMap[order.local]
        auditor?.audit(order)
    }

    fun meetsGoal(local: Local): Boolean {
        return localMap[local]?.meetsGoal(local) ?: false
    }
}

class VeganOrderObserver: OrderObserver {
    override fun notify(order: Order) {
        if (order.isVegan()) {
            val client = order.client
            val currentType = client.clientType

            val newTypes = mutableListOf<ClientType>()
            newTypes.add(VeganClient())
            newTypes.add(currentType)

            client.clientType = ClientAllTypes(newTypes)
        }
    }
}

class CertifiedOrderObserver(): OrderObserver {
    override fun notify(order: Order) {
        if (order.isCertifiedOrder()) {
            val message = Message(
                LocalDateTime.now(),
                "¡Pedido Certificado! - Cliente Premium",
                """
                    Se ha recibido un pedido certificado que requiere atención prioritaria.
                    Cliente: ${order.client.firstName}
                    Antigüedad: ${ChronoUnit.YEARS.between(order.client.registrationDate, LocalDate.now())} años
                    Cantidad de platos: ${order.dishes.size}
                """.trimIndent()
            )

            order.local.inbox.addMessage(message)
        }
    }
}

interface LocalAuditor {
    fun audit(order: Order)
    fun meetsGoal(local: Local): Boolean
}

class ReachAmount(var minimumAmount: Double, var accumulatedAmount: Double = 0.0): LocalAuditor {
    override fun audit(order: Order) {
        accumulatedAmount += order.getOrderTotal()
    }

    override fun meetsGoal(local: Local): Boolean {
        return accumulatedAmount >= minimumAmount
    }
}

class ReachSales(var accumulatedSales: Int = 0, var salesGoal: Int = 5): LocalAuditor {
    override fun audit(order: Order) {
        if (order.dishes.size >= 3) {
            accumulatedSales += 1
        }
    }

    override fun meetsGoal(local: Local): Boolean {
        return accumulatedSales >= salesGoal
    }
}

class ReachGoal(val veganGoal: Int, var accumulated: Int = 0): LocalAuditor {
    override fun audit(order: Order) {
        order.dishes.forEach { dish ->
            if (dish.isVegan()) {
                accumulated += 1
            }
        }
    }

    override fun meetsGoal(local: Local): Boolean {
        return accumulated >= veganGoal
    }
}

class Combined(val auditors: List<LocalAuditor>): LocalAuditor {
    override fun audit(order: Order) {
        auditors.forEach { it.audit(order) }
    }

    override fun meetsGoal(local: Local): Boolean {
        return auditors.all { it.meetsGoal(local) }
    }
}
