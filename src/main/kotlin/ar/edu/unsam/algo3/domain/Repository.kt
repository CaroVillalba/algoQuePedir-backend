package ar.edu.unsam.algo3.domain
import org.springframework.stereotype.Repository

//Repositorio para cada una de las entidades
//Deberia haber una entidad global de cada tipo de repositorio, y en este se deben
//guardar todas las entidades disponibles.
abstract class InMemoryRepository<T: Entity> {
    val elements = mutableMapOf<Int, T>()
    var idDisponible = 1

    fun create(item: T) {
        item.id = idDisponible
        elements[idDisponible] = item
        idDisponible += 1
    }

    fun delete(item: T) {
        elements.remove(item.id)
    }

    fun update(item: T) {
        elements[item.id] = item
    }

    fun getById(id: Int): T? = elements[id]

    fun getIds(): List<Int> = elements.keys.toList()

    fun getSize(): Int = elements.size

    abstract fun search(value: String): List<T>
}

open class Entity (
    var id: Int = 0
)

@Repository
class ClienteRepository : InMemoryRepository<Cliente>() {

    fun buscarNombre(value: String): List<Cliente> =  elements.values.filter { cliente -> cliente.nombre.contains(value, ignoreCase = true)}
    fun buscarApellido(value: String): List<Cliente> = elements.values.filter { cliente ->cliente.apellido.contains(value, ignoreCase = true)}
    fun buscarUsername(value: String): List<Cliente> = elements.values.filter { cliente -> cliente.username.equals(value, ignoreCase = true)}


    override fun search(value: String): List<Cliente> {
        return (buscarUsername(value) + buscarApellido(value) + buscarNombre(value)).distinct()
    }
}

@Repository
class LocalRepository : InMemoryRepository<Local>() {
    fun buscarNombre(value: String): List<Local> = elements.values.filter { local -> local.nombre.contains(value, ignoreCase = true)}
    fun buscarDireccion(value: String): List<Local> = elements.values.filter { local -> local.direccion.equals(value)}

    override fun search(value: String): List<Local> {
        return (buscarNombre(value) + buscarDireccion(value)).distinct()
    }
}

class DeliveryRepository : InMemoryRepository<Delivery>() {
    override fun search(value: String): List<Delivery> {
        return elements.values.filter { delivery -> delivery.username.startsWith(value, ignoreCase = true)  }.toList()
    }

}
class IngredienteRepository : InMemoryRepository<Ingrediente>() {
    override fun search(value: String): List<Ingrediente> {
        return elements.values.filter { ingrediente -> ingrediente.nombre.equals(value, ignoreCase = true) }.toList()
    }
}

open class PlatoRepository : InMemoryRepository<Plato>() {

    fun descripcionContiene(value: String): List<Plato> = elements.values.filter { plato -> plato.descripcion.contains(value, ignoreCase = true)}
    fun buscarNombreLocal(value: String): List<Plato> = elements.values.filter { plato -> plato.local.nombre.contains(value, ignoreCase = true) }
    fun buscarDireccionLocal(value: String): List<Plato> = elements.values.filter { plato -> plato.local.direccion.calle.equals(value, ignoreCase = true) }

    override fun search(value: String): List<Plato> {
        return (descripcionContiene(value) + buscarNombreLocal(value) + buscarDireccionLocal(value)).distinct()
    }
}

class CuponRepository : InMemoryRepository<Cupon>() {
    override fun search(value: String): List<Cupon> {
        val porcentajeBuscado = value.toDouble()
        return elements.values.filter { cupon -> cupon.porcentajeBaseDescuento == (porcentajeBuscado) }.toList()
    }

}