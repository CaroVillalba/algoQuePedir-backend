package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.dto.AddressDTO
import ar.edu.unsam.algo3.dto.LocalAccountDTO
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.uqbar.geodds.Point

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Dado un LocalController")
class LocalControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var localRepository: LocalRepository

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private lateinit var local: Local
    private lateinit var localDTO: LocalAccountDTO

    @BeforeEach
    fun setup() {
        // Limpiar repositorio
        localRepository.getAll().forEach { localRepository.delete(it) }

        val address = Address("Calle Falsa", "123", Point(10.0, 20.0))
        local = Local(
            "Local Test",
            "http://imagen.com/logo.png",
            address,
            10.0,
            5.0,
            mutableSetOf(PaymentMethod.EFECTIVO)
        )
        localRepository.create(local)

        localDTO = LocalAccountDTO(
            1,
            local.name,
            local.imgURL,
            AddressDTO(local.address.street, local.address.number, local.address.coordinates.x, local.address.coordinates.y),
            local.appCommission,
            local.authorCommission,
            local.paymentMethods.map { it.name.lowercase() }
        )
    }

    @Test
    fun `GET v1 local {id} debe devolver 200 OK`() {
        val id = local.id
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/local/$id"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Local Test"))
    }

    @Test
    fun `PUT v1 local {id} debe actualizar el local y devolver 200 OK`() {
        val id = local.id
        // Modificar DTO
        val updatedDTO = localDTO.copy(name = "Local Actualizado")
        val json = objectMapper.writeValueAsString(updatedDTO)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/v1/local/$id")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(MockMvcResultMatchers.status().isOk)

        // Verificar que se actualizó en el repositorio
        val updatedLocal = localRepository.getById(id)
        assert(updatedLocal!!.name == "Local Actualizado")
    }
}
