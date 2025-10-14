package ar.edu.unsam.algo3.service

import ar.edu.unsam.algo3.domain.*
import org.springframework.stereotype.Service
import ar.edu.unsam.algo3.domain.Cliente
import ar.edu.unsam.algo3.domain.ClienteRepository
import ar.edu.unsam.algo3.dto.AuthDTO
import ar.edu.unsam.algo3.dto.MenuDTO
import ar.edu.unsam.algo3.exceptions.BusinessException
import java.math.RoundingMode


@Service
class ClienteService(private val clienteRepository: ClienteRepository) {

    fun getCliente(user: String, password: String): AuthDTO {
        val cliente = clienteRepository.searchCredenciales(user, password)

        val authDTO =
            AuthDTO(
                username = cliente.username,
                password = cliente.password,
                id = cliente.id
            )

        return authDTO

    }

    fun createCliente(cliente : Cliente){
        clienteRepository.create(cliente)
    }
}
