package ar.edu.unsam.algo2
import org.uqbar.geodds.Point
import java.time.LocalDate

class Cliente (
    nombre: String = "",
    apellido: String = "",
    username: String = "",
    password: String = "",
    fechaNacimiento: LocalDate = LocalDate.now(),
    fechaRegistro: LocalDate = LocalDate.now(),
    mail: String = "",
    var direccion: Direccion = Direccion("", "", Point(0,0)),
    val ingredientesFavoritos: MutableSet<Ingrediente> = mutableSetOf(),
    val ingredientesAEvitar: MutableSet<Ingrediente> = mutableSetOf(),
    var distanciaCercana: Double = 5.0,
    var tipoDeCliente: TipoDeCliente = ClienteNormal(),
    val valoracionesPendientes: MutableSet<ValoracionPendiente> = mutableSetOf()
) : Usuario(nombre, apellido, username, password, fechaNacimiento, fechaRegistro, mail) {

    var comandos = mutableListOf<ClienteCommand>()

    fun crearPedido(
        platos: List<Plato>,
        local: Local,
        medioDePago: MedioDePago
    ) {
        Pedido.crearPedido(
            this,
            platos,
            local,
            medioDePago
        )
    }

    fun agregarIngredienteFavorito(ingrediente: Ingrediente) {
        if (ingredientesAEvitar.contains(ingrediente)) {
            println("Error: El ingrediente ${ingrediente.nombre} está en la lista de ingredientes a evitar.")
        }
        else ingredientesFavoritos.add(ingrediente)
    }

    fun agregarIngredienteAEvitar(ingrediente: Ingrediente) {
        if (ingredientesFavoritos.contains(ingrediente)) {
            println("Error: El ingrediente ${ingrediente.nombre} está en la lista de ingredientes favoritos.")
        }
        else ingredientesAEvitar.add(ingrediente)
    }

    fun esPlatoAdecuadoParaCliente(plato: Plato): Boolean {
        return tipoDeCliente.esPlatoAdecuado(plato, this) && validarIngredientes(plato)
    }

    fun crearValoracion(valoracionPendiente: ValoracionPendiente,valor: Double){
        if (!valoracionPendiente.puedePuntuar()) {
            throw ValoracionException("No se puede puntuar este local. Ha expirado el plazo.")
        }
        valoracionPendiente.local.crearValoracion(this, valor)
        println("Se puntuó con exito el local.")
    }

    fun puntuar(local: Local, puntuacion: Double){
        val valoracionPendiente = valoracionesPendientes.find { it.local == local}

        if (valoracionPendiente == null) {
            throw ValoracionException("No se encontró una valoración pendiente ")
        }

        crearValoracion(valoracionPendiente, puntuacion)
        eliminarValoracionPendiente(valoracionPendiente)
    }

    fun eliminarValoracionPendiente(valoracion: ValoracionPendiente) {
        this.valoracionesPendientes.remove(valoracion)
    }

    fun confirmarPedido(pedido: Pedido){
        val local = pedido.local
        val existeValoracion = valoracionesPendientes.find { it.local == local }

        if (existeValoracion != null) {
            // Si existe, actualizamos la fecha del último pedido
            existeValoracion.fechaUltimoPedido = LocalDate.now()
        } else {
            // Si no existe, creamos una nueva valoración
            val nuevaValoracion = ValoracionPendiente(local)
            valoracionesPendientes.add(nuevaValoracion)
        }
    }

    fun esLocalCercano(local: Local) : Boolean{
        val distancia = this.direccion.coordenadas.distance(local.direccion.coordenadas)
        println("Distancia: $distancia")
        return distancia <= distanciaCercana
    }

    fun validarIngredientes(plato: Plato): Boolean {
        return plato.ingredientes.none{it in ingredientesAEvitar}
    }

    fun ejecutarComandos() {
        comandos.forEach {command -> command.execute(this)}
    }

    fun agregarComando(comando: ClienteCommand) {
        comandos.add(comando)
    }
}

// Interfaz principal para definir tipos de cliente
interface TipoDeCliente {
    fun esPlatoAdecuado(plato: Plato, cliente: Cliente): Boolean
}

// Marca opcional para identificar composites de TipoDeCliente
interface TipoDeClienteComposite : TipoDeCliente

// Composite AND: el plato debe ser adecuado según todos los subtipos
class ClienteAllTipos(
    val tipos: List<TipoDeCliente>
) : TipoDeClienteComposite {
    override fun esPlatoAdecuado(plato: Plato, cliente: Cliente): Boolean =
        tipos.all { it.esPlatoAdecuado(plato, cliente) }
}

// Implementaciones concretas de TipoDeCliente
class ClienteNormal : TipoDeCliente {
    override fun esPlatoAdecuado(plato: Plato, cliente: Cliente): Boolean = true
}

class ClienteVegano : TipoDeCliente {
    override fun esPlatoAdecuado(plato: Plato, cliente: Cliente): Boolean =
        !plato.ingredientes.any { it.grupoAlimentos.esDeOrigenAnimal }
}

class ClienteExquisito : TipoDeCliente {
    override fun esPlatoAdecuado(plato: Plato, cliente: Cliente): Boolean =
        plato.esDeAutor()
}

class ClienteConservador : TipoDeCliente {
    override fun esPlatoAdecuado(plato: Plato, cliente: Cliente): Boolean =
        plato.ingredientes.all { it in cliente.ingredientesFavoritos }
}

class ClienteFiel(private val localesPreferidos: Set<Local>) : TipoDeCliente {
    override fun esPlatoAdecuado(plato: Plato, cliente: Cliente): Boolean =
        plato.local in localesPreferidos
}

class ClienteMarketing(private val palabrasClave: Set<String>) : TipoDeCliente {
    override fun esPlatoAdecuado(plato: Plato, cliente: Cliente): Boolean =
        palabrasClave.any { clave ->
            plato.descripcion.contains(clave, ignoreCase = true)
        }
}

class ClienteImpaciente : TipoDeCliente {
    override fun esPlatoAdecuado(plato: Plato, cliente: Cliente): Boolean =
        cliente.esLocalCercano(plato.local)
}

class ClienteRaro : TipoDeCliente {
    private val cExquisito = ClienteExquisito()
    private val cConservador = ClienteConservador()

    override fun esPlatoAdecuado(plato: Plato, cliente: Cliente): Boolean {
        val tipoAUsar: TipoDeCliente =
            if (cliente.edad % 2 == 0) cExquisito
            else cConservador
        return tipoAUsar.esPlatoAdecuado(plato, cliente)
    }
}
