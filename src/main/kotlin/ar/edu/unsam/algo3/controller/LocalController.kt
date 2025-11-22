package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.domain.Client
import ar.edu.unsam.algo3.domain.Local
import ar.edu.unsam.algo3.dto.LocalAccountDTO
import ar.edu.unsam.algo3.dto.LocalDTO
import ar.edu.unsam.algo3.dto.LocalCheckoutDTO
import ar.edu.unsam.algo3.exceptions.BusinessException
import ar.edu.unsam.algo3.mapper.LocalMapper
import ar.edu.unsam.algo3.service.LocalService
import ar.edu.unsam.algo3.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/local")
@CrossOrigin(origins = [
    "http://localhost:5173",
    "http://localhost:5174"
])
class LocalController(private val localService: LocalService, private val userService: UserService) {

    // GET /local/{id}
    @GetMapping("/admin/{id}")
    fun getAdminById(@PathVariable id: Int): LocalAccountDTO {
        val local = localService.getById(id)
        return LocalMapper.toLocalAccountDTO(local)
    }

    @PostMapping("/admin")
    fun createLocal(@RequestBody dto: LocalAccountDTO): ResponseEntity<Map<String, Int>> {
        val local = LocalMapper.toNewEntity(dto)
        val created = localService.create(local)
        return ResponseEntity.status(HttpStatus.CREATED).body(mapOf("id" to created.id!!))
    }

    // PUT /local/{id}
    @PutMapping("/{id}")
    fun update(@PathVariable id: Int, @RequestBody dto: LocalAccountDTO) {
        val local = localService.getById(id)
        val updatedLocal = LocalMapper.toEntity(dto, local)
        localService.update(updatedLocal)
    }

    // GET /local
    @GetMapping("/profile")
    fun getAll(): List<LocalAccountDTO> {
        return localService.getAll().map { LocalMapper.toLocalAccountDTO(it) }
    }

    @GetMapping
    fun search(@RequestParam(required = false) query: String, @RequestParam(required = false) userId: Int): List<LocalDTO> {
        val locales = localService.search(query)

        val user = userService.getById(userId)
        val client = user as? Client
            ?: throw BusinessException("Solo los clientes pueden calcular distancia a los locales")
        return locales.map { local ->
            val esCercano = user.isLocalNearby(local)
            //esto lo tendría que hacer el controller
            LocalMapper.toLocalDTO(local).copy(esCercano = esCercano) }
    }


    @GetMapping("/{id}")
    fun getPublicById(@PathVariable id: Int): LocalDTO {
        val local = localService.getById(id)
        return LocalMapper.toLocalDTO(local)
    }

    @GetMapping("/order-checkout/{id}")
    fun getCheckoutInfo(
        @PathVariable id: Int,
        @RequestParam(required = false) userId: Int?
    ): LocalCheckoutDTO {
        val local = localService.getById(id)
        val distanceKm = userId?.let { localService.getDistanceToClient(local, it) }

        return LocalMapper.toCheckoutDTO(local, distanceKm)
    }
}
