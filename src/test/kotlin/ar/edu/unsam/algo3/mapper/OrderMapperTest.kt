package ar.edu.unsam.algo3.mapper

import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.dto.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.uqbar.geodds.Point
import java.time.LocalTime

@DisplayName("Tests para OrderMapper")
class OrderMapperTest {

    private lateinit var client: Client
    private lateinit var local: Local
    private lateinit var dish1: Dish
    private lateinit var dish2: Dish
    private lateinit var delivery: Delivery
    private lateinit var paymentMethod: PaymentMethod
    private lateinit var order: Order

    @BeforeEach
    fun setup() {
        client = mock()
        whenever(client.firstname).thenReturn("Juan")
        whenever(client.username).thenReturn("juan123")
        whenever(client.address).thenReturn(Address("Calle Falsa", "123", Point(10.0, 20.0)))

        local = mock()
        whenever(local.name).thenReturn("Pizzeria")
        whenever(local.address).thenReturn(Address("Calle Falsa", "123", Point(10.0, 20.0)))
        whenever(local.getScore()).thenReturn(4.5)
        whenever(local.paymentMethods).thenReturn(mutableSetOf(PaymentMethod.EFECTIVO))

        dish1 = mock()
        whenever(dish1.id).thenReturn(1)
        whenever(dish1.name).thenReturn("Muzzarella")
        whenever(dish1.description).thenReturn("Pizza de muzzarella")
        whenever(dish1.imageURL).thenReturn("http://img.com/muzzarella.png")
        whenever(dish1.local).thenReturn(local)
        whenever(dish1.salePrice()).thenReturn(150.0)
        whenever(dish1.salePriceWithoutDiscount()).thenReturn(150.0)

        dish2 = mock()
        whenever(dish2.id).thenReturn(2)
        whenever(dish2.name).thenReturn("Napolitana")
        whenever(dish2.description).thenReturn("Pizza napolitana")
        whenever(dish2.imageURL).thenReturn("http://img.com/napolitana.png")
        whenever(dish2.local).thenReturn(local)
        whenever(dish2.salePrice()).thenReturn(200.0)
        whenever(dish2.salePriceWithoutDiscount()).thenReturn(200.0)

        paymentMethod = PaymentMethod.EFECTIVO

        order = Order(
            client = client,
            dishes = listOf(dish1, dish2, dish1),
            status = OrderStatus.PENDIENTE,
            local = local,
            delivery = null,
            paymentMethod = paymentMethod,
            pickupTime = LocalTime.of(19, 30)
        ).apply { id = 99 }
    }

    @Test
    fun `toOrderDTO deberia mapear correctamente todos los campos`() {
        val dto = OrderMapper.toOrderDTO(order)

        assertEquals(order.id, dto.id)
        assertEquals(order.status, dto.state)
        assertEquals(order.pickupTime, dto.time)
        assertEquals(order.client.firstname, dto.user.name)
        assertEquals(order.client.username, dto.user.username)
        assertEquals(order.paymentMethod, dto.paymentMethod)
        assertEquals(order.dishes.size, dto.quantity)
        assertEquals(3, dto.dishes.sumOf { it.quantity }, "Debe sumar cantidades correctamente")
        assertEquals(order.baseTotal(), dto.subtotal)
        assertEquals(order.getOrderTotal(), dto.total)
        assertEquals(order.deliveryCost(), dto.comission)
        assertEquals(order.applyTax(), dto.increment)

        // Chequeo de los detalles de platos
        val plate1DTO = dto.dishes.find { it.id == dish1.id }
        assertNotNull(plate1DTO)
        assertEquals(2, plate1DTO!!.quantity, "Cantidad de dish1 repetido debe ser 2")
        assertEquals(dish1.name, plate1DTO.name)

        val plate2DTO = dto.dishes.find { it.id == dish2.id }
        assertNotNull(plate2DTO)
        assertEquals(1, plate2DTO!!.quantity)
        assertEquals(dish2.name, plate2DTO.name)

        // Dirección
        assertEquals(order.client.address.street, dto.direction.street)
        assertEquals(order.client.address.number, dto.direction.number)
        assertEquals(order.client.address.coordinates.x, dto.direction.coordinates.x)
        assertEquals(order.client.address.coordinates.y, dto.direction.coordinates.y)
    }
}
