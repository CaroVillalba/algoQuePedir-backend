package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.domain.*
import ar.edu.unsam.algo3.dto.AddressDTO
import ar.edu.unsam.algo3.dto.LocalAccountDTO
import ar.edu.unsam.algo3.service.UserService
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
    lateinit var userService: UserService

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
            AddressDTO(
                local.address.street,
                local.address.number,
                local.address.coordinates
            ),
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

    @Test
    fun `GET v1 local admin {id} debe devolver un LocalAccountDTO con 200 OK`() {
        val id = local.id!!

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/local/admin/$id"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(local.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.appCommission").value(local.appCommission))
    }

    @Test
    fun `POST v1 local admin debe crear un local y devolver 201 Created con id`() {
        val newDTO = LocalAccountDTO(
            id = null,
            name = "Local Nuevo",
            imgURL = "http://imagen.com/otro.png",
            address = AddressDTO("Calle", "100", Point(5.0, 5.0)),
            appCommission = 8.0,
            authorCommission = 3.0,
            paymentMethods = listOf("efectivo")
        )

        val json = objectMapper.writeValueAsString(newDTO)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/v1/local/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
    }

    @Test
    fun `GET v1 local profile debe devolver lista de locales`() {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/local/profile"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(local.name))
    }

    @Test
    fun `GET v1 local public {id} debe devolver LocalDTO`() {
        val id = local.id!!

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/local/$id"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(local.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.imgURL").value(local.imgURL))
    }

    @Test
    fun `GET v1 local search debe devolver locales con esCercano calculado`() {
        val client = Client(
            firstname = "Carlos",
            email = "c@c.com",
            password = "1234",
            address = Address("C", "1", Point(10.0, 20.0))
        )
        localRepository.create(local)


        userService.createUser(client)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/v1/local")
                .param("query", "")
                .param("userId", client.id!!.toString())
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value(local.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$[0].esCercano").value(true))
    }

    @Test
    fun `GET v1 local order-checkout con userId debe incluir distancia`() {
        val client = Client(
            firstname = "Cliente",
            email = "cli@c.com",
            password = "1234",
            address = Address("X", "1", Point(10.0, 20.0))
        )
        userService.createUser(client)

        val id = local.id!!

        mockMvc.perform(
            MockMvcRequestBuilders.get("/v1/local/order-checkout/$id")
                .param("userId", client.id.toString())
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(local.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.distanceKm").exists())
    }

    @Test
    fun `GET v1 local order-checkout sin userId no debe incluir distancia`() {
        val id = local.id!!

        mockMvc.perform(MockMvcRequestBuilders.get("/v1/local/order-checkout/$id"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(local.name))
            .andExpect(MockMvcResultMatchers.jsonPath("$.distanceKm").doesNotExist())
    }


}
