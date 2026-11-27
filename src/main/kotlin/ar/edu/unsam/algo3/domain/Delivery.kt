package ar.edu.unsam.algo3.domain

import ar.edu.unsam.algo3.exceptions.InvalidOrderException
import ar.edu.unsam.algo3.exceptions.OrderStateException
import org.uqbar.geodds.Polygon // Se agrega dependencia

import java.time.LocalDate

class Delivery(
    firstName: String,
    lastName: String,
    username: String,
    password: String,
    birthDate: LocalDate,
    var address: Address, // objeto Address del aporte de Ulises
    var workZone: Polygon, // Se cambia por tipo de objeto Polygon
    val deliveryConditions: DeliveryCondition
) : User(firstName, lastName, username, password, birthDate) {

    fun takeOrder(order: Order): Boolean { // por qué devuelve un boolean?
        if (order.status != OrderStatus.PREPARADO) {
            println("El pedido no esta preparado")
            throw OrderStateException("El pedido no está preparado.")
        }

        val pickupInside = workZone.isInside(order.local.address.coordinates)
        val deliveryInside = workZone.isInside(order.client.address.coordinates)

        if (!pickupInside || !deliveryInside) {
            throw InvalidOrderException("El pedido no está dentro la zona de trabajo.")
        }

        if (!meetsConditions(order)) {
            throw InvalidOrderException("El pedido no cumple con las condiciones de entrega.")
        }

        return true
    }

    fun meetsConditions(order: Order): Boolean {
        return deliveryConditions.meetsCondition(order, this)
    }

    fun deliverOrder(order: Order) {
        order.markAsDelivered()
    }
}
