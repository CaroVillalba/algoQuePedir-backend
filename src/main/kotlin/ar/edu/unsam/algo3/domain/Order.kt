package ar.edu.unsam.algo3.domain

import ar.edu.unsam.algo3.exceptions.OrderAssignmentException
import ar.edu.unsam.algo3.exceptions.InvalidPaymentMethodException
import ar.edu.unsam.algo3.exceptions.OrderStateException
import ar.edu.unsam.algo3.exceptions.InvalidDishException
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.math.RoundingMode

enum class OrderStatus {
    PENDIENTE,
    PREPARADO,
    ENTREGADO,
    CANCELADO
}

object GlobalMailSender: AuditMailSender()
object GlobalRepository: DishRepository()

class Order(
    val client: Client,
    val dishes: List<Dish>,
    var status: OrderStatus = OrderStatus.PENDIENTE,
    var local: Local,
    var delivery: Delivery?, /*ATENCION!!: CAMBIO QUE HICE, ponerle null. Sino, no termino más de probar esto. */
    var paymentMethod: PaymentMethod,
    var pickupTime: LocalTime? = null,
    var appliedCoupon: Coupon = BaseCoupon()
) : Entity() {

    var observers: MutableList<OrderObserver> = mutableListOf(
        LocalAuditorObject,
        VeganOrderObserver(),
        CertifiedOrderObserver()
    )

    companion object {
        fun createOrder(
            client: Client,
            dishes: List<Dish>,
            local: Local,
            paymentMethod: PaymentMethod
        ): Order {
            val dishRepository = GlobalRepository
            val mailSender = GlobalMailSender
            var order = Order(client, dishes, OrderStatus.PENDIENTE, local, null, paymentMethod)
            order.observers.add(SendDishEmails(mailSender, dishRepository))
            order.validateDishes()
            order.validatePaymentMethod()
            return order
        }
    }

    private fun validateDishes() {
        dishes.forEach {
            if (it.local != local) {
                throw InvalidDishException("El plato ${it.name} no pertenece al local.")
            }

            if (!client.isDishSuitableForClient(it)) {
                throw InvalidDishException("El plato ${it.name} contiene ingrediente(s) que el cliente evita.")
            }
        }
    }

    private fun validatePaymentMethod() {
        if (!local.paymentMethods.contains(paymentMethod)) {
            throw InvalidPaymentMethodException("El medio de pago $paymentMethod no es válido para este local.")
        }
    }

    fun isVegan(): Boolean {
        return dishes.all { it.isVegan() }
    }

    fun assignDelivery(delivery: Delivery) {
        if(!delivery.takeOrder(this)) {
            throw OrderAssignmentException("No se pudo asignar el pedido al delivery.")
        }
        this.delivery = delivery
    }

    fun isCertifiedOrder(): Boolean {
        val today = LocalDate.now()
        val accountAgeYears =
            ChronoUnit.YEARS.between(client.registrationDate, today) // usé ChronoUnit para más control sobre el año
        val localScore = local.getScore()
        return accountAgeYears >= 1 && localScore in 4.0..5.0
    }

    fun markAsPrepared() {
        if (status != OrderStatus.PREPARADO) {
            status = OrderStatus.PREPARADO
        } else {
            throw OrderStateException("El estado ya estaba preparado")
        }
    }

    fun markAsDelivered() {
        validateDishes()
        validatePaymentMethod()
        validateDeliverableStatus()
        client.confirmOrder(this)
    }

    fun validateDeliverableStatus() {
        if (status != OrderStatus.ENTREGADO && status == OrderStatus.PREPARADO) {
            status = OrderStatus.ENTREGADO
        } else {
            throw OrderStateException("No se puede entregar un pedido no preparado o el pedido ya estaba entregado")
        }
    }

    fun getOrderTotal(): Double {
        val dishCost = dishes.sumOf { it.salePrice() }
        val dishCostWithoutDiscounts = dishes.sumOf { it.salePriceWithoutDiscount() }
        val deliveryCost = dishCostWithoutDiscounts * 0.1
        val baseTotal = dishCost + deliveryCost
        return (baseTotal * paymentMethod.tax()).toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toDouble()
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    fun dishesCost(): Double =
        dishes.sumOf { it.salePrice() }

    fun dishesCostWithoutDiscount(): Double =
        dishes.sumOf { it.salePriceWithoutDiscount() }

    fun deliveryCost(): Double =
        dishesCostWithoutDiscount() * 0.1

    fun baseTotal(): Double =
        dishesCost() + deliveryCost()

    fun applyTax(): Double =
        this.baseTotal() * paymentMethod.tax()

    fun round(value: Double): Double =
        value.toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toDouble()

    fun totalDishes(): Int = dishes.count()
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    fun getOrderTotalWithCoupon(coupon: Coupon): Double {
        return (getOrderTotal() - coupon.calculateTotalDiscount(this)).toBigDecimal().setScale(2, RoundingMode.HALF_EVEN).toDouble()
    }

    fun applyDiscount(coupon: Coupon) {
        coupon.apply(this) // detonante de si puede ser o no
        this.appliedCoupon = coupon
    }

    fun hasAppliedCoupon(coupon: Coupon): Boolean = getOrderTotalWithCoupon(coupon) != getOrderTotal()
    fun notifyObservers() = observers.forEach { it.notify(this) }
}
