package ar.edu.unsam.algo3.bootstrap

import ar.edu.unsam.algo3.domain.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ar.edu.unsam.algo3.dto.PlatoListDTO
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import org.uqbar.geodds.Point
import java.io.File
import java.time.LocalDate
import org.uqbar.geodds.Polygon
import java.time.LocalTime

@Component
class DataInitializer(
    private val localRepository: LocalRepository,
    private val platoRepository: DishRepository,
    private val pedidoRepository: OrderRepository,
    private val ingredienteRepository: IngredientRepository
) {
    private val mapper = jacksonObjectMapper()

    @PostConstruct
    fun init() {
        val fileMenu = File("src/main/resources/menuData.json")
        val platoListDTO = mapper.readValue<PlatoListDTO>(fileMenu)

        val fileIngredients = File("src/main/resources/ingredientsData.json")
        val ingredients = mapper.readValue<List<Ingredient>>(fileIngredients)

        val restaurante = Local(
            "La Buena Mesa",
            "https://www.hilton.com/im/en/BUEPIHH/19783367/mural-2.jpg?impolicy=crop&cw=3857&ch=3000&gravity=NorthWest&xposition=71&yposition=0&rw=684&rh=532",
            Address(
                "Av. del Gourmet",
                 "1250",
                Point(-31.32, -66.95)
            ),
            25.0,
            12.5,
            mutableSetOf(PaymentMethod.EFECTIVO, PaymentMethod.QR, PaymentMethod.TRANSFERENCIA),
            mutableSetOf()
        )

        localRepository.create(restaurante)

        // ------------------------------------------------------------------
        platoListDTO.platos.forEach { platoDTO ->

            val ingredientesEntidad = platoDTO.ingredientes.map { ingDTO ->
                Ingredient(
                    name = ingDTO.name,
                    price = ingDTO.price,
                    group = FoodGroup.valueOf(ingDTO.group)
                )
            }.toMutableList()


            val dishEntidad = Dish(
                platoDTO.imageURL,
                platoDTO.nombre,
                platoDTO.descripcion,
                ingredientesEntidad,
                restaurante,
                platoDTO.porcentajeAcordadoConLocal,
                LocalDate.now(),
                platoDTO.regaliasAutor,
                platoDTO.valorBase,
                platoDTO.descuento,
            )

            platoRepository.create(dishEntidad)
        }

        ingredients.forEach { ingrediente -> ingredienteRepository.create(ingrediente) }
        ///////////////////////////////////////////////////////////////////////////////////////////////////

        // ------------------------------------------------------------------
        val ingredient1 = Ingredient(
            name = "lechuga",
            price = 5.0,
            group = FoodGroup.FRUTAS_Y_VERDURAS
        )
        val ingredient2 = Ingredient(
            name= "tomate",
            price = 3.0,
            group = FoodGroup.FRUTAS_Y_VERDURAS
        )

        val dish = Dish(
            "https://picsum.photos/id/237/400/300",
            "Ensalada de Tomate y lechuga",
            "Ensalada clasica de tomate y lechuga",
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
            "Ensalada clasica de lechuga",
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
            "Ensalada clasica de tomate",
            mutableListOf(ingredient2),
            restaurante,
            1.0,
            LocalDate.now(),
            0.0,
            10.0,
            1.0,
        )

        platoRepository.create(dish)
        platoRepository.create(dish2)
        platoRepository.create(dish3)

        ///////////////////////////////////////////////////////////////////////////////////////////////////

        /*la direccion no deberia ser del cliente directamente? */
        val addressW = Address(
            "San Andres",
            "334",
            Point(-5.0, 5.0)
        )

        val addressH = Address(
            "Hinamizawa",
            "34",
            Point(-53.4430, 25.220)
        )

        val user = Client(
            "Alejandra Pisco",
            username = "piscosour"
        )

        val user2 = Client(
            "Pedro Aznar",
            username = "cigarra"
        )

        val user3 = Client(
            "Yu Tsun",
            username = "bifurc"
        )

        val user4 = Client(
            "Paul Valery",
            username = "omniscent"
        )

        val user5 = Client(
            "Homer Silson",
            username = "silso"
        )

        user.address = addressW
        user2.address = addressW
        user3.address = addressH
        user4.address = addressH
        user5.address = addressH

        val puntos = listOf(
            Point(0.0, 0.0),
            Point(0.0, 10.0),
            Point(10.0, 10.0),
            Point(10.0, 0.0)
        )

        val zonaTrabajo = Polygon(puntos)

        val condicion = AnyCondition(
            listOf(
                MinimumAmountCondition(50.0),  // mínimo $50
                TimeRangeCondition(LocalTime.of(10,0), LocalTime.of(22,0)) // entre 10:00 y 22:00
            )
        )

        val deliveri = Delivery(
            "Deliveri",
            "pipo",
            "pipon",
            "pepin",
            LocalDate.now(),
            addressW,
            zonaTrabajo,
            condicion
        )

        /*ATENCION!!: Realice un cambio en Pedido. Hablar con el profe de esto.
        DOS CAMBIOS en realidad. */
        /*el state está como pendiente en default*/
        val order1 = Order(
            user2,
            listOf(dish, dish2, dish3),
            local = restaurante,
            delivery = deliveri,
            paymentMethod = PaymentMethod.TRANSFERENCIA
        )

        val order2 = Order(
            user3,
            listOf(dish, dish2, dish3),
            local = restaurante,
            delivery = deliveri,
            paymentMethod = PaymentMethod.EFECTIVO
        )

        val order3 = Order(
            user,
            listOf(dish, dish2, dish3),
            local = restaurante,
            delivery = deliveri,
            paymentMethod = PaymentMethod.DEBITO
        )

        val order4 = Order(
            user4,
            listOf(dish, dish2, dish3),
            local = restaurante,
            delivery = deliveri,
            paymentMethod = PaymentMethod.TRANSFERENCIA
        )

        val order5 = Order(
            user5,
            listOf(dish, dish2, dish3),
            local = restaurante,
            delivery = deliveri,
            paymentMethod = PaymentMethod.TRANSFERENCIA
        )


        order4.status = OrderStatus.PREPARADO
        order2.status = OrderStatus.ENTREGADO
        order5.status = OrderStatus.CANCELADO



        order1.pickupTime = LocalTime.now()
        order2.pickupTime = LocalTime.now()
        order3.pickupTime = LocalTime.now()
        order4.pickupTime = LocalTime.now()
        order5.pickupTime = LocalTime.now()

        pedidoRepository.create(order1)
        pedidoRepository.create(order2)
        pedidoRepository.create(order3)
        pedidoRepository.create(order4)
        pedidoRepository.create(order5)
    }
}
