package com.example.flatmatehub

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Sirve para diferenciar si un elemento de la lista es una tarea del hogar o un gasto compartido.
 */
enum class TipoItem { TAREA, GASTO }

/**
 * Función de extensión para pasar un objeto [Date] a texto con formato día/mes/año
 * y así no tener que repetir el `SimpleDateFormat` cada vez que muestre una fecha.
 *
 * @return La fecha formateada como `"dd/MM/yyyy"`.
 */
fun Date.aTextoCorto(): String {
    val formato = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
    return formato.format(this)
}

/**
 * Guarda los datos básicos de un usuario registrado en la app y el código del piso
 * en el que está metido actualmente (si ya tiene alguno).
 *
 * @property id Identificador numérico del usuario.
 * @property nombre Nombre que se muestra en la app y al asignar tareas.
 * @property email Correo usado para iniciar sesión.
 * @property password Contraseña del usuario (en memoria para las pruebas).
 * @property codigoPisoActual Código del piso activo o `null` si todavía no se ha unido a ninguno.
 */
data class Usuario(
    val id: Int,
    var nombre: String,
    var email: String,
    var password: String,
    var codigoPisoActual: String? = null
)

/**
 * Representa un piso compartido con su código único para invitar compañeros
 * y la lista de personas que viven en él.
 *
 * @property codigo Código único del piso, por ejemplo `"P-4821"`.
 * @property nombre Nombre del piso, por ejemplo `"Piso Mallorca 12"`.
 * @property idCreador ID del usuario que creó el piso.
 * @property miembros Lista de nombres de los compañeros que pertenecen al piso.
 */
data class Piso(
    val codigo: String,
    var nombre: String,
    val idCreador: Int,
    val miembros: MutableList<String> = mutableListOf()
)

/**
 * Representa una tarea o un gasto dentro de un piso concretos.
 * El importe solo se usa cuando el tipo es [TipoItem.GASTO].
 *
 * @property id Identificador único del elemento.
 * @property codigoPiso Código del piso al que pertenece este elemento.
 * @property titulo Nombre corto de la tarea o gasto.
 * @property descripcion Explicación extra de lo que hay que hacer o comprar.
 * @property tipo Indica si es [TipoItem.TAREA] o [TipoItem.GASTO].
 * @property asignadoA Nombre del compañero al que le toca la tarea o el gasto.
 * @property fechaLimite Texto con la fecha o plazo (por ejemplo `"Hoy"`, `"Mañana"` o una fecha).
 * @property importe Cantidad en euros si es un gasto, o `null` si es una tarea.
 * @property completado Marca si la tarea está hecha o el gasto resuelto.
 */
data class ItemPiso(
    val id: Int,
    val codigoPiso: String,
    var titulo: String,
    var descripcion: String,
    var tipo: TipoItem,
    var asignadoA: String,
    var fechaLimite: String,
    var importe: Double? = null,
    var completado: Boolean = false
)

/**
 * Objeto único (`object`) que hace de almacén central en memoria mientras la app está abierta.
 * Aquí guardo los datos de prueba, el usuario y piso activos, y las funciones para registrar,
 * hacer login, crear pisos con código único y filtrar las tareas o gastos del piso.
 */
object FlatmateRepository {
    val usuarios = mutableListOf(
        Usuario(1, "Alex", "alex@piso.com", "1234", "P-4821"),
        Usuario(2, "María", "maria@piso.com", "1234", "P-4821"),
        Usuario(3, "Sofía", "sofia@piso.com", "1234", "P-4821")
    )

    val pisos = mutableListOf(
        Piso("P-4821", "Piso Mallorca 12", 1, mutableListOf("Alex", "María", "Sofía"))
    )

    val items = mutableListOf(
        ItemPiso(1, "P-4821", "Limpiar Platos", "Repasar la encimera", TipoItem.TAREA, "María", "Hoy"),
        ItemPiso(2, "P-4821", "Compra Super", "Limpieza y cocina", TipoItem.GASTO, "María", "Ayer", 34.50),
        ItemPiso(3, "P-4821", "Tirar basura", "Orgánica y envases", TipoItem.TAREA, "Sofía", "Mañana")
    )

    var usuarioActual: Usuario? = null
    var pisoActual: Piso? = null

    private var contadorUsuarios = 4
    private var contadorItems = 4

