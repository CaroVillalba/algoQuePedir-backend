package ar.edu.unsam.algo3.bootstrap

import ar.edu.unsam.algo3.domain.*
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import org.uqbar.geodds.Point

@Component
class DataInitializer(
    private val localRepository: LocalRepository
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
    }
}
