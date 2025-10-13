package ar.edu.unsam.algo3.bootstrap

import ar.edu.unsam.algo3.domain.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ar.edu.unsam.algo3.dto.LocalAccountDTO
import ar.edu.unsam.algo3.dto.PlatoListDTO
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import org.uqbar.geodds.Point
import java.io.File
import java.time.LocalDate
import org.uqbar.geodds.Polygon
import java.time.LocalTime
import java.time.temporal.TemporalQueries.localDate

@Component
class DataInitializer(
    private val localRepository: LocalRepository,
    private val platoRepository: PlatoRepository,
    private val pedidoRepository: PedidoRepository,
    private val ingredienteRepository: IngredienteRepository
) {
    private val mapper = jacksonObjectMapper()

    @PostConstruct
    fun init() {
        val file = File("src/main/resources/localData.json")
        val localDTO = mapper.readValue<LocalAccountDTO>(file)

        val fileMenu = File("src/main/resources/menuData.json")
        val platoListDTO = mapper.readValue<PlatoListDTO>(fileMenu)

        val fileIngredients = File("src/main/resources/ingredientsData.json")
        val ingredients = mapper.readValue<List<Ingrediente>>(fileIngredients)


        val local = Local(
            name = localDTO.nombre,
            imgURL = localDTO.imgURL,
            address = Direccion(
                calle = localDTO.direccion.calle,
                altura = localDTO.direccion.altura,
                coordenadas = Point(localDTO.direccion.lat, localDTO.direccion.lon)
            ),
            comisionApp = localDTO.comisionApp,
            comisionAuthor = localDTO.comisionAutor,
            paymentMethods = localDTO.metodosDePago.map { MedioDePago.valueOf(it.uppercase()) }.toMutableSet(),
            ratings = mutableSetOf()
        )

        localRepository.create(local)

        println("✅ Local cargado desde JSON: ${local.name}")

        // ------------------------------------------------------------------
        // Accedemos a la lista de DTOs y realizamos el mapeo manual
        platoListDTO.platos.forEach { platoDTO ->

            // Mapeo de Ingredientes de DTO a Entidad
            val ingredientesEntidad = platoDTO.ingredientes.map { ingDTO ->
                Ingrediente(
                    name = ingDTO.name,
                    price = ingDTO.price,
                    group = GrupoAlimentos.valueOf(ingDTO.group)
                )
            }.toMutableList()

            // Mapeo a la entidad Plato, INYECTANDO el 'local'
            val platoEntidad = Plato(
                imageURL = platoDTO.imageURL,
                nombre = platoDTO.nombre,
                descripcion = platoDTO.descripcion,
                ingredientes = ingredientesEntidad,
                local = local, // 👈 Se inyecta el objeto Local ya existente
                porcentajeAcordadoConLocal = platoDTO.porcentajeAcordadoConLocal,
                lanzamiento = LocalDate.now(), // 👈 Se inyecta la fecha actual
                regaliasAutor = platoDTO.regaliasAutor,
                valorBase = platoDTO.valorBase,
                descuento = platoDTO.descuento,
            )

            platoRepository.create(platoEntidad)
        }

        ingredients.forEach { ingrediente -> ingredienteRepository.create(ingrediente) }
        ///////////////////////////////////////////////////////////////////////////////////////////////////

        // ------------------------------------------------------------------
        val ingrediente1 = Ingrediente(
            name = "lechuga",
            price = 5.0,
            group = GrupoAlimentos.FRUTAS_Y_VERDURAS
        )
        val ingrediente2 = Ingrediente(
            name= "tomate",
            price = 3.0,
            group = GrupoAlimentos.FRUTAS_Y_VERDURAS
        )

        val plato = Plato(
            imageURL = "https://picsum.photos/id/237/400/300",
            nombre = "Ensalada de Tomate y lechuga",
            descripcion = "Ensalada clasica de tomate y lechuga",
            ingredientes = mutableListOf(ingrediente1, ingrediente2),
            local = local,
            porcentajeAcordadoConLocal = 1.0,
            lanzamiento = LocalDate.now(),
            regaliasAutor = 0.0,
            valorBase = 10.0,
            descuento = 1.0,
        )
        val plato2 = Plato(
            imageURL = "https://picsum.photos/id/237/400/300",
            nombre = "Ensalada de lechuga",
            descripcion = "Ensalada clasica de lechuga",
            ingredientes = mutableListOf(ingrediente1),
            local = local,
            porcentajeAcordadoConLocal = 1.0,
            lanzamiento = LocalDate.now(),
            regaliasAutor = 0.0,
            valorBase = 10.0,
            descuento = 1.0,
        )
        val plato3 = Plato(
            imageURL = "https://picsum.photos/id/237/400/300",
            nombre = "Ensalada de Tomate",
            descripcion = "Ensalada clasica de tomate",
            ingredientes = mutableListOf(ingrediente2),
            local = local,
            porcentajeAcordadoConLocal = 1.0,
            lanzamiento = LocalDate.now(),
            regaliasAutor = 0.0,
            valorBase = 10.0,
            descuento = 1.0,
        )

        platoRepository.create(plato)
        platoRepository.create(plato2)
        platoRepository.create(plato3)

        ///////////////////////////////////////////////////////////////////////////////////////////////////

        /*la direccion no deberia ser del cliente directamente? */
        val direccionW = Direccion(
            calle = "San Andres",
            altura = "334",
            coordenadas = Point(-5.0, 5.0)
        )

        val direccionH = Direccion(
            calle = "Hinamizawa",
            altura = "34",
            coordenadas = Point(-53.4430, 25.220)
        )

        val user = Cliente(
            nombre = "Alejandra",
            username = "piscosour"
        )

        val user2 = Cliente(
            nombre = "Pedro Aznar",
            username = "cigarra"
        )

        val user3 = Cliente(
            nombre = "Yu Tsun",
            username = "senderosbifurcados"
        )

        user.direccion = direccionW
        user2.direccion = direccionW
        user3.direccion = direccionH

        val puntos = listOf(
            Point(0.0, 0.0),
            Point(0.0, 10.0),
            Point(10.0, 10.0),
            Point(10.0, 0.0)
        )

        val zonaTrabajo = Polygon(puntos)

        val condicion = CondicionAny(
            listaCondiciones = listOf(
                CondicionMontoMinimo(50.0),  // mínimo $50
                CondicionRangoHorario(LocalTime.of(10,0), LocalTime.of(22,0)) // entre 10:00 y 22:00
            )
        )

        val deliveri = Delivery(
            nombre = "Deliveri",
            apellido = "pipo",
            username = "pipon",
            password = "pepin",
            fechaNacimiento = LocalDate.now(),
            direccion = direccionW,
            zonaDeTrabajo = zonaTrabajo,
            condicionesEntrega = condicion
        )

        /*ATENCION!!: Realice un cambio en Pedido. Hablar con el profe de esto.
        DOS CAMBIOS en realidad. */
        /*el state está como pendiente en default*/
        val order1 = Pedido(
            cliente = user2,
            plato = listOf(plato, plato2, plato3),
            local = local,
            delivery = deliveri,
            medioDePago = MedioDePago.QR
        )

        val order2 = Pedido(
            cliente = user3,
            plato = listOf(plato, plato2, plato3),
            local = local,
            delivery = deliveri,
            medioDePago = MedioDePago.EFECTIVO
        )

        val order3 = Pedido(
            cliente = user,
            plato = listOf(plato, plato2, plato3),
            local = local,
            delivery = deliveri,
            medioDePago = MedioDePago.TRANSFERENCIA
        )


        order1.horaRetiro = LocalTime.now()
        order2.horaRetiro = LocalTime.now()
        order3.horaRetiro = LocalTime.now()

        pedidoRepository.create(order1)
        pedidoRepository.create(order2)
        pedidoRepository.create(order3)
    }
}