    /**
     * Genera un código tipo `P-XXXX` y comprueba con un bucle `do-while`
     * que no exista ya en la lista de pisos para asegurar que sea único.
     *
     * @return Un código de piso que no está repetido.
     */
    fun generarCodigoPisoUnico(): String {
        var nuevoCodigo: String
        do {
            nuevoCodigo = "P-${(1000..9999).random()}"
        } while (pisos.any { it.codigo.equals(nuevoCodigo, ignoreCase = true) })
        return nuevoCodigo
    }

    /**
     * Crea un usuario nuevo si el correo no estaba ya registrado, lo añade a la lista
     * y lo deja marcado como usuario actual (todavía sin piso asignado).
     *
     * @param nombre Nombre del nuevo usuario.
     * @param email Correo electrónico para el registro.
     * @param pass Contraseña elegida.
     * @return El [Usuario] creado, o `null` si el email ya existe.
     */
    fun registrarUsuario(nombre: String, email: String, pass: String): Usuario? {
        if (usuarios.any { it.email.equals(email, ignoreCase = true) }) return null
        val nuevo = Usuario(contadorUsuarios++, nombre, email, pass, null)
        usuarios.add(nuevo)
        usuarioActual = nuevo
        pisoActual = null
        return nuevo
    }

    /**
     * Busca si existe un usuario con ese correo y contraseña. Si coincide, guarda
     * la sesión activa y carga su piso actual si ya tenía uno.
     *
     * @param email Correo introducido en el login.
     * @param pass Contraseña introducida en el login.
     * @return El [Usuario] si los datos son correctos, o `null` si no coinciden.
     */
    fun iniciarSesion(email: String, pass: String): Usuario? {
        val encontrado = usuarios.find {
            it.email.equals(email, ignoreCase = true) && it.password == pass
        }
        if (encontrado != null) {
            usuarioActual = encontrado
            pisoActual = pisos.find { it.codigo == encontrado.codigoPisoActual }
        }
        return encontrado
    }

    /**
     * Crea un piso nuevo con un código único para el usuario que ha iniciado sesión,
     * lo mete como primer miembro y lo asigna como su piso actual.
     *
     * @param nombrePiso Nombre que el usuario quiere darle al piso.
     * @return El [Piso] creado, o `null` si no hay ningún usuario con sesión iniciada.
     */
    fun crearPiso(nombrePiso: String): Piso? {
        val user = usuarioActual ?: return null
        val codigoUnico = generarCodigoPisoUnico()
        val nuevoPiso = Piso(codigoUnico, nombrePiso, user.id, mutableListOf(user.nombre))
        pisos.add(nuevoPiso)
        user.codigoPisoActual = codigoUnico
        pisoActual = nuevoPiso
        return nuevoPiso
    }

    /**
     * Une al usuario actual a un piso existente buscando por su código. Si el usuario
     * no estaba en la lista de miembros, lo añade y actualiza su piso activo.
     *
     * @param codigoIntroducido Código del piso escrito por el usuario.
     * @return El [Piso] al que se ha unido, o `null` si el código no existe o no hay sesión.
     */
    fun unirseAPiso(codigoIntroducido: String): Piso? {
        val user = usuarioActual ?: return null
        val pisoEncontrado = pisos.find {
            it.codigo.equals(codigoIntroducido.trim(), ignoreCase = true)
        } ?: return null

        if (!pisoEncontrado.miembros.contains(user.nombre)) {
            pisoEncontrado.miembros.add(user.nombre)
        }
        user.codigoPisoActual = pisoEncontrado.codigo
        pisoActual = pisoEncontrado
        return pisoEncontrado
    }

    /**
     * Devuelve solo las tareas y gastos que pertenecen al piso en el que está
     * actualmente el usuario para no mezclar datos de otros pisos.
     *
     * @return Lista mutable con los [ItemPiso] del piso actual, o vacía si no hay piso activo.
     */
    fun obtenerItemsDelPisoActual(): MutableList<ItemPiso> {
        val cod = pisoActual?.codigo ?: return mutableListOf()
        return items.filter { it.codigoPiso == cod }.toMutableList()
    }

    /**
     * Devuelve un ID nuevo para la próxima tarea o gasto y suma uno al contador.
     *
     * @return Siguiente número de ID disponible.
     */
    fun siguienteIdItem(): Int = contadorItems++

    /**
     * Limpia el usuario y el piso activos al cerrar sesión.
     */
    fun cerrarSesion() {
        usuarioActual = null
        pisoActual = null
    }
}