package ar.edu.unsam.algo3.domain

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.uqbar.geodds.Point
import java.time.LocalDate

class UserSpec {
    private lateinit var user: User
    private lateinit var ingredient1: Ingredient
    private lateinit var ingredient2: Ingredient
    private lateinit var ingredient3: Ingredient
    val dateTodayMinus30 = LocalDate.now().minusYears(30)
    @BeforeEach
    fun setUp() {
        ingredient1 = Ingredient("Tomate", 100.0, group = FoodGroup.FRUTAS_Y_VERDURAS,1)
        ingredient2 = Ingredient("Carne", 500.0, group = FoodGroup.PROTEINAS,2)
        ingredient3 = Ingredient("Lechuga", 80.0, group = FoodGroup.FRUTAS_Y_VERDURAS,3)

        user = Client(
            "Juan",
            "Perez",
            "juan123",
            "seguro123",
            dateTodayMinus30,
            "usuario@gmail.com",
            LocalDate.now().minusYears(30),
            Address(
                "Calle random en kirguistan",
                "123",
                Point(40.92963, 73.00132)
            ),
            "a",
            mutableSetOf(),
            mutableSetOf(),
            5.0,
            NormalClient()
        )
    }

    @Test
    fun `verificar que la edad se calcula correctamente`() {
        assertEquals(30, user.age, "edad calculada correctamente")
    }
}