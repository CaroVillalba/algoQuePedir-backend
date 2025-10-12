package ar.edu.unsam.algo3.bootstrap

import ar.edu.unsam.algo3.domain.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ar.edu.unsam.algo3.dto.LocalAccountDTO
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
    private val pedidoRepository: PedidoRepository
) {
    private val mapper = jacksonObjectMapper()

    @PostConstruct
    fun init() {
        val file = File("src/main/resources/localData.json")
        val localDTO = mapper.readValue<LocalAccountDTO>(file)

        val local = Local(
            nombre = localDTO.nombre,
            imgURL = localDTO.imgURL,
            direccion = Direccion(
                calle = localDTO.direccion.calle,
                altura = localDTO.direccion.altura,
                coordenadas = Point(localDTO.direccion.lat, localDTO.direccion.lon)
            ),
            comisionApp = localDTO.comisionApp,
            comisionAutor = localDTO.comisionAutor,
            metodosDePago = localDTO.metodosDePago.map { MedioDePago.valueOf(it.uppercase()) }.toMutableSet(),
            valoraciones = mutableSetOf()
        )

        localRepository.create(local)

        println("✅ Local cargado desde JSON: ${local.nombre}")

        // ------------------------------------------------------------------
        val ingrediente1 = Ingrediente(
            nombre = "lechuga",
            costo = 5.0,
            grupoAlimentos = GrupoAlimentos.FRUTAS_Y_VERDURAS
        )
        val ingrediente2 = Ingrediente(
            nombre = "tomate",
            costo = 3.0,
            grupoAlimentos = GrupoAlimentos.FRUTAS_Y_VERDURAS
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

        val user = Cliente(
            nombre = "Alejandra",
            username = "piscosour"
        )

        user.direccion = direccionW

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
            cliente = user,
            plato = listOf(plato, plato2, plato3),
            local = local,
            delivery = deliveri,
            medioDePago = MedioDePago.QR
        )

        order1.horaRetiro = LocalTime.now()

        pedidoRepository.create(order1)
    }
}
