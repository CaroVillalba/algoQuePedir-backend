package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.domain.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Dado un OrderController")
class OrderControllerTest {

    private lateinit var client: Client
    private lateinit var local: Local
    private lateinit var dish: Dish
    private lateinit var order: Order

    @Autowired
    lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        client = Client("Juan", "Pérez", "juanp", "234556", LocalDate.now().minusYears(2))
        val address = Address("Calle Falsa", "123", org.uqbar.geodds.Point(10.0, 20.0))
        local = Local("Local Test", "http://img.com/logo.png", address, 10.0, 5.0, mutableSetOf(PaymentMethod.EFECTIVO))
        dish = Dish("http://img.com/pizza.png", "Pizza", "Pizza test", listOf(), local, 10.0, LocalDate.now(), 5.0, 200.0, 0.0)
        order = Order(client, listOf(dish), OrderStatus.PENDIENTE, local, null, PaymentMethod.EFECTIVO)
    }

    @Test
    fun `GET v1 order sin state debe devolver 200 OK`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/order"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `GET v1 order con state PENDIENTE debe devolver 200 OK`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/order")
            .param("state", "PENDIENTE"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `GET v1 order orderDetail debe devolver 200 OK`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/order/orderDetail/1"))
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
}
