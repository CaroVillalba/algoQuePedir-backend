package ar.edu.unsam.algo3.bootstrap

import ar.edu.unsam.algo3.domain.*
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ar.edu.unsam.algo3.dto.LocalAccountDTO
import ar.edu.unsam.algo3.dto.PlatoListDTO
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import org.uqbar.geodds.Point
import java.io.File
import java.time.LocalDate

@Component
class DataInitializer(
    private val localRepository: LocalRepository,
    private val platoRepository: PlatoRepository,
    private val ingredienteRepository: IngredienteRepository
) {
    private val mapper = jacksonObjectMapper()

    @PostConstruct
    fun init() {
        val file = File("src/main/resources/localData.json")
        val localDTO = mapper.readValue<LocalAccountDTO>(file)

        val fileMenu = File("src/main/resources/menuData.json")
        val platoListDTO = mapper.readValue<PlatoListDTO>(fileMenu)

        val fileIngredients = File("src/main/resources/ingredientsData.json")
        val ingredients = mapper.readValue<List<Ingrediente>>(fileIngredients)


        val local = Local(
            name = localDTO.nombre,
            imgURL = localDTO.imgURL,
            address = Direccion(
                calle = localDTO.direccion.calle,
                altura = localDTO.direccion.altura,
                coordenadas = Point(localDTO.direccion.lat, localDTO.direccion.lon)
            ),
            comisionApp = localDTO.comisionApp,
            comisionAuthor = localDTO.comisionAutor,
            paymentMethods = localDTO.metodosDePago.map { MedioDePago.valueOf(it.uppercase()) }.toMutableSet(),
            ratings = mutableSetOf()
        )

        localRepository.create(local)

        println("✅ Local cargado desde JSON: ${local.name}")

        // ------------------------------------------------------------------
        // Accedemos a la lista de DTOs y realizamos el mapeo manual
        platoListDTO.platos.forEach { platoDTO ->

            // Mapeo de Ingredientes de DTO a Entidad
            val ingredientesEntidad = platoDTO.ingredientes.map { ingDTO ->
                Ingrediente(
                    name = ingDTO.name,
                    price = ingDTO.price,
                    group = GrupoAlimentos.valueOf(ingDTO.group)
                )
            }.toMutableList()

            // Mapeo a la entidad Plato, INYECTANDO el 'local'
            val platoEntidad = Plato(
                imageURL = platoDTO.imageURL,
                nombre = platoDTO.nombre,
                descripcion = platoDTO.descripcion,
                ingredientes = ingredientesEntidad,
                local = local, // 👈 Se inyecta el objeto Local ya existente
                porcentajeAcordadoConLocal = platoDTO.porcentajeAcordadoConLocal,
                lanzamiento = LocalDate.now(), // 👈 Se inyecta la fecha actual
                regaliasAutor = platoDTO.regaliasAutor,
                valorBase = platoDTO.valorBase,
                descuento = platoDTO.descuento,
            )

            platoRepository.create(platoEntidad)
        }

        ingredients.forEach { ingrediente -> ingredienteRepository.create(ingrediente) }

    }
}
