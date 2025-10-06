package ar.edu.unsam.algo2
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


interface ServiceIngredientes{
    fun getIngredientes(): String
}

data class DataIngrediente(
    val id: Int = -1,
    val nombre: String = "",
    val costo: Double = 0.0,
    val grupo: String = "",
    val origenAnimal: Boolean = false

)


class UpdaterIngredientes(

    var service:ServiceIngredientes,
    var repo: IngredienteRepository
){
    fun getDataIngredientes(): List<DataIngrediente> {
        val gson = Gson()
        var json_data: String = service.getIngredientes()
        val typeToken = object : TypeToken<List<DataIngrediente>>() {}.type
        var dataIngredientes = gson.fromJson<List<DataIngrediente>>(json_data, typeToken)
        return dataIngredientes
    }

    fun updateIngredientes() {
        val dataIngredientes = getDataIngredientes()

        dataIngredientes.forEach {
            if (repo.getById(it.id) == null) {
                repo.create(Ingrediente(
                        it.nombre,
                        it.costo,
                        GrupoAlimentos.valueOf(it.grupo)
                ))
            }
            else {
                repo.update(Ingrediente(
                    it.nombre,
                    it.costo,
                    GrupoAlimentos.valueOf(it.grupo),
                ))
            }
        }
    }
}