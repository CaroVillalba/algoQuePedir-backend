package ar.edu.unsam.algo3.bootstrap

import ar.edu.unsam.algo3.domain.*
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import org.uqbar.geodds.Point
import java.time.LocalDate

@Component
class DataInitializerAuth (
    private val clienteRepository: ClientRepository
) {
    @PostConstruct
    fun init() {
        val usuario = Client(
            "Admin",
            "Admin",
            username = "admin",
            password = "admin",
            LocalDate.of(1990, 1, 1),
            email = "ola123@gmail.com",
            address = Address(
                "Calle Falsa",
                "123",
                Point(-34.6, -58.45)
            )
        )

        clienteRepository.create(usuario)

        println("✅ Usuario inicial cargado: admin con id ${usuario.username} con id ${usuario.id}")


    }
}