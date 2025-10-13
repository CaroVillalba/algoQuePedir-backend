package ar.edu.unsam.algo3.service

import ar.edu.unsam.algo3.domain.Direccion
import ar.edu.unsam.algo3.domain.Local
import ar.edu.unsam.algo3.domain.LocalRepository
import ar.edu.unsam.algo3.domain.MedioDePago
import ar.edu.unsam.algo3.dto.DireccionDTO
import ar.edu.unsam.algo3.dto.LocalAccountDTO
import ar.edu.unsam.algo3.exceptions.BusinessException
import org.springframework.stereotype.Service
import org.uqbar.geodds.Point

@Service
class LocalService(private val localRepository: LocalRepository) {

    fun getLocalById(id: Int): LocalAccountDTO {
        val local = localRepository.getById(id)
            ?: throw BusinessException("Local no encontrado")
        return local.toDTO()
    }

    fun updateLocal(id: Int, dto: LocalAccountDTO): LocalAccountDTO {
        val local = localRepository.getById(id)
            ?: throw BusinessException("Local no encontrado")

        local.name = dto.nombre
        local.imgURL = dto.imgURL
        local.address = Direccion(
            calle = dto.direccion.calle,
            altura = dto.direccion.altura,
            coordenadas = Point(dto.direccion.lat, dto.direccion.lon)
        )
        local.paymentMethods = dto.metodosDePago.map { nombre ->
            try {
                MedioDePago.valueOf(nombre.uppercase())
            } catch (e: IllegalArgumentException) {
                throw BusinessException("Método de pago inválido: $nombre")
            }
        }.toMutableSet()

        localRepository.update(local)

        try {
            val mapper = com.fasterxml.jackson.module.kotlin.jacksonObjectMapper()
            val file = java.io.File("src/main/resources/localData.json")
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, local.toDTO())
        } catch (e: Exception) {
            throw BusinessException("Error al guardar Local en JSON: ${e.message}")
        }

        return local.toDTO()
    }

}

fun Local.toDTO() = LocalAccountDTO(
    nombre = name,
    imgURL = imgURL,
    direccion = DireccionDTO(
        calle = address.calle,
        altura = address.altura,
        lat = address.coordenadas.x,
        lon = address.coordenadas.y
    ),
    comisionApp = comisionApp,
    comisionAutor = comisionAuthor,
    metodosDePago = paymentMethods
        .filter { it in setOf(MedioDePago.EFECTIVO, MedioDePago.QR, MedioDePago.TRANSFERENCIA) }
        .map { it.name.lowercase() }
)
