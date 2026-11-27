package ar.edu.unsam.algo3.service

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import ar.edu.unsam.algo3.domain.Client
import ar.edu.unsam.algo3.domain.ClientRepository
import ar.edu.unsam.algo3.exceptions.EmptyFieldsException
import ar.edu.unsam.algo3.exceptions.BusinessException
import org.mockito.kotlin.*
import java.time.LocalDate


class UserServiceTest {
    private lateinit var clientRepository: ClientRepository
    private lateinit var userService: UserService

    private val passwordValida = "password123"
    private val usernameValido = "testuser"
    private val clienteValido = Client(
        firstname = "user",
        lastname = "user",
        username = usernameValido,
        password = passwordValida,
        email = "testuser@gmail.com",
    )
}