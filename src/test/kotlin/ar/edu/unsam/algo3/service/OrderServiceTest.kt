package ar.edu.unsam.algo3.service

import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.exceptions.BusinessException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*

@ExtendWith(MockitoExtension::class)
class OrderServiceTest {

    @Mock
    private lateinit var orderRepository: OrderRepository

    @InjectMocks
    private lateinit var orderService: OrderService

    private lateinit var order1: Order
    private lateinit var order2: Order

    @BeforeEach
    fun setup() {
        // Objetos reales para spy / mocks
        order1 = spy(
            Order(
                client = mock(),
                local = mock(),
                dishes = emptyList(),
                delivery = mock(),
                paymentMethod = PaymentMethod.EFECTIVO,
                status = OrderStatus.PENDIENTE
            )
        )
        order1.id = 1

        order2 = spy(
            Order(
                client = mock(),
                local = mock(),
                dishes = emptyList(),
                delivery = mock(),
                paymentMethod = PaymentMethod.EFECTIVO,
                status = OrderStatus.PREPARADO
            )
        )
        order2.id = 2
    }

    @Test
    fun `getAll deberia retornar todas las ordenes`() {
        whenever(orderRepository.getAll()).thenReturn(listOf(order1, order2))

        val result = orderService.getAll()

        assertEquals(listOf(order1, order2), result)
        verify(orderRepository).getAll()
    }

    @Test
    fun `getByState deberia retornar las ordenes filtradas por estado`() {
        whenever(orderRepository.search("PENDIENTE")).thenReturn(listOf(order1))

        val result = orderService.getByState("PENDIENTE")

        assertEquals(listOf(order1), result)
        verify(orderRepository).search("PENDIENTE")
    }

    @Test
    fun `getByState deberia lanzar BusinessException si el estado no existe`() {
        whenever(orderRepository.search("INEXISTENTE")).thenReturn(null)

        val exception = assertThrows<BusinessException> {
            orderService.getByState("INEXISTENTE")
        }

        assertEquals("El estado solicitado no existe.", exception.message)
        verify(orderRepository).search("INEXISTENTE")
    }


    @Test
    fun `getByID deberia retornar una orden existente`() {
        whenever(orderRepository.getById(1)).thenReturn(order1)

        val result = orderService.getByID(1)

        assertEquals(order1, result)
        verify(orderRepository).getById(1)
    }

    @Test
    fun `getByID deberia lanzar BusinessException si la orden no existe`() {
        whenever(orderRepository.getById(99)).thenReturn(null)

        val exception = assertThrows<BusinessException> {
            orderService.getByID(99)
        }

        assertEquals("El pedido solicitado no existe.", exception.message)
        verify(orderRepository).getById(99)
    }

    @Test
    fun `updateOrderState deberia actualizar PENDIENTE a PREPARADO`() {
        whenever(orderRepository.getById(1)).thenReturn(order1)

        val updatedOrder = orderService.updateOrderState(1)

        assertEquals(OrderStatus.PREPARADO, updatedOrder.status)
        verify(orderRepository).update(order1)
    }

    @Test
    fun `updateOrderState deberia actualizar PREPARADO a ENTREGADO`() {
        whenever(orderRepository.getById(2)).thenReturn(order2)

        val updatedOrder = orderService.updateOrderState(2)

        assertEquals(OrderStatus.ENTREGADO, updatedOrder.status)
        verify(orderRepository).update(order2)
    }

    @Test
    fun `updateOrderState deberia lanzar BusinessException si no puede actualizar`() {
        val order3 = spy(
            Order(
                client = mock(),
                local = mock(),
                dishes = emptyList(),
                delivery = mock(),
                paymentMethod = PaymentMethod.EFECTIVO,
                status = OrderStatus.ENTREGADO
            )
        )
        order3.id = 3
        whenever(orderRepository.getById(3)).thenReturn(order3)

        val exception = assertThrows<BusinessException> {
            orderService.updateOrderState(3)
        }

        assertEquals("No se puede actualizar el estado de este pedido", exception.message)
        verify(orderRepository, never()).update(order3)
    }

    @Test
    fun `deleteOrder deberia eliminar una orden existente`() {
        whenever(orderRepository.getById(1)).thenReturn(order1)

        orderService.deleteOrder(1)

        verify(orderRepository).delete(order1)
    }

    @Test
    fun `deleteOrder deberia lanzar BusinessException si la orden no existe`() {
        whenever(orderRepository.getById(99)).thenReturn(null)

        val exception = assertThrows<BusinessException> {
            orderService.deleteOrder(99)
        }

        assertEquals("El pedido que intentás eliminar no existe.", exception.message)
        verify(orderRepository).getById(99)
    }
}
