package com.example.flatmatehub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

/**
 * Activitat principal de Flatmate Hub després d'iniciar sessió i accedir a un pis.
 * Gestiona la capçalera de benvinguda, el menú lateral ([DrawerLayout]), els filtres
 * per categoria (Tots, Tasques, Despeses) i el llistat dinàmic d'elements ([RecyclerView]).
 */
class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var adapter: ItemPisoAdapter
    private var filtroActual: TipoItem? = null // null = Todos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configurarToolbarYDrawer()
        configurarCabeceraUsuario()
        configurarFiltros()
        configurarRecyclerView()

        // Botón flotante + -> abre pantalla para crear tarea o gasto
        findViewById<FloatingActionButton>(R.id.fabCrearItem).setOnClickListener {
            startActivity(Intent(this, CreateItemActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        configurarCabeceraUsuario()
        aplicarFiltroYRefrescar()
    }
    /**
     * Mostra a la capçalera la salutació personalitzada de l'usuari actiu juntament amb el
     * nom i el codi únic del pis actual emmagatzemats a [FlatmateRepository].
     */
    private fun configurarCabeceraUsuario() {
        val nombreUser = FlatmateRepository.usuarioActual?.nombre ?: "Compañero"
        val nombrePiso = FlatmateRepository.pisoActual?.nombre ?: "Sin piso"
        val codigoPiso = FlatmateRepository.pisoActual?.codigo ?: ""

        findViewById<TextView>(R.id.tvSaludoUsuario)?.text = "¡Hola, $nombreUser!"
        findViewById<TextView>(R.id.tvNombrePisoActual)?.text = "$nombrePiso ($codigoPiso)"
    }

    /**
     * Configura la [Toolbar] superior i el [NavigationView] lateral, enllaçant les tres
     * opcions del menú hamburguesa: Preferències, Canviar Pis i Tancar Sessió.
     */
    private fun configurarToolbarYDrawer() {
        val toolbar = findViewById<Toolbar>(R.id.toolbarMain)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawerLayout)
        val navView = findViewById<NavigationView>(R.id.navigationView)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_preferencias -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
                R.id.nav_cambiar_piso -> {
                    startActivity(Intent(this, WelcomeFloorActivity::class.java))
                }
                R.id.nav_cerrar_sesion -> {
                    FlatmateRepository.cerrarSesion()
                    Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
    /**
     * Configura els botons de filtre (Tots, Tasques i Despeses) per actualitzar el criteri
     * de visualització del llistat principal.
     */
    private fun configurarFiltros() {
        val btnTodos = findViewById<Button>(R.id.btnFiltroTodos)
        val btnTareas = findViewById<Button>(R.id.btnFiltroTareas)
        val btnGastos = findViewById<Button>(R.id.btnFiltroGastos)

        btnTodos.setOnClickListener {
            filtroActual = null
            aplicarFiltroYRefrescar()
        }
        btnTareas.setOnClickListener {
            filtroActual = TipoItem.TAREA
            aplicarFiltroYRefrescar()
        }
        btnGastos.setOnClickListener {
            filtroActual = TipoItem.GASTO
            aplicarFiltroYRefrescar()
        }
    }
    /**
     * Inicialitza el [RecyclerView] amb el seu [ItemPisoAdapter] i defineix les accions d'edició,
     * canvi d'estat i eliminació amb confirmació, registrant cada operació mitjançant
     * [Log] i notificacions [Toast].
     */
    private fun configurarRecyclerView() {
        val rv = findViewById<RecyclerView>(R.id.rvTareasGastos)
        rv.layoutManager = LinearLayoutManager(this)

        adapter = ItemPisoAdapter(
            listaVisible = mutableListOf(),
            onEditar = { item ->
                // Abre CreateItemActivity en modo edición pasando el ID del elemento
                val intent = Intent(this, CreateItemActivity::class.java).apply {
                    putExtra("ITEM_ID", item.id)
                }
                startActivity(intent)
            },
            onEliminar = { item ->
                AlertDialog.Builder(this)
                    .setTitle("Eliminar ${if (item.tipo == TipoItem.TAREA) "tarea" else "gasto"}")
                    .setMessage("¿Eliminar '${item.titulo}' de la lista del piso?")
                    .setPositiveButton("Eliminar") { _, _ ->
                        FlatmateRepository.items.remove(item)
                        aplicarFiltroYRefrescar()
                        Log.d("FlatmateHub", "ELIMINADO: ${item.titulo}")
                        Toast.makeText(this, "Eliminado: ${item.titulo}", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            },
            onToggleCompletado = { item ->
                item.completado = !item.completado
                aplicarFiltroYRefrescar()
                val estado = if (item.completado) "completado" else "pendiente"
                Log.d("FlatmateHub", "MODIFICADO estado ($estado): ${item.titulo}")
                Toast.makeText(this, "${item.titulo} marcado como $estado", Toast.LENGTH_SHORT).show()
            }
        )
        rv.adapter = adapter
        aplicarFiltroYRefrescar()
    }
    /**
     * Filtra els elements del pis actiu segons la categoria seleccionada i actualitza
     * el contingut de l'[ItemPisoAdapter].
     */
    private fun aplicarFiltroYRefrescar() {
        val delPiso = FlatmateRepository.obtenerItemsDelPisoActual()
        val filtrados = if (filtroActual == null) {
            delPiso
        } else {
            delPiso.filter { it.tipo == filtroActual }.toMutableList()
        }
        adapter.refrescarDatos(filtrados)
    }
}