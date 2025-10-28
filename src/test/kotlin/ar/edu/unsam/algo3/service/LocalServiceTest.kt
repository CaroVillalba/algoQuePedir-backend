package ar.edu.unsam.algo3.service

import ar.edu.unsam.algo3.domain.Address
import ar.edu.unsam.algo3.domain.Local
import ar.edu.unsam.algo3.domain.LocalRepository
import ar.edu.unsam.algo3.domain.PaymentMethod
import ar.edu.unsam.algo3.exceptions.ElementDoesNotExistException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.uqbar.geodds.Point

@ExtendWith(MockitoExtension::class)
@DisplayName("Dado un LocalService")
class LocalServiceTest {

    @Mock
    lateinit var localRepository: LocalRepository

    @InjectMocks
    lateinit var localService: LocalService

    lateinit var local: Local

    @BeforeEach
    fun setup() {
        val address = Address("Calle Falsa", "123", Point(10.0, 20.0))
        local = Local(
            "Local Test",
            "http://imagen.com/logo.png",
            address,
            10.0,
            5.0,
            mutableSetOf(PaymentMethod.EFECTIVO)
        ).apply { id = 1 }
    }

    @Test
    @DisplayName("Se puede obtener un local existente por ID")
    fun `sePuedeObtenerUnLocalExistentePorID`() {
        whenever(localRepository.getById(1)).thenReturn(local)

        val localEncontrado = localService.getById(1)

        assert(localEncontrado.name == "Local Test")
        verify(localRepository, times(1)).getById(1)
    }

    @Test
    @DisplayName("Obtener un local con ID inexistente lanza ElementDoesNotExistException")
    fun `obtenerLocalInexistenteLanzaException`() {
        whenever(localRepository.getById(99)).thenReturn(null)

        assertThrows<ElementDoesNotExistException> {
            localService.getById(99)
        }
    }

    @Test
    @DisplayName("Se puede actualizar un local")
    fun `sePuedeActualizarUnLocal`() {
        localService.update(local)

        verify(localRepository, times(1)).update(local)
    }
}