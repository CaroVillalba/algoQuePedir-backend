package ar.edu.unsam.algo3.service

import org.springframework.stereotype.Service
import ar.edu.unsam.algo3.domain.Client
import ar.edu.unsam.algo3.domain.ClientRepository
import ar.edu.unsam.algo3.exceptions.EmptyFieldsException
import ar.edu.unsam.algo3.exceptions.BusinessException

@Service
class UserService(private val clientRepository: ClientRepository) {

    fun createClient(client: Client) {
        clientRepository.create(client)
    }

    fun getUser(user: String, password: String): Client {
        if (user.isBlank() || password.isBlank()) {
            throw EmptyFieldsException("Usuario o contraseña incorrectos")
        }

        val foundUser = clientRepository.searchByUsername(user)
            .firstOrNull { it.password == password }
            ?: throw BusinessException("Usuario o contraseña incorrectos")

        return foundUser
    }
}
