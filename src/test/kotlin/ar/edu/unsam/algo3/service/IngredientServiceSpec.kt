package ar.edu.unsam.algo3.service

import ar.edu.unsam.algo3.domain.ClientRepository
import ar.edu.unsam.algo3.domain.IngredientRepository
import io.kotest.core.spec.style.DescribeSpec
import org.mockito.Mockito.mock

class IngredientServiceSpec : DescribeSpec({

    describe("Ingredient Service") {
        lateinit var ingredientRepository: IngredientRepository
        lateinit var ingredientService: IngredientService

        beforeEach {
            ingredientRepository = mock()
            ingredientService = IngredientService(ingredientRepository)
        }

        
    }

})