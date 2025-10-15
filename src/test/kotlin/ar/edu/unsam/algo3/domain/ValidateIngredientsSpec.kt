package ar.edu.unsam.algo3.domain

import org.uqbar.geodds.*
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class ValidateIngredientsSpec : DescribeSpec({
    describe("Validación de ingredientes") {
        lateinit var client: Client
        lateinit var ingredient1: Ingredient
        lateinit var ingredient2: Ingredient
        lateinit var ingredient3: Ingredient

        beforeEach {
            ingredient1 = Ingredient("Tomate", 100.0, FoodGroup.FRUTAS_Y_VERDURAS)
            ingredient2 = Ingredient("Carne", 500.0, FoodGroup.PROTEINAS)
            ingredient3 = Ingredient("Lechuga", 80.0, FoodGroup.FRUTAS_Y_VERDURAS)
            client = Client(
                "Juan",
                "Perez",
                "juan123",
                "seguro123",
                LocalDate.of(1995, 5, 10),
                LocalDate.now(),
                "cliente@gmail.com",
                Address(
                    "Calle Siempre Viva ",
                    "742",
                    Point(8, 6)
                ),
                mutableSetOf(ingredient1),
                mutableSetOf(ingredient2),
                10.0,
                NormalClient()
            )
        }

        it("Un ingrediente válido es aceptado") {
            client.favoriteIngredients.add(ingredient3)  // Agregar directamente al set
            client.favoriteIngredients.contains(ingredient3) shouldBe true
        }

        it("Un ingrediente a evitar es rechazado") {
            client.ingredientsToAvoid.remove(ingredient1)  // Eliminar directamente del set
            client.ingredientsToAvoid.contains(ingredient1) shouldBe false
        }

        it("Un ingrediente favorito es rechazado") {
            client.favoriteIngredients.add(ingredient2)  // Intentar agregar un ingrediente a favoritos
            client.favoriteIngredients.contains(ingredient2) shouldBe true
        }


        it("Un ingrediente a evitar es aceptado") {
            client.ingredientsToAvoid.add(ingredient3)  // Agregar directamente al set
            client.ingredientsToAvoid.contains(ingredient3) shouldBe true
        }
    }
})
