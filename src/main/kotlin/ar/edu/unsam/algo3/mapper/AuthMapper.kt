package ar.edu.unsam.algo3.mapper

import ar.edu.unsam.algo3.domain.Client
import ar.edu.unsam.algo3.dto.AuthDTO
import ar.edu.unsam.algo3.exceptions.BusinessException

object AuthMapper {
    fun toDTO(client: Client): AuthDTO {
        return AuthDTO(
            client.id,
            client.username,
            client.password
        )
    }

    fun toEntity(dto: AuthDTO, existing: Client? = null): Client {
        if (dto.username.isBlank()) throw BusinessException("El usuario no puede estar vacío")
        if (dto.password.length < 6) throw BusinessException("La contraseña debe tener al menos 6 caracteres")
        return Client().apply {
            username = dto.username
            password = dto.password
            // id se genera automáticamente en el repositorio si es nuevo
        }
    }
}
