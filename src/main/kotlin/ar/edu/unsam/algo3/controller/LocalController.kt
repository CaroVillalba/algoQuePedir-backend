package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.dto.LocalAccountDTO
import ar.edu.unsam.algo3.dto.LocalDTO
import ar.edu.unsam.algo3.dto.LocalCheckoutDTO
import ar.edu.unsam.algo3.mapper.LocalMapper
import ar.edu.unsam.algo3.service.LocalService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/local")
@CrossOrigin(origins = [
    "http://localhost:5173",
    "http://localhost:5174"
])
class LocalController(private val localService: LocalService) {

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
    /*@GetMapping()
    fun getAll(): List<LocalAccountDTO> {
        return localService.getAll().map { LocalMapper.toLocalAccountDTO(it) }
    }*/

    @GetMapping
    fun getAll(@RequestParam(required = false) query: String?, @RequestParam(required = false) userId: Int?): List<LocalDTO> {

        //manejar esta logica en el repo
        val locales = if (query.isNullOrBlank()) {
            localService.getAll()
        } else {
            localService.search(query)
        }
        return if (userId != null) {
            localService.addDistanceInfo(locales, userId)
        } else {
            locales.map { LocalMapper.toLocalDTO(it) }
        }
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
