package ar.edu.unsam.algo3.mapper

import ar.edu.unsam.algo3.domain.Address
import ar.edu.unsam.algo3.domain.Client
import ar.edu.unsam.algo3.dto.AddressDTO
import ar.edu.unsam.algo3.dto.UserDTO
import ar.edu.unsam.algo3.exceptions.BusinessException

object UserMapper {

    fun toEntity(dto: UserDTO): Client {
        if( dto.firstname.isEmpty() ) throw BusinessException("El nombre no puede estar vacio")
        if( dto.lastname.isEmpty() ) throw BusinessException("El apellido no puede estar vacio")
        if (dto.address.street.isBlank()) throw BusinessException("Calle inválida")
        if (dto.address.number <= "0") throw BusinessException("Número de calle inválido")
        if (dto.address.coordinates.y !in -90.0..90.0) throw BusinessException("Latitud inválida")
        if (dto.address.coordinates.x !in -180.0..180.0) throw BusinessException("Longitud inválida")

        return Client().apply {
            firstname = dto.firstname
            lastname = dto.lastname
            address = Address(
                dto.address.street,
                dto.address.number,
                dto.address.coordinates,
            )
        }
    }

    fun toDTO(client: Client) : UserDTO {
        return UserDTO(
            client.id,
            client.firstname,
            client.lastname,
            AddressDTO(
                client.address.street,
                client.address.number,
                client.address.coordinates
            ),
            client.username,
            client.password,
            client.email,

        )
    }


}