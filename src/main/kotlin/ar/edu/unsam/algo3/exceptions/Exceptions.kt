package ar.edu.unsam.algo3.exceptions

class InvalidOrderException(override var message: String) : Exception() {
    /* Este Exception es para cuando el pedido usado no es aceptable */
}

class OrderStateException(override var message: String) : Exception() {
    /* Este Exception es para cuando el pedido esta en un estado en el cual no deberia*/
}

class InvalidDishException(override var message: String) : Exception()
class InvalidPaymentMethodException(override var message: String) : Exception()
class OrderAssignmentException(override var message: String) : Exception()
class RatingException(override var message: String) : Exception()
class CouponNotApplicableException(override var message: String) : Exception()

class BusinessException(override var message: String) : Exception()

class ElementDoesNotExistException(override var message: String) : Exception()
