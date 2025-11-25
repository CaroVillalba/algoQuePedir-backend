package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.service.OrderService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDate

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Dado un OrderController")
class OrderControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var orderRepository: OrderRepository

    @Autowired
    lateinit var clientRepository: ClientRepository

    @Autowired
    lateinit var localRepository: LocalRepository

    @Autowired
    lateinit var dishRepository: DishRepository

    @Autowired
    lateinit var orderService: OrderService

    private lateinit var client: Client
    private lateinit var local: Local
    private lateinit var dish: Dish
    private lateinit var order: Order

    @BeforeEach
    fun setup() {
        client = Client("Juan", "Pérez", "juanp", "234556", LocalDate.now().minusYears(2))
        val address = Address("Calle Falsa", "123", org.uqbar.geodds.Point(10.0, 20.0))
        local = Local("Local Test", "http://img.com/logo.png", address, 10.0, 5.0, mutableSetOf(PaymentMethod.EFECTIVO))
        dish = Dish("http://img.com/pizza.png", "Pizza", "Pizza test", listOf(), local, 10.0, LocalDate.now(), 5.0, 200.0, 0.0)
        order = Order(client, listOf(dish), OrderStatus.PENDIENTE, local, null, PaymentMethod.EFECTIVO)

        // Capturamos el ID asignado por create
        orderRepository.create(order)
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
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/order/${order.id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(order.id))
    }

    @Test
    fun `PATCH v1 order {orderId} debe avanzar el estado y devolver 200 OK`() {
        mockMvc.perform(MockMvcRequestBuilders.patch("/v1/order/${order.id}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.state").value("PREPARADO"))
    }

    @Test
    fun `DELETE v1 order {orderId} debe eliminar el pedido y devolver 200 OK`() {
        mockMvc.perform(MockMvcRequestBuilders.delete("/v1/order/${order.id}"))
            .andExpect(MockMvcResultMatchers.status().isOk)

        // Comprobar que realmente se eliminó
        val deleted = orderRepository.getById(order.id)
        assert(deleted == null)
    }

    @Test
    fun `GET v1 order {orderId} debe devolver 200 OK y el pedido correcto`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/v1/order/${order.id}")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(order.id!!))
    }

    @Test
    fun `GET v1 order con state Todos debe devolver 200 OK`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/v1/order")
                .param("state", "Todos")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `POST v1 order debe crear un pedido y devolver 200 OK`() {
        clientRepository.create(client)
        localRepository.create(local)
        dishRepository.create(dish)

        val dtoJson = """
        {
          "restaurantId": ${local.id},
          "items": [
            {
              "dishId": ${dish.id},
              "quantity": 1
            }
          ],
          "paymentMethod": "EFECTIVO"
        }
    """.trimIndent()

        mockMvc.perform(
            MockMvcRequestBuilders.post("/v1/order")
                .param("clientId", client.id!!.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(dtoJson)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)

    }




}
