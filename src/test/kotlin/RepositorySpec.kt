import ar.edu.unsam.algo3.domain.AuditoriaMailSender
import ar.edu.unsam.algo3.domain.DataIngrediente
import ar.edu.unsam.algo3.domain.GrupoAlimentos
import ar.edu.unsam.algo3.domain.Ingredient
import ar.edu.unsam.algo3.domain.IngredienteRepository
import ar.edu.unsam.algo3.domain.ServiceIngredientes
import ar.edu.unsam.algo3.domain.UpdaterIngredientes
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

fun equalIngredientes(ing1: Ingredient, ing2: Ingredient): Boolean {
    return ing1.name == ing2.name &&
            ing1.price == ing2.price &&
            ing1.group == ing2.group
}

fun printIngrediente(ing: Ingredient) {
    println("")
}

class ServiceIngredientesStub : ServiceIngredientes {
    override fun getIngredientes(): String {
        return """
            [
              {
            	"id": 22,
            	"nombre": "Leche",
            	"costo": 200.5,
            	"grupo": "LACTEOS",
            	"origenAnimal": true
              },
              {
            	"nombre": "Arroz",
            	"costo": 100.5,
            	"grupo": "CEREALES_Y_TUBERCULOS",
            	"origenAnimal": false
              },
              {
            	"id": 3,
            	"nombre": "Azúcar",
            	"costo": 200.0,
            	"grupo": "AZUCARES_Y_DULCES",
            	"origenAnimal": false
              },
              {
            	"nombre": "Pollo",
            	"costo": 400.5,
            	"grupo": "PROTEINAS",
            	"origenAnimal": true
              },
              {
            	"id": 57,
            	"nombre": "Manzana",
            	"costo": 100.0,
            	"grupo": "FRUTAS_Y_VERDURAS",
            	"origenAnimal": false
              }
            ]
        """.trimIndent()
    }
}

class UpdaterIngredientesSpec : DescribeSpec({
    describe ("Funcionalidad del repositorio") {
        val rIngredientes = IngredienteRepository()
        val ingredient1 = Ingredient(
            name = "IngredienteAbstracto UUUUhhhhhh",
            price = 200.5,
            group = GrupoAlimentos.PROTEINAS
        )
        val ingredient2 = Ingredient(
            name = "IngredienteAbstracto UUUUhhhhhh Aaaaaahhahad8awdawu hiahs",
            price = 200.5,
            group = GrupoAlimentos.PROTEINAS
        )

        it ("Agrega un ingrediente al repositorio, lo actualiza y luego lo elimina") {
            // Nuevo ingrediente fuaaaaa
            rIngredientes.create(ingredient1)
            //rIngredientes.id shouldBe 0
            var itemRecienCreado = rIngredientes.getById(1)
            equalIngredientes(
                ingredient1,
                itemRecienCreado!!
            ) shouldBe true

            rIngredientes.idDisponible shouldBe 2
            //itemRecienCreado?.id shouldBe 1

            // Nuevo ingrediente fuaaaaa 2
            rIngredientes.create(ingredient2)
            equalIngredientes(
                ingredient1,
                rIngredientes.getById(1)!!
            ) shouldBe true

            itemRecienCreado = rIngredientes.getById(1)
            itemRecienCreado?.id shouldBe 1

            // Inflacion de 10000% LOL
            rIngredientes.update(
                Ingredient(
                    name = "IngredienteAbstracto UUUUhhhhhh",
                    price = 20050.0,
                    group = GrupoAlimentos.PROTEINAS
                )
            )
            equalIngredientes(
                ingredient1,
                rIngredientes.getById(1)!!
            ) shouldBe true

            // No hay mas stock lol
            rIngredientes.delete(ingredient1)
            rIngredientes.getById(1) shouldBe null
        }

    }

    describe ("La cosa desserializa bien") {
        val rIngredientes = IngredienteRepository()
        val serviceStub = ServiceIngredientesStub()
        val mailSender = AuditoriaMailSender()
        val updater = UpdaterIngredientes(serviceStub, rIngredientes)

        it ("Parsea bien los ingredientes, y los convierte bien en DataIngrediente.") {
            val ingredientesList = updater.getDataIngredientes()
            val expectedList = mutableListOf(
                DataIngrediente(
                    id = 22,
                    nombre = "Leche",
                    costo = 200.5,
                    grupo = "LACTEOS",
                    origenAnimal = true
                ),
                DataIngrediente(
                    id = -1,
                    nombre = "Arroz",
                    costo = 100.5,
                    grupo = "CEREALES_Y_TUBERCULOS",
                    origenAnimal = false
                ),
                DataIngrediente(
                    id = 3,
                    nombre = "Azúcar",
                    costo = 200.0,
                    grupo = "AZUCARES_Y_DULCES",
                    origenAnimal = false
                ),
                DataIngrediente(
                    id = -1,
                    nombre = "Pollo",
                    costo = 400.5,
                    grupo = "PROTEINAS",
                    origenAnimal = true
                ),
                DataIngrediente(
                    id = 57,
                    nombre = "Manzana",
                    costo = 100.0,
                    grupo = "FRUTAS_Y_VERDURAS",
                    origenAnimal = false
                )
            )

            ingredientesList shouldBe expectedList
        }

        it("Actualiza bien el repositorio"){
            updater.updateIngredientes()

            var expectedMap = mutableMapOf<Int, Ingredient>(
                22 to Ingredient(
                    name = "Leche",
                    price = 200.5,
                    group = GrupoAlimentos.LACTEOS
                ),
                1 to Ingredient(
                    name = "Arroz",
                    price = 100.5,
                    group = GrupoAlimentos.CEREALES_Y_TUBERCULOS
                ),
                3 to Ingredient(
                    name = "Azúcar",
                    price = 200.0,
                    group = GrupoAlimentos.AZUCARES_Y_DULCES
                ),
                2 to Ingredient(
                    name = "Pollo",
                    price = 400.5,
                    group = GrupoAlimentos.PROTEINAS
                ),
                57 to Ingredient(
                    name = "Manzana",
                    price = 100.0,
                    group = GrupoAlimentos.FRUTAS_Y_VERDURAS
                )
            )

            println(rIngredientes.getById(1)!!)
            equalIngredientes(expectedMap[22]!!, rIngredientes.getById(1)!!) shouldBe true
            println(rIngredientes.getById(2)!!)
            equalIngredientes(expectedMap[1]!!, rIngredientes.getById(2)!!) shouldBe true
            println(rIngredientes.getById(3))
            equalIngredientes(expectedMap[3]!!, rIngredientes.getById(3)!!) shouldBe true
            println(rIngredientes.getById(4))
            equalIngredientes(expectedMap[2]!!, rIngredientes.getById(4)!!) shouldBe true
            println(rIngredientes.getById(5)!!)
            equalIngredientes(expectedMap[57]!!, rIngredientes.getById(5)!!) shouldBe true

        }
    }
})
