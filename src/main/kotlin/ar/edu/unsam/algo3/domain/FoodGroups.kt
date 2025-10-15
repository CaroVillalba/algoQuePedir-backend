package ar.edu.unsam.algo3.domain

enum class FoodGroups(open val isAnimalOrigin: Boolean, open val names: String) {
    CEREALES_Y_TUBERCULOS(false, "Cereales y Tuberculos"),
    AZUCARES_Y_DULCES(false, "Azucares y Dulces"),
    LACTEOS(true, "Lacteos"),
    FRUTAS_Y_VERDURAS(false, "Frutas y Verduras"),
    GRASAS_Y_ACEITES(false, "Grasas y Aceites"),
    PROTEINAS(true, "Proteinas"),
}