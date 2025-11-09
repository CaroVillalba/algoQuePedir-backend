package ar.edu.unsam.algo3.service

import ar.edu.unsam.algo3.domain.Local
import ar.edu.unsam.algo3.domain.LocalRepository
import ar.edu.unsam.algo3.exceptions.ElementDoesNotExistException
import org.springframework.stereotype.Service

@Service
class LocalService(private val localRepository: LocalRepository) {

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

}
