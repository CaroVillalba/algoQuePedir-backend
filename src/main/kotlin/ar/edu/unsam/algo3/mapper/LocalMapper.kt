package ar.edu.unsam.algo3.mapper

import ar.edu.unsam.algo3.domain.Address
import ar.edu.unsam.algo3.domain.Client
import ar.edu.unsam.algo3.domain.Local
import ar.edu.unsam.algo3.domain.PaymentMethod
import ar.edu.unsam.algo3.dto.AddressDTO
import ar.edu.unsam.algo3.dto.PaymentMethodTaxDTO
import ar.edu.unsam.algo3.dto.LocalAccountDTO
import ar.edu.unsam.algo3.dto.LocalDTO
import ar.edu.unsam.algo3.dto.LocalCheckoutDTO
import ar.edu.unsam.algo3.exceptions.BusinessException

object LocalMapper {
    // Convierte entidad a DTO
    fun toLocalAccountDTO(local: Local): LocalAccountDTO {
        return LocalAccountDTO(
            local.id,
            local.name,
            local.imgURL,
            AddressDTO(
                local.address.street,
                local.address.number,
                local.address.coordinates
            ),
            local.appCommission,
            local.authorCommission,
            local.paymentMethods
                .filter { it in setOf(PaymentMethod.EFECTIVO, PaymentMethod.QR, PaymentMethod.TRANSFERENCIA) }
                .map { it.name.lowercase() }
        )
    }

    fun toLocalDTO(local: Local): LocalDTO {
        return LocalDTO(
            local.id,
            local.name,
            local.address.fullAddress(),
            local.imgURL,
            local.getScore(),
            local.ratings.size,
            local.orders.size,
            local.appCommission,
        )
    }

    fun toCheckoutDTO(local: Local): LocalCheckoutDTO {

        // TODO: agregar lógica real cuando exista
        val freeDelivery = true

        return LocalCheckoutDTO(
            local.id,
            local.name,
            local.imgURL,
            freeDelivery,
            local.paymentMethods.map {
                PaymentMethodTaxDTO(
                    it.name.lowercase(),
                    it.tax()
                )
            }
        )
    }

    // Convierte el DTO a entidad para actualizar la existente
    fun toEntity(dto: LocalAccountDTO, existing: Local): Local {
        validateDTO(dto)
        val paymentMethods = dto.paymentMethods.map { name ->
            try {
                PaymentMethod.valueOf(name.uppercase())
            } catch (e: BusinessException) {
                throw BusinessException("Método de pago inválido: $name")
            }
        }.toMutableSet()

        existing.name = dto.name
        existing.imgURL = dto.imgURL
        existing.address = Address(
            dto.address.street,
            dto.address.number,
            dto.address.coordinates
        )
        existing.appCommission = dto.appCommission
        existing.authorCommission = dto.authorCommission
        existing.paymentMethods = paymentMethods

        return existing
    }

    fun validateDTO(dto: LocalAccountDTO) {
        if (dto.name.isBlank()) throw BusinessException("El nombre no puede estar vacío")
        if (!dto.imgURL.startsWith("http")) throw BusinessException("URL de la imagen inválida")
        if (dto.address.street.isBlank()) throw BusinessException("Calle inválida")
        if (dto.address.number <= "0") throw BusinessException("Número de calle inválido")
        if (dto.address.coordinates.x !in -90.0..90.0) throw BusinessException("Latitud inválida")
        if (dto.address.coordinates.y !in -180.0..180.0) throw BusinessException("Longitud inválida")
        if (dto.appCommission !in 0.0..100.0) throw BusinessException("Comisión App inválida")
        if (dto.authorCommission !in 0.0..100.0) throw BusinessException("Comisión autores inválida")
        if (dto.paymentMethods.isEmpty()) throw BusinessException("Debe seleccionar al menos un método de pago")
    }
}