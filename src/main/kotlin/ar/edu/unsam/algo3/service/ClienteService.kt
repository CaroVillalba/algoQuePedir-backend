package ar.edu.unsam.algo3.service

import ar.edu.unsam.algo3.domain.*
import org.springframework.stereotype.Service
import ar.edu.unsam.algo3.domain.Cliente
import ar.edu.unsam.algo3.domain.ClienteRepository
import ar.edu.unsam.algo3.dto.AuthDTO
import ar.edu.unsam.algo3.exceptions.BusinessException


@Service
class ClienteService(private val clienteRepository: ClienteRepository) {

    fun getClienteById(id: Int): AuthDTO {
        val cliente = clienteRepository.getById(id)
            ?: throw BusinessException("Cliente no encontrado")
        return cliente.toDTO()
    }


fun Cliente.toDTO() = AuthDTO(
    username = this.username,
    password = this.password,
    nombre = this.nombre,
    apellido = this.apellido,
    mail = this.mail,
    )
}