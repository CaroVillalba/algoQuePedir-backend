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
        firstname = "Test"
        lastname = "User"
        email = "test@example.com"
    }

    private val dtoValido = AuthDTO(
        id = CLIENT_ID,
        username = USERNAME_VALIDO,
        password = PASSWORD_VALIDA,
        localId = null
    )

    @Test
    fun `toDTO deberia mapear correctamente todos los campos de Client a AuthDTO`() {

        val resultadoDTO = AuthMapper.toDTO(clienteValido)


        assertNotNull(resultadoDTO)
        assertEquals(USERNAME_VALIDO, resultadoDTO.username)
        assertEquals(PASSWORD_VALIDA, resultadoDTO.password)
    }

}