package ar.edu.unsam.algo2

enum class GrupoAlimentos(open val esDeOrigenAnimal: Boolean) {
    CEREALES_Y_TUBERCULOS(false),
    AZUCARES_Y_DULCES(false),
    LACTEOS(true),
    FRUTAS_Y_VERDURAS(false),
    GRASAS_Y_ACEITES(false),
    PROTEINAS(true),
}