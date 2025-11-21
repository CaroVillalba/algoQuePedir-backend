package ar.edu.unsam.algo3.domain
import org.springframework.stereotype.Repository

//Repositorio para cada una de las entidades
//Deberia haber una entidad global de cada tipo de repositorio, y en este se deben
//guardar todas las entidades disponibles.
abstract class InMemoryRepository<T: Entity> {
    val elements = mutableMapOf<Int, T>()
    var nextId = 1

    fun create(item: T) {
        item.id = nextId
        elements[nextId] = item
        nextId += 1
    }

    fun delete(item: T) {
        elements.remove(item.id)
    }

    fun update(item: T) {
        elements[item.id as Int] = item
    }

    fun getById(id: Int?): T? = elements[id]

    fun getIds(): List<Int> = elements.keys.toList()

    fun getSize(): Int = elements.size

    fun getAll(): List<T> = elements.values.toList()

    abstract fun search(value: String): List<T>
}

abstract class Entity(
    open var id: Int? =0
)

@Repository
class ClientRepository : InMemoryRepository<Client>() {

    fun searchByName(value: String): List<Client> = elements.values.filter { it.firstName.contains(value, ignoreCase = true) }
    fun searchByLastName(value: String): List<Client> = elements.values.filter { it.lastName.contains(value, ignoreCase = true) }
    fun searchByPassword(value: String): List<Client> = elements.values.filter { it.password.equals(value, ignoreCase = true) }
    fun searchByUsername(value: String): List<Client> = elements.values.filter { it.username.contains(value, ignoreCase = true) }

    override fun search(value: String): List<Client> {
        return (searchByName(value) + searchByLastName(value) + searchByUsername(value)).distinct()
    }
}

@Repository
class LocalRepository : InMemoryRepository<Local>() {
    fun searchByName(value: String): List<Local> = elements.values.filter { it.name.contains(value, ignoreCase = true) }
    fun searchByAddress(value: String): List<Local> = elements.values.filter { it.address.fullAddress().contains(value, ignoreCase = true) }

    //que devuelva todo si está vacío ' get all
    override fun search(value: String): List<Local> {
        return (searchByName(value) + searchByAddress(value)).distinct()
    }
}

@Repository
class DeliveryRepository : InMemoryRepository<Delivery>() {
    override fun search(value: String): List<Delivery> {
        return elements.values.filter { it.username.startsWith(value, ignoreCase = true) }.toList()
    }
}

/*ATENCION!!: Estoy creando el repositorio porque no estaba. ¿Podré usar alguno de los que ya están?*/
@Repository
class OrderRepository : InMemoryRepository<Order>() {
    override fun search(value: String): List<Order> {
        val status = try {
            OrderStatus.valueOf(value.uppercase())
        } catch (e: IllegalArgumentException) {
            return emptyList()  //ojo con esto
        }
        return elements.values.filter { it.status == status }
    }
}

@Repository
class IngredientRepository : InMemoryRepository<Ingredient>() {
    override fun search(value: String): List<Ingredient> {
        return elements.values.filter { it.name.equals(value, ignoreCase = true) }.toList()
    }
}

@Repository
open class DishRepository : InMemoryRepository<Dish>() {

    fun descriptionContains(value: String): List<Dish> = elements.values.filter { it.description.contains(value, ignoreCase = true) }
    fun searchByLocalName(value: String): List<Dish> = elements.values.filter { it.local.name.contains(value, ignoreCase = true) }
    fun searchByLocalAddress(value: String): List<Dish> = elements.values.filter { it.local.address.street.equals(value, ignoreCase = true) }

    override fun search(value: String): List<Dish> {
        return (descriptionContains(value) + searchByLocalName(value) + searchByLocalAddress(value)).distinct()
    }

    fun findByLocal(localId: Int): List<Dish> =
        elements.values.filter { it.local.id == localId }
}

@Repository
class CouponRepository : InMemoryRepository<Coupon>() {
    override fun search(value: String): List<Coupon> {
        val searchedPercentage = value.toDouble()
        return elements.values.filter { it.baseDiscountRate == searchedPercentage }.toList()
    }
}

/*
@Repository
class RatingRepository : InMemoryRepository<Rating>() {
    override fun search(value: String): List<Rating> {

    }
}

 */