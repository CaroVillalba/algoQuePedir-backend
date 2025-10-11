package ar.edu.unsam.algo3.domain
import java.time.Duration
import java.time.LocalDate

class Plato(
    var nombre: String,
    var descripcion: String,
    var ingredientes: List<Ingrediente>,
    var local: Local,
    var porcentajeAcordadoConLocal: Double,
    var lanzamiento: LocalDate,
    var regaliasAutor: Double,
    var valorBase: Double,
    var descuento: Double  //valor fijo, no porcentual
): Entity() {
    //calcula el costo
    fun costoDeIngredientes(): Double = ingredientes.sumOf { it.costo }

    //cantidad de días que han pasado desde el lanzamiento
    fun getDiasDesdeLanzamiento(): Int = Duration.between(
        lanzamiento.atStartOfDay(),
        LocalDate.now().atStartOfDay()
    ).toDays().toInt()

    //calcula el valor
    fun valorDeVenta(): Double {
        var resultado = valorDeVentaSinDescuento()
        if (descuentoDependiendoDeCondiciones() > 0.0){
            resultado -= descuentoDependiendoDeCondiciones()
        }
        return resultado
    }
    //

    fun valorDeVentaSinDescuento():Double {
        return costoDeIngredientes() + valorBase + costoPlataforma() + costoRegalias()
    }
    fun costoPlataforma(): Double = valorBase * (porcentajeAcordadoConLocal/100.0)
    fun costoRegalias(): Double = if (esDeAutor()) valorBase * (regaliasAutor/100.0) else 0.0

    fun descuentoDependiendoDeCondiciones(): Double{
        val dias = getDiasDesdeLanzamiento()

        if (esNuevo()) {
            if (dias <= 20.0){
                return valorDeVentaSinDescuento()*((30.0-dias)/100.0)
            } else {
                return valorDeVentaSinDescuento()*0.1
            }
        } else {
            return valorDeVentaSinDescuento()*(descuento/100.0)
        }
    }
    fun esNuevo(): Boolean = getDiasDesdeLanzamiento() <= 30.0

    fun enPromo(): Boolean = (descuento != 0.0)

    fun esDeAutor(): Boolean = regaliasAutor != 0.0

    fun esVegano(): Boolean {
        return !ingredientes.any { it.origenAnimal()}
    }
}