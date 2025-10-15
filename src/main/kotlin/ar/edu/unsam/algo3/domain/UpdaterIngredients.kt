package ar.edu.unsam.algo3.domain
import com.google.gson.Gson

interface IngredientsService {
    fun getIngredients(): String
}

data class IngredientData(
    val id: Int = -1,
    val name: String = "",
    val cost: Double = 0.0,
    val group: String = "",
    val animalOrigin: Boolean = false
)

class IngredientsUpdater(
    var service: IngredientsService,
    var repo: IngredientRepository
) {
    fun getIngredientsData(): List<IngredientData> {
        val gson = Gson()
        val jsonData: String = service.getIngredients()
        val typeToken = object : com.google.gson.reflect.TypeToken<List<IngredientData>>() {}.type
        val ingredientDataList = gson.fromJson<List<IngredientData>>(jsonData, typeToken)
        return ingredientDataList
    }

    fun updateIngredients() {
        val ingredientDataList = getIngredientsData()

        ingredientDataList.forEach {
            if (repo.getById(it.id) == null) {
                repo.create(
                    Ingredient(
                        it.name,
                        it.cost,
                        FoodGroups.valueOf(it.group)
                    )
                )
            } else {
                repo.update(
                    Ingredient(
                        it.name,
                        it.cost,
                        FoodGroups.valueOf(it.group),
                    )
                )
            }
        }
    }
}
