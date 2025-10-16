package ar.edu.unsam.algo3.mapper

import ar.edu.unsam.algo3.domain.Client
import ar.edu.unsam.algo3.dto.AuthDTO

object AuthMapper {
    fun toDTO(client: Client): AuthDTO {
        return AuthDTO(
            client.id,
            client.username,
            client.password
        )
    }

    fun toEntity(dto: AuthDTO, existing: Client? = null): Client {
        return Client().apply {
            username = dto.username
            password = dto.password
            // id se genera automáticamente en el repositorio si es nuevo
        }
    }
}
