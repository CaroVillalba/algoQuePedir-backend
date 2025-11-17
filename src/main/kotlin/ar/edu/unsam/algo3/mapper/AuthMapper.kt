package ar.edu.unsam.algo3.mapper

import ar.edu.unsam.algo3.domain.Address
import ar.edu.unsam.algo3.domain.Client
import ar.edu.unsam.algo3.dto.AuthDTO
import ar.edu.unsam.algo3.exceptions.BusinessException
import ar.edu.unsam.algo3.dto.UserDTO
import java.time.LocalDate


object AuthMapper {
    fun toDTO(client: Client): AuthDTO {
        return AuthDTO(
            client.id,
            client.username,
            client.password
        )
    }

    fun toEntity(dto: UserDTO): Client {
        if (dto.username.isBlank()) throw BusinessException("El usuario no puede estar vacío")
        if (dto.password.length < 6) throw BusinessException("La contraseña debe tener al menos 6 caracteres")

        return Client (
            firstname = dto.firstname,
            lastname = dto.lastname,
            username = dto.username,
            password = dto.password,
            registrationDate = LocalDate.now(),
            email = dto.email,
            address = Address(
                dto.address.street,
                dto.address.number,
                dto.address.coordinates,
            )
        )
    }
}
