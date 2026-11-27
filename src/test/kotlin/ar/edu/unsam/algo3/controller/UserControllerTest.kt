package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.dto.AuthDTO
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

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Dado un UserController")
class UserControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    private lateinit var authDTO: AuthDTO

    @BeforeEach
    fun setup() {
        authDTO = AuthDTO(1, "juan123", "password123", localId = null)
    }

    @Test
    fun `POST v1 auth register debe crear un usuario y devolver 201 CREATED`() {
        val json = objectMapper.writeValueAsString(authDTO)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
    }

    @Test
    fun `GET v1 auth login debe devolver 200 OK y DTO`() {
        val json = objectMapper.writeValueAsString(authDTO)
        mockMvc.perform(
            MockMvcRequestBuilders.post("/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )

        mockMvc.perform(
            MockMvcRequestBuilders.get("/v1/auth/login")
                .param("username", authDTO.username)
                .param("password", authDTO.password)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(authDTO.username))
    }

    @Test
    fun `GET v1 auth login con credenciales incorrectas devuelve 400`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/v1/auth/login")
                .param("username", "noexiste")
                .param("password", "1234")
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Usuario o contraseña incorrectos"))
    }
}
