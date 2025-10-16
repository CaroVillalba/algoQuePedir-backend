package ar.edu.unsam.algo3.service

import org.springframework.stereotype.Service
import ar.edu.unsam.algo3.domain.Client
import ar.edu.unsam.algo3.domain.ClientRepository


@Service
class UserService(private val clientRepository: ClientRepository) {

    fun getClient(user: String, password: String): Client {
        return clientRepository.searchCredentials(user, password)
    }

    fun createClient(client : Client){
        clientRepository.create(client)
    }
}
