import ar.edu.unsam.algo3.domain.AuditoriaMailSender
import ar.edu.unsam.algo3.domain.DataIngrediente
import ar.edu.unsam.algo3.domain.GrupoAlimentos
import ar.edu.unsam.algo3.domain.Ingrediente
import ar.edu.unsam.algo3.domain.IngredienteRepository
import ar.edu.unsam.algo3.domain.ServiceIngredientes
import ar.edu.unsam.algo3.domain.UpdaterIngredientes
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

fun equalIngredientes(ing1: Ingrediente, ing2: Ingrediente): Boolean {
    return ing1.nombre == ing2.nombre &&
            ing1.costo == ing2.costo &&
            ing1.grupoAlimentos == ing2.grupoAlimentos
}

fun printIngrediente(ing: Ingrediente) {
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
        val ingrediente1 = Ingrediente(
            nombre = "IngredienteAbstracto UUUUhhhhhh",
            costo = 200.5,
            grupoAlimentos = GrupoAlimentos.PROTEINAS
        )
        val ingrediente2 = Ingrediente(
            nombre = "IngredienteAbstracto UUUUhhhhhh Aaaaaahhahad8awdawu hiahs",
            costo = 200.5,
            grupoAlimentos = GrupoAlimentos.PROTEINAS
        )

        it ("Agrega un ingrediente al repositorio, lo actualiza y luego lo elimina") {
            // Nuevo ingrediente fuaaaaa
            rIngredientes.create(ingrediente1)
            //rIngredientes.id shouldBe 0
            var itemRecienCreado = rIngredientes.getById(1)
            equalIngredientes(
                ingrediente1,
                itemRecienCreado!!
            ) shouldBe true

            rIngredientes.idDisponible shouldBe 2
            //itemRecienCreado?.id shouldBe 1

            // Nuevo ingrediente fuaaaaa 2
            rIngredientes.create(ingrediente2)
            equalIngredientes(
                ingrediente1,
                rIngredientes.getById(1)!!
            ) shouldBe true

            itemRecienCreado = rIngredientes.getById(1)
            itemRecienCreado?.id shouldBe 1

            // Inflacion de 10000% LOL
            rIngredientes.update(
                Ingrediente(
                    nombre = "IngredienteAbstracto UUUUhhhhhh",
                    costo = 20050.0,
                    grupoAlimentos = GrupoAlimentos.PROTEINAS
                )
            )
            equalIngredientes(
                ingrediente1,
                rIngredientes.getById(1)!!
            ) shouldBe true

            // No hay mas stock lol
            rIngredientes.delete(ingrediente1)
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

            var expectedMap = mutableMapOf<Int, Ingrediente>(
                22 to Ingrediente(
                    nombre = "Leche",
                    costo = 200.5,
                    grupoAlimentos = GrupoAlimentos.LACTEOS
                ),
                1 to Ingrediente(
                    nombre = "Arroz",
                    costo = 100.5,
                    grupoAlimentos = GrupoAlimentos.CEREALES_Y_TUBERCULOS
                ),
                3 to Ingrediente(
                    nombre = "Azúcar",
                    costo = 200.0,
                    grupoAlimentos = GrupoAlimentos.AZUCARES_Y_DULCES
                ),
                2 to Ingrediente(
                    nombre = "Pollo",
                    costo = 400.5,
                    grupoAlimentos = GrupoAlimentos.PROTEINAS
                ),
                57 to Ingrediente(
                    nombre = "Manzana",
                    costo = 100.0,
                    grupoAlimentos = GrupoAlimentos.FRUTAS_Y_VERDURAS
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
