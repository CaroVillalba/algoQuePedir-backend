package ar.edu.unsam.algo3.bootstrap

import ar.edu.unsam.algo3.domain.*
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import org.uqbar.geodds.Point
import org.uqbar.geodds.Polygon
import java.time.LocalDate
import java.time.LocalTime

@Component
class DataInitializer(
    private val localRepository: LocalRepository,
    private val dishRepository: DishRepository,
    private val orderRepository: OrderRepository,
    private val ingredientRepository: IngredientRepository,
    private val clientRepository: ClientRepository
) {

    @PostConstruct
    fun init() {
        val restaurant = Local(
            "La Buena Mesa",
            "https://www.hilton.com/im/en/BUEPIHH/19783367/mural-2.jpg?impolicy=crop&cw=3857&ch=3000&gravity=NorthWest&xposition=71&yposition=0&rw=684&rh=532",
            Address("Av. del Gourmet", "1250", Point(-31.32, -66.95)),
            25.0,
            12.5,
            mutableSetOf(PaymentMethod.EFECTIVO, PaymentMethod.QR, PaymentMethod.TRANSFERENCIA),
            mutableSetOf()
        )
        localRepository.create(restaurant)

        // Platos de ejemplo adicionales
        val ingredient1 = Ingredient("Lechuga", 5.0, FoodGroup.FRUTAS_Y_VERDURAS)
        val ingredient2 = Ingredient("Tomate", 3.0, FoodGroup.FRUTAS_Y_VERDURAS)

        ingredientRepository.create(ingredient1)
        ingredientRepository.create(ingredient2)

        val dish1 = Dish(
            "https://www.cocina-chilena.com/base/stock/Recipe/ensalada-de-lechuga-y-tomate/ensalada-de-lechuga-y-tomate_web.jpg.webp",
            "Ensalada de Tomate y Lechuga",
            "Ensalada clásica de tomate y lechuga",
            mutableListOf(ingredient1, ingredient2),
            restaurant,
            1.0,
            LocalDate.now(),
            0.0,
            10.0,
            1.0,
        )
        val dish2 = Dish(
            "https://www.robinfoodtv.com/documentos/recetas/694.jpg",
            "Ensalada de Lechuga",
            "Ensalada clásica de lechuga",
            mutableListOf(ingredient1),
            restaurant,
            1.0,
            LocalDate.now(),
            0.0,
            10.0,
            1.0,
        )
        val dish3 = Dish(
            "https://micocinareal.com/wp-content/uploads/ensalada-tomate-alino-tomillo.jpeg",
            "Ensalada de Tomate",
            "Ensalada clásica de tomate",
            mutableListOf(ingredient2),
            restaurant,
            1.0,
            LocalDate.now(),
            0.0,
            10.0,
            1.0,
        )
        dishRepository.create(dish1)
        dishRepository.create(dish2)
        dishRepository.create(dish3)

        // ----------------------- Carga de Clientes ---------------------------
        val addressW = Address("San Andrés", "334", Point(-5.0, 5.0))
        val addressH = Address("Hinamizawa", "34", Point(-53.4430, 25.220))

        val client1 = Client("Alejandra Pisco", username = "piscosour").apply { address = addressW }
        val client2 = Client("Pedro Aznar", username = "cigarra").apply { address = addressW }
        val client3 = Client("Yu Tsun", username = "senderosbifurcados").apply { address = addressH }
        val client4 = Client("Paul Valery", username = "omniscent").apply { address = addressH }
        val client5 = Client("Arnold Stallone", username = "termirambo").apply { address = addressW }

        val admin = Client(
            "Admin",
            "Admin",
            "admin",
            "admin",
            LocalDate.of(1990,1,1),
            LocalDate.now().minusDays(17),
            "ola123@gmail.com",
            Address("Calle Falsa", "123", Point(-34.6, -58.45))
        )

        listOf(client1, client2, client3, client4, client5, admin).forEach { clientRepository.create(it) }

        // ----------------------- Carga de Pedidos ----------------------------
        val points = listOf(Point(0.0,0.0), Point(0.0,10.0), Point(10.0,10.0), Point(10.0,0.0))
        val workZone = Polygon(points)
        val condition = AnyCondition(
            listOf(
                MinimumAmountCondition(50.0),
                TimeRangeCondition(LocalTime.of(10,0), LocalTime.of(22,0))
            )
        )
        val delivery = Delivery(
            "Delivery", "pipo", "pipon", "pepin", LocalDate.now(), addressW, workZone, condition
        )

        val order1 = Order(client2, listOf(dish1, dish2, dish3, dish3), OrderStatus.PENDIENTE, restaurant, delivery, PaymentMethod.QR)
        val order2 = Order(client3, listOf(dish1, dish2, dish3), OrderStatus.PENDIENTE, restaurant, delivery, PaymentMethod.EFECTIVO)
        val order3 = Order(client1, listOf(dish1, dish2, dish3), OrderStatus.PENDIENTE, restaurant, delivery, PaymentMethod.TRANSFERENCIA)
        val order4 = Order(client4, listOf(dish1, dish2, dish3), OrderStatus.PENDIENTE, restaurant, delivery, PaymentMethod.TRANSFERENCIA)
        val order5 = Order(client5, listOf(dish1, dish2, dish3), OrderStatus.PENDIENTE, restaurant, delivery, PaymentMethod.CREDITO)

        order4.status = OrderStatus.PREPARADO
        order2.status = OrderStatus.ENTREGADO
        order5.status = OrderStatus.CANCELADO

        listOf(order1, order2, order3, order4, order5).forEach {
            it.pickupTime = LocalTime.now()
            orderRepository.create(it)
        }
    }
}
