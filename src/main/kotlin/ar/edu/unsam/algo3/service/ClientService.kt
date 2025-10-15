package ar.edu.unsam.algo3.service

import ar.edu.unsam.algo3.domain.*
import org.springframework.stereotype.Service
import ar.edu.unsam.algo3.domain.Client
import ar.edu.unsam.algo3.domain.ClientRepository
import ar.edu.unsam.algo3.dto.AuthDTO


@Service
class ClientService(private val clientRepository: ClientRepository) {

    fun getClient(user: String, password: String): AuthDTO {
        val client = clientRepository.searchCredentials(user, password)

        val authDTO =
            AuthDTO(
                username = client.username,
                password = client.password,
                id = client.id
            )

        return authDTO

    }

    fun createClient(client : Client){
        clientRepository.create(client)
    }
}
