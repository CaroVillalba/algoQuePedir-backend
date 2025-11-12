package ar.edu.unsam.algo3.service

import ar.edu.unsam.algo3.domain.Local
import ar.edu.unsam.algo3.domain.LocalRepository
import ar.edu.unsam.algo3.dto.LocalDTO
import ar.edu.unsam.algo3.exceptions.ElementDoesNotExistException
import ar.edu.unsam.algo3.mapper.LocalMapper
import org.springframework.stereotype.Service

@Service
class LocalService(private val localRepository: LocalRepository, private val userService: UserService) {

    fun getById(id: Int): Local {
       return localRepository.getById(id)
           ?: throw ElementDoesNotExistException("Local no encontrado")
    }

    fun update(updatedLocal: Local){
        localRepository.update(updatedLocal)
    }

    fun getAll(): List<Local> {
        return localRepository.getAll()
    }

    fun search(query: String): List<Local> {
        return localRepository.search(query)
    }

    fun addDistanceInfo(locales: List<Local>, userId: Int): List<LocalDTO> {
        val user = userService.getById(userId)
        return locales.map { local ->
            val esCercano = user.isLocalNearby(local)
            //esto lo tendría que hacer el controller
            LocalMapper.toLocalDTO(local).copy(esCercano = esCercano)
        }
    }

}
