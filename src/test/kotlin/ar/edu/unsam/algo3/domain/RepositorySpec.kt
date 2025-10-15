package ar.edu.unsam.algo3.domain

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

fun compareIngredients(ing1: Ingredient, ing2: Ingredient): Boolean {
    return ing1.name == ing2.name &&
            ing1.price == ing2.price &&
            ing1.group == ing2.group
}

class IngredientServiceStub : IngredientsService {
    override fun getIngredients(): String {
        return """
            [
              {
                "id": 22,
                "name": "Leche",
                "cost": 200.5,
                "group": "LACTEOS",
                "animalOrigin": true
              },
              {
                "name": "Arroz",
                "cost": 100.5,
                "group": "CEREALES_Y_TUBERCULOS",
                "animalOrigin": false
              },
              {
                "id": 3,
                "name": "Azúcar",
                "cost": 200.0,
                "group": "AZUCARES_Y_DULCES",
                "animalOrigin": false
              },
              {
                "name": "Pollo",
                "cost": 400.5,
                "group": "PROTEINAS",
                "animalOrigin": true
              },
              {
                "id": 57,
                "name": "Manzana",
                "cost": 100.0,
                "group": "FRUTAS_Y_VERDURAS",
                "animalOrigin": false
              }
            ]
        """.trimIndent()
    }
}

class IngredientUpdaterSpec : DescribeSpec({
    describe("Funcionalidad del repositorio") {
        val ingredientRepo = IngredientRepository()
        val ingredient1 = Ingredient(
            "IngredienteAbstracto UUUUhhhhhh",
            200.5,
            FoodGroup.PROTEINAS
        )
        val ingredient2 = Ingredient(
            "IngredienteAbstracto UUUUhhhhhh Aaaaaahhahad8awdawu hiahs",
            200.5,
            FoodGroup.PROTEINAS
        )

        it("Agrega ingrediente al repositorio, lo actualiza y luego lo elimina") {
            ingredientRepo.create(ingredient1)
            val newlyCreatedItem = ingredientRepo.getById(1)
            compareIngredients(
                ingredient1,
                newlyCreatedItem!!
            ) shouldBe true

            ingredientRepo.nextId shouldBe 2

            ingredientRepo.create(ingredient2)
            compareIngredients(
                ingredient1,
                ingredientRepo.getById(1)!!
            ) shouldBe true

            newlyCreatedItem?.id shouldBe 1

            ingredientRepo.update(
                Ingredient(
                    "IngredienteAbstracto UUUUhhhhhh",
                    20050.0,
                    FoodGroup.PROTEINAS
                )
            )
            compareIngredients(
                ingredient1,
                ingredientRepo.getById(1)!!
            ) shouldBe true

            ingredientRepo.delete(ingredient1)
            ingredientRepo.getById(1) shouldBe null
        }
    }

    describe("Funcionalidad de deserialización") {
        val ingredientRepo = IngredientRepository()
        val serviceStub = IngredientServiceStub()
        val mailSender = AuditMailSender()
        val updater = IngredientsUpdater(serviceStub, ingredientRepo)

        it("Parsea correctamente los ingredientes y los convierte a IngredientData") {
            val ingredientsList = updater.getIngredientsData()
            val expectedList = mutableListOf(
                IngredientData(
                    22,
                    "Leche",
                    200.5,
                    "LACTEOS",
                    true
                ),
                IngredientData(
                    -1,
                    "Arroz",
                    100.5,
                    "CEREALES_Y_TUBERCULOS",
                    false
                ),
                IngredientData(
                    3,
                    "Azúcar",
                    200.0,
                    "AZUCARES_Y_DULCES",
                    false
                ),
                IngredientData(
                    -1,
                    "Pollo",
                    400.5,
                    "PROTEINAS",
                    true
                ),
                IngredientData(
                    57,
                    "Manzana",
                    100.0,
                    "FRUTAS_Y_VERDURAS",
                    false
                )
            )

            ingredientsList shouldBe expectedList
        }

        it("Actualiza correctamente el repositorio") {
            updater.updateIngredients()

            val expectedMap = mutableMapOf<Int, Ingredient>(
                22 to Ingredient(
                    "Leche",
                    200.5,
                    FoodGroup.LACTEOS
                ),
                1 to Ingredient(
                    "Arroz",
                    100.5,
                    FoodGroup.CEREALES_Y_TUBERCULOS
                ),
                3 to Ingredient(
                    "Azúcar",
                    200.0,
                    FoodGroup.AZUCARES_Y_DULCES
                ),
                2 to Ingredient(
                    "Pollo",
                    400.5,
                    FoodGroup.PROTEINAS
                ),
                57 to Ingredient(
                    "Manzana",
                    100.0,
                    FoodGroup.FRUTAS_Y_VERDURAS
                )
            )

            compareIngredients(expectedMap[22]!!, ingredientRepo.getById(1)!!) shouldBe true
            compareIngredients(expectedMap[1]!!, ingredientRepo.getById(2)!!) shouldBe true
            compareIngredients(expectedMap[3]!!, ingredientRepo.getById(3)!!) shouldBe true
            compareIngredients(expectedMap[2]!!, ingredientRepo.getById(4)!!) shouldBe true
            compareIngredients(expectedMap[57]!!, ingredientRepo.getById(5)!!) shouldBe true
        }
    }
})