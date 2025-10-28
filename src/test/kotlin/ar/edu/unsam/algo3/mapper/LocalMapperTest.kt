package ar.edu.unsam.algo3.mapper

import ar.edu.unsam.algo3.domain.Address
import ar.edu.unsam.algo3.domain.Local
import ar.edu.unsam.algo3.domain.PaymentMethod
import ar.edu.unsam.algo3.dto.AddressDTO
import ar.edu.unsam.algo3.dto.LocalAccountDTO
import ar.edu.unsam.algo3.exceptions.BusinessException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.uqbar.geodds.Point

@DisplayName("Dado el LocalMapper")
class LocalMapperTest {

    private lateinit var local: Local
    private lateinit var dto: LocalAccountDTO
    private lateinit var addressDTO: AddressDTO

    private val baseAddress = Address("Calle Falsa", "123", Point(10.0, 20.0))
    private val basePaymentMethods = mutableSetOf(PaymentMethod.EFECTIVO, PaymentMethod.QR)

    @BeforeEach
    fun setup() {
        local = Local(
            "Local Test",
            "http://imagen.com/logo.png",
            baseAddress,
            10.0,
            5.0,
            basePaymentMethods.toMutableSet()
        ).apply { id = 1 }

        addressDTO = AddressDTO(
            "Calle Falsa",
            "123",
            Point(10.0, 20.0)
        )

        dto = LocalAccountDTO(
            1,
            "Local DTO",
            "http://imagen.com/logo.png",
            addressDTO,
            10.0,
            5.0,
            listOf("efectivo", "qr")
        )
    }

    @Test
    @DisplayName("Mapeo de Entidad a DTO es exitoso")
    fun `mapeo de Entidad a DTO exitoso`() {
        val mappedDTO = LocalMapper.toDTO(local)

        assert(mappedDTO.id == 1)
        assert(mappedDTO.name == "Local Test")
        assert(mappedDTO.address.coordinates.x == 10.0)
        assert(mappedDTO.address.coordinates.y == 20.0)
        assert(mappedDTO.paymentMethods.contains("efectivo"))
        assert(mappedDTO.paymentMethods.contains("qr"))
        assert(mappedDTO.paymentMethods.size == 2)
    }

    @Test
    @DisplayName("Mapeo a DTO filtra métodos de pago no soportados por el frontend")
    fun `mapeo a DTO filtra metodos de pago no soportados`() {
        val localConDebito = Local(
            "Local Test",
            "http://imagen.com/logo.png",
            baseAddress,
            10.0,
            5.0,
            mutableSetOf(PaymentMethod.EFECTIVO, PaymentMethod.QR, PaymentMethod.DEBITO)
        ).apply { id = 1 }

        val mappedDTO = LocalMapper.toDTO(localConDebito)

        assert(mappedDTO.paymentMethods.size == 2)
        assert(!mappedDTO.paymentMethods.contains("debito"))
    }

    @Test
    @DisplayName("Mapeo de DTO a Entidad es exitoso")
    fun `mapeo de DTO a Entidad exitoso`() {
        val existingLocal = Local(
            "Local Test",
            "http://imagen.com/logo.png",
            baseAddress,
            10.0,
            5.0,
            basePaymentMethods.toMutableSet()
        ).apply { id = 1 }

        val updatedLocal = LocalMapper.toEntity(dto, existingLocal)

        assert(updatedLocal.id == existingLocal.id)
        assert(updatedLocal.name == "Local DTO")
        assert(updatedLocal.address.coordinates.x == 10.0)
        assert(updatedLocal.address.coordinates.y == 20.0)
        assert(updatedLocal.paymentMethods.contains(PaymentMethod.EFECTIVO))
        assert(updatedLocal.paymentMethods.contains(PaymentMethod.QR))
    }

    @Test
    @DisplayName("Mapeo a Entidad falla si un método de pago es inválido")
    fun `mapeo a Entidad falla con metodo de pago invalido`() {
        val invalidDTO = dto.copy(paymentMethods = listOf("efectivo", "tarjeta_de_credito_invalida"))

        assertThrows<IllegalArgumentException> {
            LocalMapper.toEntity(invalidDTO, local)
        }
    }

