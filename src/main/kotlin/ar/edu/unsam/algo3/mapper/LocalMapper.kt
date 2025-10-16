package ar.edu.unsam.algo3.mapper

import ar.edu.unsam.algo3.domain.Address
import ar.edu.unsam.algo3.domain.Local
import ar.edu.unsam.algo3.domain.PaymentMethod
import ar.edu.unsam.algo3.dto.DireccionDTO
import ar.edu.unsam.algo3.dto.LocalAccountDTO
import org.uqbar.geodds.Point
import ar.edu.unsam.algo3.exceptions.BusinessException

object LocalMapper {
    // Convierte entidad a DTO
    fun toDTO(local: Local): LocalAccountDTO {
        return LocalAccountDTO(
            local.id,
            local.name,
            local.imgURL,
            DireccionDTO(
                local.address.street,
                local.address.number,
                local.address.coordinates.x,
                local.address.coordinates.y
            ),
            local.appCommission,
            local.authorCommission,
            local.paymentMethods
                .filter { it in setOf(PaymentMethod.EFECTIVO, PaymentMethod.QR, PaymentMethod.TRANSFERENCIA) }
                .map { it.name.lowercase() }
        )
    }

    // Convierte el DTO a entidad para actualizar la existente
    fun toEntity(dto: LocalAccountDTO, existing: Local): Local {
        existing.name = dto.name
        existing.imgURL = dto.imgURL
        existing.address = Address(
            dto.address.street,
            dto.address.number,
            Point(dto.address.lat, dto.address.lon)
        )
        existing.appCommission = dto.appCommission
        existing.authorCommission = dto.authorCommission
        existing.paymentMethods = dto.paymentMethods.map { name ->
            try {
                PaymentMethod.valueOf(name.uppercase())
            } catch (e: IllegalArgumentException) {
                throw BusinessException("Método de pago inválido: $name")
            }
        }.toMutableSet()

        return existing
    }
}