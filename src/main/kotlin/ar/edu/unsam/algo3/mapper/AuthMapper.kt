package ar.edu.unsam.algo3.mapper

import ar.edu.unsam.algo3.domain.Client
import ar.edu.unsam.algo3.domain.LocalAdmin
import ar.edu.unsam.algo3.domain.User
import ar.edu.unsam.algo3.dto.AuthDTO
import ar.edu.unsam.algo3.exceptions.BusinessException

object AuthMapper {
    fun toDTO(user: User): AuthDTO {
        return AuthDTO(
            user.id,
            user.username,
            user.password,
            localId = if (user is LocalAdmin) user.localId else null
        )
    }

    fun toClient(dto: AuthDTO): Client {
        val client = Client()
        mapUserBase(dto, client)
        return client
    }

    fun toAdmin(dto: AuthDTO): LocalAdmin {
        val admin = LocalAdmin(localId = dto.localId)
        mapUserBase(dto, admin)

        return admin
    }

    private fun mapUserBase(dto: AuthDTO, user: User): User {
        if (dto.username.isBlank()) throw BusinessException("El usuario no puede estar vacío")
        if (dto.password.length < 6) throw BusinessException("La contraseña debe tener al menos 6 caracteres")

        user.username = dto.username
        user.password = dto.password

        return user
    }
}
