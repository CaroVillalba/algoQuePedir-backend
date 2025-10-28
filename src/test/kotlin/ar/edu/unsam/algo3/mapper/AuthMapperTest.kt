package ar.edu.unsam.algo3.mapper

import ar.edu.unsam.algo3.domain.Client
import ar.edu.unsam.algo3.dto.AuthDTO
import ar.edu.unsam.algo3.exceptions.BusinessException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows

class AuthMapperTest {

    private val CLIENT_ID = 100
    private val USERNAME_VALIDO = "testuser"
    private val PASSWORD_VALIDA = "password123"

    private val clienteValido = Client().apply {
        username = USERNAME_VALIDO
        password = PASSWORD_VALIDA
        firstName = "Test"
        lastName = "User"
        email = "test@example.com"
    }

    private val dtoValido = AuthDTO(
        id = CLIENT_ID,
        username = USERNAME_VALIDO,
        password = PASSWORD_VALIDA
    )

    @Test
    fun `toDTO deberia mapear correctamente todos los campos de Client a AuthDTO`() {

        val resultadoDTO = AuthMapper.toDTO(clienteValido)


        assertNotNull(resultadoDTO)
        assertEquals(USERNAME_VALIDO, resultadoDTO.username)
        assertEquals(PASSWORD_VALIDA, resultadoDTO.password)
    }

    @Test
    fun `toEntity deberia mapear un AuthDTO valido a una nueva instancia de Client`() {

        val resultadoClient = AuthMapper.toEntity(dtoValido)


        assertNotNull(resultadoClient)
        assertEquals(USERNAME_VALIDO, resultadoClient.username)
        assertEquals(PASSWORD_VALIDA, resultadoClient.password)


        assertEquals(0, resultadoClient.id)
    }

    @Test
    fun `toEntity deberia lanzar BusinessException si el username es un string vacio`() {
        val dtoInvalido = dtoValido.copy(username = "")


        val excepcion = assertThrows<BusinessException> {
            AuthMapper.toEntity(dtoInvalido)
        }

        assertEquals("El usuario no puede estar vacío", excepcion.message)
    }

    @Test
    fun `toEntity deberia lanzar BusinessException si el username es solo espacios en blanco`() {
        val dtoInvalido = dtoValido.copy(username = "   ")

        val excepcion = assertThrows<BusinessException> {
            AuthMapper.toEntity(dtoInvalido)
        }

        assertEquals("El usuario no puede estar vacío", excepcion.message)
    }

    @Test
    fun `toEntity deberia lanzar BusinessException si la contrasena tiene menos de 6 caracteres`() {
        val passwordCorta = "abcde"
        val dtoInvalido = dtoValido.copy(password = passwordCorta)

        val excepcion = assertThrows<BusinessException> {
            AuthMapper.toEntity(dtoInvalido)
        }

        assertEquals("La contraseña debe tener al menos 6 caracteres", excepcion.message)
    }

    @Test
    fun `toEntity deberia aceptar una contrasena exactamente de 6 caracteres`() {

        val passwordValidaCorta = "abcdef"
        val dtoValidoEnLimite = dtoValido.copy(password = passwordValidaCorta)


        val resultadoClient = AuthMapper.toEntity(dtoValidoEnLimite)

        assertEquals(passwordValidaCorta, resultadoClient.password)
    }
}