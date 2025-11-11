package ar.edu.unsam.algo3.controller

import ar.edu.unsam.algo3.dto.LocalAccountDTO
import ar.edu.unsam.algo3.dto.LocalDTO
import ar.edu.unsam.algo3.dto.LocalCheckoutDTO
import ar.edu.unsam.algo3.mapper.LocalMapper
import ar.edu.unsam.algo3.service.LocalService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/local")
@CrossOrigin(origins = ["http://localhost:5173"])
class LocalController(private val localService: LocalService) {

    // GET /local/{id}
    @GetMapping("/admin/{id}")
    fun getAdminById(@PathVariable id: Int): LocalAccountDTO {
        val local = localService.getById(id)
        return LocalMapper.toLocalAccountDTO(local)
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
    fun getCheckoutInfo(@PathVariable id: Int): LocalCheckoutDTO {
        val local = localService.getById(id)
        return LocalMapper.toCheckoutDTO(local)
    }
}