    @Test
    @DisplayName("Validación falla si el nombre está vacío")
    fun `validacion falla nombre vacio`() {
        val invalidDTO = dto.copy(name = "  ")
        assertThrows<BusinessException>("El nombre no puede estar vacío") {
            LocalMapper.validateDTO(invalidDTO)
        }
    }

    @Test
    @DisplayName("Validación falla si la URL de imagen es inválida (no http)")
    fun `validacion falla url invalida`() {
        val invalidDTO = dto.copy(imgURL = "ftp://imagen.com/logo.png")
        assertThrows<BusinessException>("URL de la imagen inválida") {
            LocalMapper.validateDTO(invalidDTO)
        }
    }

    @Test
    @DisplayName("Validación falla si la calle está vacía")
    fun `validacion falla calle vacia`() {
        val invalidDTO = dto.copy(address = addressDTO.copy(street = ""))
        assertThrows<BusinessException>("Calle inválida") {
            LocalMapper.validateDTO(invalidDTO)
        }
    }

    @Test
    @DisplayName("Validación falla si el número de calle es inválido (cero)")
    fun `validacion falla numero de calle invalido cero`() {
        val invalidDTO = dto.copy(address = addressDTO.copy(number = "0"))
        assertThrows<BusinessException>("Número de calle inválido") {
            LocalMapper.validateDTO(invalidDTO)
        }
    }

    @Test
    @DisplayName("Validación falla si el número de calle es inválido (negativo)")
    fun `validacion falla numero de calle invalido negativo`() {
        val invalidDTO = dto.copy(address = addressDTO.copy(number = "-1"))
        assertThrows<BusinessException>("Número de calle inválido") {
            LocalMapper.validateDTO(invalidDTO)
        }
    }

    @Test
    @DisplayName("Validación falla si la latitud (x) está fuera de rango")
    fun `validacion falla latitud (x) fuera de rango`() {
        // 🔽 CORREGIDO: Creamos un nuevo Point con la coordenada inválida
        val invalidPoint = Point(91.0, 20.0)
        val invalidDTO = dto.copy(address = addressDTO.copy(coordinates = invalidPoint))
        assertThrows<BusinessException>("Latitud inválida") {
            LocalMapper.validateDTO(invalidDTO)
        }
    }

    @Test
    @DisplayName("Validación falla si la longitud (y) está fuera de rango")
    fun `validacion falla longitud (y) fuera de rango`() {
        // 🔽 CORREGIDO: Creamos un nuevo Point con la coordenada inválida
        val invalidPoint = Point(10.0, -181.0)
        val invalidDTO = dto.copy(address = addressDTO.copy(coordinates = invalidPoint))
        assertThrows<BusinessException>("Longitud inválida") {
            LocalMapper.validateDTO(invalidDTO)
        }
    }

    @Test
    @DisplayName("Validación falla si la comisión de App es inválida (mayor a 100)")
    fun `validacion falla comision app invalida`() {
        val invalidDTO = dto.copy(appCommission = 101.0)
        assertThrows<BusinessException>("Comisión App inválida") {
            LocalMapper.validateDTO(invalidDTO)
        }
    }

    @Test
    @DisplayName("Validación falla si la comisión de Autores es inválida (menor a 0)")
    fun `validacion falla comision autores invalida`() {
        val invalidDTO = dto.copy(authorCommission = -1.0)
        assertThrows<BusinessException>("Comisión autores inválida") {
            LocalMapper.validateDTO(invalidDTO)
        }
    }

    @Test
    @DisplayName("Validación falla si no hay métodos de pago")
    fun `validacion falla sin metodos de pago`() {
        val invalidDTO = dto.copy(paymentMethods = emptyList())
        assertThrows<BusinessException>("Debe seleccionar al menos un método de pago") {
            LocalMapper.validateDTO(invalidDTO)
        }
    }
}