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
        firstName = "user",
        lastName = "user",
        username = usernameValido,
        password = passwordValida,
        birthDate = LocalDate.now(),
        email = "testuser@gmail.com",
    )

    @BeforeEach
    fun setUp() {
        clientRepository = mock()
        userService = UserService(clientRepository)
    }

    @Test
    fun `createClient deberia llamar al metodo create del repositorio con el cliente dado`() {
        userService.createClient(clienteValido)

        verify(clientRepository, times(1)).create(clienteValido)

        verifyNoMoreInteractions(clientRepository)
    }

    @Test
    fun `getUser deberia retornar el cliente si las credenciales son correctas`() {

        whenever(clientRepository.searchByUsername(usernameValido))
            .thenReturn(listOf(clienteValido))

        val resultado = userService.getUser(usernameValido, passwordValida)

        assertEquals(clienteValido, resultado)
    }

    @Test
    fun `getUser deberia lanzar BusinessException si la contraseña es incorrecta`() {
        val clienteConOtraPassword = Client(
            username = clienteValido.username,
            password = "otraPass",
            firstName = clienteValido.firstName,
            lastName = clienteValido.lastName,
            email = clienteValido.email
        )

        whenever(clientRepository.searchByUsername(usernameValido))
            .thenReturn(listOf(clienteConOtraPassword))

        val excepcion = assertThrows<BusinessException> {
            userService.getUser(usernameValido, passwordValida)
        }

        assertEquals("Usuario o contraseña incorrectos", excepcion.message)
    }

    @Test
    fun `getUser deberia lanzar BusinessException si el usuario no existe`() {
        val usuarioNoExistente = "nonexistent"

        whenever(clientRepository.searchByUsername(usuarioNoExistente))
            .thenReturn(emptyList())

        val excepcion = assertThrows<BusinessException> {
            userService.getUser(usuarioNoExistente, passwordValida)
        }

        assertEquals("Usuario o contraseña incorrectos", excepcion.message)
    }

    @Test
    fun `getUser deberia lanzar EmptyFieldsException si el nombre de usuario esta vacio`() {
        val usuarioVacio = ""

        val excepcion = assertThrows<EmptyFieldsException> {
            userService.getUser(usuarioVacio, passwordValida)
        }

        assertEquals("Usuario o contraseña incorrectos", excepcion.message)

        verify(clientRepository, never()).searchByUsername(any())
    }

    @Test
    fun `getUser deberia lanzar EmptyFieldsException si la contraseña esta en blanco`() {
        val passwordBlanca = "    "

        val excepcion = assertThrows<EmptyFieldsException> {
            userService.getUser(usernameValido, passwordBlanca)
        }

        assertEquals("Usuario o contraseña incorrectos", excepcion.message)

        verify(clientRepository, never()).searchByUsername(any())
    }

}
