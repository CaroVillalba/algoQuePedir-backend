package ar.edu.unsam.algo3.domain

import java.time.LocalTime

interface DeliveryCondition {
    fun meetsCondition(order: Order, delivery: Delivery): Boolean
}

class TimeRangeCondition(private val startTime: LocalTime, private val endTime: LocalTime) : DeliveryCondition {
    override fun meetsCondition(order: Order, delivery: Delivery): Boolean {
        val pickupTime = order.pickupTime
        return pickupTime != null && pickupTime.isAfter(startTime) && pickupTime.isBefore(endTime)
    }
}

class MinimumAmountCondition(private val minimumAmount: Double) : DeliveryCondition {
    override fun meetsCondition(order: Order, delivery: Delivery): Boolean {
        return order.getOrderTotal() >= minimumAmount
    }
}

class SpecificLocalCondition(private val localList: MutableList<Local>) : DeliveryCondition {  //Se cambia la condicion para que no sea parametro la lista de locales
    override fun meetsCondition(order: Order, delivery: Delivery): Boolean {
        return localList.contains(order.local)
    }
}

class CertifiedOrderCondition() : DeliveryCondition {
    override fun meetsCondition(order: Order, delivery: Delivery): Boolean {
        return order.isCertifiedOrder()
    }
}

//OPCION 1:

//Una condicion con una lista de condiciones, le aplica un or ||
class AnyCondition(val conditionList: List<DeliveryCondition>) : DeliveryCondition {
    override fun meetsCondition(order: Order, delivery: Delivery): Boolean {
        return conditionList.any { it.meetsCondition(order, delivery) }
    }
}

//Una condicion con una lista de condiciones, le aplica un or ||
class AllCondition(val conditionList: List<DeliveryCondition>) : DeliveryCondition {
    override fun meetsCondition(order: Order, delivery: Delivery): Boolean {
        return conditionList.all { it.meetsCondition(order, delivery) }
    }
}

/*
OPCION 2:

//Una condicion con una lista de condiciones, le aplica un and &&
class MixedDeliveryCondition(
    val conditionList: List<DeliveryCondition>,
    val searchType: ConditionType
) : DeliveryCondition {
    override fun meetsCondition(order: Order, delivery: Delivery): Boolean {
        return if (searchType == ConditionType.ALL) {
            conditionList.all { it.meetsCondition(order, delivery) }
        } else {
            conditionList.any { it.meetsCondition(order, delivery) }
        }
    }
}
*/
