package ar.edu.unsam.algo3.service

import ar.edu.unsam.algo3.domain.Local
import ar.edu.unsam.algo3.domain.LocalRepository
import ar.edu.unsam.algo3.exceptions.BusinessException
import org.springframework.stereotype.Service

@Service
class LocalService(private val localRepository: LocalRepository) {

    fun getLocalById(id: Int): Local {
       return localRepository.getById(id)
           ?: throw BusinessException("Local no encontrado")
    }

    fun updateLocal(id: Int, updatedLocal: Local): Local {
        val local = localRepository.getById(id)
            ?: throw BusinessException("Local no encontrado")

        local.name = updatedLocal.name
        local.imgURL = updatedLocal.imgURL
        local.address = updatedLocal.address
        local.appCommission = updatedLocal.appCommission
        local.authorCommission = updatedLocal.authorCommission
        local.paymentMethods = updatedLocal.paymentMethods

        localRepository.update(local)

        return local
    }
}
