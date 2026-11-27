package ar.edu.unsam.algo3.service

import ar.edu.unsam.algo3.domain.Client
import ar.edu.unsam.algo3.domain.Local
import ar.edu.unsam.algo3.domain.LocalRepository
import ar.edu.unsam.algo3.dto.LocalAccountDTO
import ar.edu.unsam.algo3.dto.LocalDTO
import ar.edu.unsam.algo3.exceptions.ElementDoesNotExistException
import ar.edu.unsam.algo3.exceptions.BusinessException
import ar.edu.unsam.algo3.mapper.LocalMapper
import org.springframework.stereotype.Service

@Service
class LocalService(private val localRepository: LocalRepository, private val userService: UserService) {

    fun getById(id: Int): Local {
       return localRepository.getById(id)
           ?: throw ElementDoesNotExistException("Local no encontrado")
    }

    fun create(local: Local): Local {
        localRepository.create(local)  // esto asigna un id internamente
        // recuperamos la entidad recién creada desde el repo usando alguna forma de buscarla
        // por ejemplo por nombre (si es único) o tomando el último agregado
        val created = localRepository.elements.values.last() // último agregado
        return created
    }

    fun getPreferredLocals(dtos: MutableSet<LocalAccountDTO>?): MutableSet<Local?> {
        if (dtos != null) {
            return dtos.map { dto ->
                dto.id?.let { getById(it) }
            }.toMutableSet()
        }
        return mutableSetOf()
    }

    fun update(updatedLocal: Local) {
        localRepository.update(updatedLocal)
    }

    fun getAll(): List<Local> {
        return localRepository.getAll()
    }

    fun search(query: String): List<Local> {
        return localRepository.search(query)
    }

    fun getDistanceToClient(local: Local, userId: Int): Double {
        val user = userService.getById(userId)

        val client = user as? Client
            ?: throw BusinessException("Solo los clientes tienen dirección para calcular distancia")

        return client.distanceTo(local)
    }
}
