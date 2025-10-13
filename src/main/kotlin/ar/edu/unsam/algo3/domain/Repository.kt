package ar.edu.unsam.algo3.domain
import ar.edu.unsam.algo3.dto.AuthDTO
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

    fun getAll(): List<T> = elements.values.toList()

    abstract fun search(value: String): List<T>
}

open class Entity (
    var id: Int = 0
)

@Repository
class ClienteRepository : InMemoryRepository<Cliente>() {

    fun buscarNombre(value: String): List<Cliente> =  elements.values.filter { cliente -> cliente.nombre.contains(value, ignoreCase = true)}
    fun buscarApellido(value: String): List<Cliente> = elements.values.filter { cliente ->cliente.apellido.contains(value, ignoreCase = true)}
    fun buscarPassword(value: String): List<Cliente> = elements.values.filter { cliente -> cliente.password.equals(value, ignoreCase = true)}
    fun buscarUsername(value: String): List<Cliente> = elements.values.filter { cliente ->cliente.username.contains(value, ignoreCase = true)}

    override fun search(value: String): List<Cliente> {
        return (buscarPassword(value) + buscarUsername(value)).distinct()
    }

    fun searchCredenciales(value: String, password : String ): Cliente {
        val clientesPorUsername = buscarUsername(value)
        return clientesPorUsername.filter { cliente -> cliente.password == password }.first()
    }

}

@Repository
class LocalRepository : InMemoryRepository<Local>() {
    fun buscarNombre(value: String): List<Local> = elements.values.filter { local -> local.name.contains(value, ignoreCase = true)}
    fun buscarDireccion(value: String): List<Local> = elements.values.filter { local -> local.address.equals(value)}

    override fun search(value: String): List<Local> {
        return (buscarNombre(value) + buscarDireccion(value)).distinct()
    }
}
@Repository
class DeliveryRepository : InMemoryRepository<Delivery>() {
    override fun search(value: String): List<Delivery> {
        return elements.values.filter { delivery -> delivery.username.startsWith(value, ignoreCase = true)  }.toList()
    }
}

/*ATENCION!!: Estoy creando el repositorio porque no estaba. ¿Podré usar alguno de los que ya están?*/
@Repository
class PedidoRepository : InMemoryRepository<Pedido>() {
    override fun search(value: String): List<Pedido> {
        // vacio
        return listOf()
    }
}

@Repository
class IngredienteRepository : InMemoryRepository<Ingrediente>() {
    override fun search(value: String): List<Ingrediente> {
        return elements.values.filter { ingrediente -> ingrediente.name.equals(value, ignoreCase = true) }.toList()
    }
}
@Repository
open class PlatoRepository : InMemoryRepository<Plato>() {

    fun descripcionContiene(value: String): List<Plato> = elements.values.filter { plato -> plato.descripcion.contains(value, ignoreCase = true)}
    fun buscarNombreLocal(value: String): List<Plato> = elements.values.filter { plato -> plato.local.name.contains(value, ignoreCase = true) }
    fun buscarDireccionLocal(value: String): List<Plato> = elements.values.filter { plato -> plato.local.address.calle.equals(value, ignoreCase = true) }

    override fun search(value: String): List<Plato> {
        return (descripcionContiene(value) + buscarNombreLocal(value) + buscarDireccionLocal(value)).distinct()
    }
}
@Repository
class CuponRepository : InMemoryRepository<Cupon>() {
    override fun search(value: String): List<Cupon> {
        val porcentajeBuscado = value.toDouble()
        return elements.values.filter { cupon -> cupon.porcentajeBaseDescuento == (porcentajeBuscado) }.toList()
    }
}