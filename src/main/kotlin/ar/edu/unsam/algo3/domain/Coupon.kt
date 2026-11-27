package ar.edu.unsam.algo3.domain

import ar.edu.unsam.algo3.exceptions.CouponNotApplicableException
import java.time.DayOfWeek
import java.time.LocalDate

abstract class Coupon(
    val issuedAt: LocalDate,
    var durationDays: Int,
    var baseDiscountRate: Double,
    var wasApplied: Boolean
) : Entity() {

    init {
        require(baseDiscountRate in 0.0..1.0) {
            "The base discount rate must be between 0 and 1"
        }
    }

    fun apply(order: Order) {
        if (validateApplicability(order)) {
            wasApplied = true
        } else {
            throw CouponNotApplicableException("The coupon cannot be applied to this order")
        }
    }

    fun hasGeneralRestrictions(order: Order): Boolean {
        return isExpired() || wasApplied || calculateTotalDiscount(order) > order.getOrderTotal()
    }

    fun validateApplicability(order: Order): Boolean {
        return !hasGeneralRestrictions(order) && isSpecificallyApplicable(order)
    }

    fun calculateTotalDiscount(order: Order): Double {
        return baseDiscount(order) + calculateSpecificDiscount(order)
    }

    fun baseDiscount(order: Order): Double {
        return order.getOrderTotal() * baseDiscountRate
    }

    fun isExpired(): Boolean {
        val expirationDate = issuedAt.plusDays(durationDays.toLong())
        return LocalDate.now().isAfter(expirationDate)
    }

    abstract fun isSpecificallyApplicable(order: Order): Boolean
    abstract fun calculateSpecificDiscount(order: Order): Double
}

class SpecificDayCoupon(
    issuedAt: LocalDate,
    durationDays: Int,
    baseDiscountRate: Double,
    wasApplied: Boolean,
    var dayOfWeek: DayOfWeek
) : Coupon(issuedAt, durationDays, baseDiscountRate, wasApplied) {

    override fun calculateSpecificDiscount(order: Order): Double {
        val anyDishReleasedOnDay = order.dishes.any { it.releaseDate.dayOfWeek == dayOfWeek }
        val extraRate = if (anyDishReleasedOnDay) 0.10 else 0.05
        return order.getOrderTotal() * extraRate
    }

    override fun isSpecificallyApplicable(order: Order): Boolean {
        return LocalDate.now().dayOfWeek == dayOfWeek
    }
}

class SpecificLocalCoupon(
    issuedAt: LocalDate,
    durationDays: Int,
    baseDiscountRate: Double,
    wasApplied: Boolean,
    private val specificLocals: List<Local>
) : Coupon(issuedAt, durationDays, baseDiscountRate, wasApplied) {

    override fun calculateSpecificDiscount(order: Order): Double {
        return if (order.isCertifiedOrder()) 1000.0 else 500.0
    }

    override fun isSpecificallyApplicable(order: Order): Boolean {
        return order.local in specificLocals
    }
}

class MaxCapCoupon(
    issuedAt: LocalDate,
    durationDays: Int,
    baseDiscountRate: Double,
    wasApplied: Boolean,
    var maxCap: Double,
    private val extraDiscountRate: Double
) : Coupon(issuedAt, durationDays, baseDiscountRate, wasApplied) {

    init {
        require(maxCap > 0.0) {
            "The maximum cap must be greater than 0"
        }
    }

    override fun calculateSpecificDiscount(order: Order): Double {
        val discountCalculated = order.getOrderTotal() * extraDiscountRate
        return minOf(discountCalculated, maxCap)
    }

    override fun isSpecificallyApplicable(order: Order): Boolean = true
}

class BaseCoupon : Coupon(
    issuedAt = LocalDate.now(),
    durationDays = 1,
    baseDiscountRate = 0.0,
    wasApplied = false
) {
    override fun isSpecificallyApplicable(order: Order): Boolean = true
    override fun calculateSpecificDiscount(order: Order): Double = 0.0
}
