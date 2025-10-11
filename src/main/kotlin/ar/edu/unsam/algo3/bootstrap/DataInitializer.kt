package ar.edu.unsam.algo3.bootstrap

import ar.edu.unsam.algo3.domain.*
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import org.uqbar.geodds.Point
import java.time.LocalDate
import kotlin.math.cos

@Component
class DataInitializer(
    private val localRepository: LocalRepository,
    private val platoRepository: PlatoRepository,
) {

    @PostConstruct
    fun init() {
        val local = Local(
            nombre = "UNSAM - Tornavías",
            imgURL = "https://picsum.photos/id/237/400/300",
            direccion = Direccion(
                calle = "San Martín",
                altura = "1234",
                coordenadas = Point(-34.6, -58.45)
            ),
            comisionApp = 30,
            comisionAutor = 10,
            metodosDePago = mutableSetOf(MedioDePago.EFECTIVO, MedioDePago.QR),
            valoraciones = mutableSetOf()
        )

        localRepository.create(local)

        println("✅ Local inicial cargado: ${local.nombre} con id ${local.id}")

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


    }
}
