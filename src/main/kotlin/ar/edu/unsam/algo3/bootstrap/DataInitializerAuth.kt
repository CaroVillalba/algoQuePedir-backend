package ar.edu.unsam.algo3.bootstrap

import ar.edu.unsam.algo3.domain.*
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import org.uqbar.geodds.Point
import java.time.LocalDate

@Component
class DataInitializerAuth (
    private val clienteRepository: ClienteRepository
) {
    @PostConstruct
    fun init() {
        val usuario = Cliente(
            nombre = "Admin",
            apellido = "Admin",
            username = "admin",
            password = "admin",
            fechaNacimiento = LocalDate.of(1990, 1, 1),
            mail = "ola123@gmail.com",
            direccion = Direccion(
                calle = "Calle Falsa",
                altura = "123",
                coordenadas = Point(-34.6, -58.45)
            )
        )

        clienteRepository.create(usuario)

        println("✅ Usuario inicial cargado: admin con id ${usuario.username} con id ${usuario.id}")


    }
}