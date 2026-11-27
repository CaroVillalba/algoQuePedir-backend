package ar.edu.unsam.algo3.service

import ar.edu.unsam.algo3.domain.Client
import ar.edu.unsam.algo3.domain.ClientRepository
import org.springframework.stereotype.Service
import ar.edu.unsam.algo3.domain.LocalAdmin
import ar.edu.unsam.algo3.domain.LocalAdminRepository
import ar.edu.unsam.algo3.domain.LocalRepository
import ar.edu.unsam.algo3.domain.User
import ar.edu.unsam.algo3.exceptions.EmptyFieldsException
import ar.edu.unsam.algo3.exceptions.BusinessException
import ar.edu.unsam.algo3.exceptions.ElementDoesNotExistException

@Service
class UserService(
    private val localAdminRepository: LocalAdminRepository,
    private val clientRepository: ClientRepository,
    private val localRepository: LocalRepository
) {

    fun createUser(user: User) {
        if (user is LocalAdmin) {
            if (user.localId != null) {
                if (localRepository.getById(user.localId!!) == null)
                    throw BusinessException("El local asignado no existe")
            }

            localAdminRepository.create(user)
            return
        }

        if (user is Client) {
            clientRepository.create(user)
            return
        }

        throw BusinessException("Tipo de usuario desconocido")
    }

    fun getUser(username: String, password: String): User {
        if (username.isBlank() || password.isBlank()) {
            throw EmptyFieldsException("Usuario o contraseña incorrectos")
        }

        val admin = localAdminRepository.searchByUsername(username)
            .firstOrNull { it.password == password }
        if (admin != null) return admin

        val client = clientRepository.searchByUsername(username)
            .firstOrNull { it.password == password }
        if (client != null) return client

        throw BusinessException("Usuario o contraseña incorrectos")
    }

    fun getById(id: Int): User {
        localAdminRepository.getById(id)?.let { return it }
        clientRepository.getById(id)?.let { return it }

        throw ElementDoesNotExistException("Usuario no encontrado")
    }

    fun getClientById(id: Int): Client {
        val user = this.getById(id)
        if (user is Client) return user
        throw BusinessException("El usuario con id $id no es un cliente")
    }

    fun getLocalAdminById(id: Int): LocalAdmin {
        val user = this.getById(id)
        if (user is LocalAdmin) return user
        throw BusinessException("El usuario con id $id no es un administrador de local")
    }

    fun assignLocalToAdmin(userId: Int, localId: Int) {
        val admin = localAdminRepository.getById(userId)
            ?: throw BusinessException("No existe admin con id $userId")

        val local = localRepository.getById(localId)
            ?: throw BusinessException("No existe local con id $localId")

        admin.localId = localId
        localAdminRepository.update(admin)
    }

    fun updateProfile(updatedClient: Client) {
        clientRepository.update(updatedClient)
    }

    fun getUserByUsername(username : String) : Client {
        val foundUser = clientRepository.searchByUsername(username).firstOrNull()
        ?: throw BusinessException("Usuario no encontrado")
        return foundUser
    }
}
