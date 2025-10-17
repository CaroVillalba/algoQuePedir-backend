package ar.edu.unsam.algo3.bootstrap

import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.dto.PlatoListDTO
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import org.uqbar.geodds.Point
import org.uqbar.geodds.Polygon
import java.io.File
import java.time.LocalDate
import java.time.LocalTime

@Component
class DataInitializer(
    private val localRepository: LocalRepository,
    private val platoRepository: DishRepository,
    private val pedidoRepository: OrderRepository,
    private val ingredienteRepository: IngredientRepository,
    private val clienteRepository: ClientRepository
) {
    private val mapper = jacksonObjectMapper()

    @PostConstruct
    fun init() {
        // ----------------------- Carga de Ingredientes -----------------------
        val fileIngredients = File("src/main/resources/ingredientsData.json")
        val ingredients = mapper.readValue<List<Ingredient>>(fileIngredients)
        ingredients.forEach { ingredienteRepository.create(it) }



        val restaurante = Local(
            "La Buena Mesa",
            "https://www.hilton.com/im/en/BUEPIHH/19783367/mural-2.jpg?impolicy=crop&cw=3857&ch=3000&gravity=NorthWest&xposition=71&yposition=0&rw=684&rh=532",
            Address("Av. del Gourmet", "1250", Point(-31.32, -66.95)),
            25.0,
            12.5,
            mutableSetOf(PaymentMethod.EFECTIVO, PaymentMethod.QR, PaymentMethod.TRANSFERENCIA),
            mutableSetOf()
        )
        localRepository.create(restaurante)



        // Platos de ejemplo adicionales
        val ingredient1 = Ingredient("lechuga", 5.0, FoodGroup.FRUTAS_Y_VERDURAS)
        val ingredient2 = Ingredient("tomate", 3.0, FoodGroup.FRUTAS_Y_VERDURAS)

        ingredienteRepository.create(ingredient1)
        ingredienteRepository.create(ingredient2)

        val dish1 = Dish(
            "https://picsum.photos/id/237/400/300",
            "Ensalada de Tomate y lechuga",
            "Ensalada clásica de tomate y lechuga",
            mutableListOf(ingredient1, ingredient2),
            restaurante,
            1.0,
            LocalDate.now(),
            0.0,
            10.0,
            1.0,
        )
        val dish2 = Dish(
            "https://picsum.photos/id/237/400/300",
            "Ensalada de lechuga",
            "Ensalada clásica de lechuga",
            mutableListOf(ingredient1),
            restaurante,
            1.0,
            LocalDate.now(),
            0.0,
            10.0,
            1.0,
        )
        val dish3 = Dish(
            "https://picsum.photos/id/237/400/300",
            "Ensalada de Tomate",
            "Ensalada clásica de tomate",
            mutableListOf(ingredient2),
            restaurante,
            1.0,
            LocalDate.now(),
            0.0,
            10.0,
            1.0,
        )
        platoRepository.create(dish1)
        platoRepository.create(dish2)
        platoRepository.create(dish3)

        // ----------------------- Carga de Clientes ---------------------------
        val addressW = Address("San Andres", "334", Point(-5.0, 5.0))
        val addressH = Address("Hinamizawa", "34", Point(-53.4430, 25.220))

        val client1 = Client("Alejandra Pisco", username = "piscosour").apply { address = addressW }
        val client2 = Client("Pedro Aznar", username = "cigarra").apply { address = addressW }
        val client3 = Client("Yu Tsun", username = "senderosbifurcados").apply { address = addressH }
        val client4 = Client("Paul Valery", username = "omniscent").apply { address = addressH }

        val admin = Client(
            "Admin",
            "Admin",
            username = "admin",
            password = "admin",
            LocalDate.of(1990,1,1),
            email = "ola123@gmail.com",
            address = Address("Calle Falsa", "123", Point(-34.6, -58.45))
        )

        listOf(client1, client2, client3, client4, admin).forEach { clienteRepository.create(it) }
        // ----------------------- Carga de Pedidos ----------------------------
        val puntos = listOf(Point(0.0,0.0), Point(0.0,10.0), Point(10.0,10.0), Point(10.0,0.0))
        val zonaTrabajo = Polygon(puntos)
        val condicion = AnyCondition(
            listOf(
                MinimumAmountCondition(50.0),
                TimeRangeCondition(LocalTime.of(10,0), LocalTime.of(22,0))
            )
        )
        val deliveri = Delivery(
            "Deliveri", "pipo", "pipon", "pepin", LocalDate.now(), addressW, zonaTrabajo, condicion
        )

        val order1 = Order(client2, listOf(dish1, dish2, dish3), OrderStatus.PENDIENTE, restaurante, deliveri, PaymentMethod.QR)
        val order2 = Order(client3, listOf(dish1, dish2, dish3), OrderStatus.PENDIENTE, restaurante, deliveri, PaymentMethod.EFECTIVO)
        val order3 = Order(client1, listOf(dish1, dish2, dish3), OrderStatus.PENDIENTE, restaurante, deliveri, PaymentMethod.TRANSFERENCIA)
        val order4 = Order(client4, listOf(dish1, dish2, dish3), OrderStatus.PENDIENTE, restaurante, deliveri, PaymentMethod.TRANSFERENCIA)

        order4.status = OrderStatus.PREPARADO
        order2.status = OrderStatus.ENTREGADO

        listOf(order1, order2, order3, order4).forEach {
            it.pickupTime = LocalTime.now()
            pedidoRepository.create(it)
        }
    }
}
