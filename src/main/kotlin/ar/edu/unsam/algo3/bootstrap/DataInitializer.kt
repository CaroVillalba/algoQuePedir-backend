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

        // ----------------------- LOCALES -----------------------------
        val local1 = Local(
            "El Ferroviario",
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSx-7q7poRQlBrhkPHcDqdFvts9QJDKX1-HwQ&s",
            Address("Av. San Martín", "1234", Point(-31.32, -66.95)),
            25.0,
            12.5,
            mutableSetOf(PaymentMethod.EFECTIVO, PaymentMethod.QR)
        )
        localRepository.create(local1)

        val local2 = Local(
            "Restorán La Canchita",
            "https://cdn0.casamientos.com.ar/vendor/2289/3_2/640/png/1005-1.jpeg",
            Address("Belgrano", "456", Point(-31.30, -66.90)),
            20.0,
            10.0,
            mutableSetOf(PaymentMethod.EFECTIVO, PaymentMethod.TRANSFERENCIA)
        )
        localRepository.create(local2)

        val local3 = Local(
            "Maribel",
            "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/07/f1/bb/5b/cornelio.jpg",
            Address("Artigas", "6950", Point(-31.33, -66.92)),
            30.0,
            15.0,
            mutableSetOf(PaymentMethod.QR, PaymentMethod.TRANSFERENCIA)
        )
        localRepository.create(local3)

        // ----------------------- INGREDIENTES -----------------------------
        val ingredient1 = Ingredient("Lechuga", 5.0, FoodGroup.FRUTAS_Y_VERDURAS, 0)
        val ingredient2 = Ingredient("Tomate", 3.0, FoodGroup.FRUTAS_Y_VERDURAS, 1)
        val ingredient3 = Ingredient("Pollo", 20.0, FoodGroup.PROTEINAS, 2)
        val ingredient4 = Ingredient("Papas", 8.0, FoodGroup.FRUTAS_Y_VERDURAS, 3)

        listOf(ingredient1, ingredient2, ingredient3, ingredient4).forEach {
            ingredientRepository.create(it)
        }

        // ----------------------- PLATOS (algunos compartidos) -----------------------------

        val dish1 = Dish(
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ5ttboBLzQuY5VPQGm92DKWibKfjyIdV4-RA&s",
            "Ensalada Mixta",
            "Tomate + Lechuga",
            mutableListOf(ingredient1, ingredient2),
            local1,
            1.0,
            LocalDate.now(),
            0.0,
            10.0,
            1.0,
        )

        val dish2 = Dish(
            "https://wp-cdn.typhur.com/wp-content/uploads/2024/02/roasted-chicken.jpg",
            "Pollo Asado",
            "Pollo al horno con especias",
            mutableListOf(ingredient3),
            local1,
            1.0,
            LocalDate.now(),
            0.0,
            25.0,
            1.0,
        )

        val dish3 = Dish(
            "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQprKoqyoxNcntQpFeC9Lu1ve5UxUe0Pe6PHg&s",
            "Papas Fritas",
            "Crocantes y doradas",
            mutableListOf(ingredient4),
            local2,
            1.0,
            LocalDate.now(),
            0.0,
            8.0,
            1.0,
        )

        val dish4 = Dish(
            "https://resizer.glanacion.com/resizer/v2/el-truco-para-hacer-milanesas-de-bajo-MJ6775DYBRF3FNKAWVJVB6KY5I.jpg?auth=d69225ae02a7ee3cb62de88ec89b27b6b480558bff97cc7342a6d7c7f2db9c35&width=420&height=280&quality=70&smart=true",
            "Milanesa Criolla",
            "Carne empanizada con guarnición",
            mutableListOf(ingredient3, ingredient4),
            local3,
            1.0,
            LocalDate.now(),
            0.0,
            22.0,
            1.0,
        )

        listOf(
            dish1, dish2, dish3, dish4
        ).forEach { dishRepository.create(it) }

        // ----------------------- CLIENTES -----------------------------
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
            LocalDate.now().minusDays(17),
            "ola123@gmail.com",
            Address("Calle Falsa", "123", Point(-34.6, -58.45)),
        )

        listOf(client1, client2, client3, client4, client5, admin).forEach { clientRepository.create(it) }

        // ----------------------- PEDIDOS -----------------------------
        val points = listOf(Point(0.0,0.0), Point(0.0,10.0), Point(10.0,10.0), Point(10.0,0.0))
        val workZone = Polygon(points)
        val condition = AnyCondition(
            listOf(
                MinimumAmountCondition(50.0),
                TimeRangeCondition(LocalTime.of(10,0), LocalTime.of(22,0))
            )
        )

        val delivery = Delivery(
            "Delivery",
            "pipo",
            "pipon",
            "pepin",
            LocalDate.now(),
            addressW,
            workZone,
            condition
        )

        // ----------------------- ORDERS -----------------------------

        val order1 = Order(
            client2,
            listOf(dish1, dish2, dish3),
            OrderStatus.PENDIENTE,
            local1,
            delivery,
            PaymentMethod.QR
        )

        val order2 = Order(
            client1,
            listOf(dish2, dish4),
            OrderStatus.ENTREGADO,
            local1,
            delivery,
            PaymentMethod.TRANSFERENCIA
        )

        val ordersLocal1 = listOf(order1, order2)


        val order3 = Order(
            client3,
            listOf(dish4, dish4),
            OrderStatus.PENDIENTE,
            local2,
            delivery,
            PaymentMethod.EFECTIVO
        )

        val order4 = Order(
            client4,
            listOf(dish4),
            OrderStatus.PREPARADO,
            local2,
            delivery,
            PaymentMethod.TRANSFERENCIA
        )

        val ordersLocal2 = listOf(order3, order4)


        // LOCAL 3 → solo tiene dish5
        val order5 = Order(
            client5,
            listOf(dish4),
            OrderStatus.CANCELADO,
            local3,
            delivery,
            PaymentMethod.CREDITO
        )

        val ordersLocal3 = listOf(order5)

        val allOrders = ordersLocal1 + ordersLocal2 + ordersLocal3

        allOrders.forEach {
            it.pickupTime = LocalTime.now()
            orderRepository.create(it)
        }

        local1.orders.addAll(ordersLocal1)
        local2.orders.addAll(ordersLocal2)
        local3.orders.addAll(ordersLocal3)

        // ----------------------- RATINGS -----------------------------
        val ratingLocal1 = listOf(
            Rating(client1, local1, 4.5),
            Rating(client2, local1, 4.0),
            Rating(client3, local1, 3.5),
        )

        val ratingLocal2 = listOf(
            Rating(client3, local2, 5.0),
            Rating(client4, local2, 4.3)
        )

        val ratingLocal3 = listOf(
            Rating(client5, local3, 4.8)
        )

        local1.ratings.addAll(ratingLocal1)
        local2.ratings.addAll(ratingLocal2)
        local3.ratings.addAll(ratingLocal3)
    }
}
