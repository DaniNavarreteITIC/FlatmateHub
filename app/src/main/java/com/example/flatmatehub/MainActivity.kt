package com.example.flatmatehub

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Activitat principal de Flatmate Hub després d'iniciar sessió i accedir a un pis.
 * Integra la vista principal (`fragment_main_home.xml`) amb el menú lateral
 * (`fragment_navigation_drawer.xml`), gestiona els filtres per categoria (Tots, Tasques, Despeses)
 * i administra el llistat dinàmic d'elements ([RecyclerView]).
 */
class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var homeView: View
    private lateinit var drawerView: View
    private lateinit var adapter: ItemPisoAdapter
    private var filtroActual: TipoItem? = null // null = Tots

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        montarInterfazConDrawer()
        configurarCabeceraUsuario()
        configurarMenuLateral()
        configurarFiltros()
        configurarRecyclerView()

        // Botó flotant + -> obre la pantalla per crear una tasca o despesa
        homeView.findViewById<FloatingActionButton>(R.id.fab_add_item).setOnClickListener {
            startActivity(Intent(this, CreateItemActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        configurarCabeceraUsuario()
        aplicarFiltroYRefrescar()
    }

    /**
     * Infla `fragment_main_home.xml` i `fragment_navigation_drawer.xml` dins d'un [DrawerLayout]
     * acoblat al contenidor arrel `R.id.main` de `activity_main.xml`.
     */
    private fun montarInterfazConDrawer() {
        val rootContainer = findViewById<FrameLayout>(R.id.main)
        drawerLayout = DrawerLayout(this).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        homeView = layoutInflater.inflate(R.layout.fragment_main_home, drawerLayout, false)
        drawerView = layoutInflater.inflate(R.layout.fragment_navigation_drawer, drawerLayout, false)

        val drawerParams = DrawerLayout.LayoutParams(
            drawerView.layoutParams?.width ?: ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ).apply { gravity = GravityCompat.START }

        drawerLayout.addView(homeView)
        drawerLayout.addView(drawerView, drawerParams)
        rootContainer.addView(drawerLayout)
    }

    /**
     * Mostra a la capçalera el nom i el codi únic del pis actiu emmagatzemat a [FlatmateRepository].
     */
    private fun configurarCabeceraUsuario() {
        val nombrePiso = FlatmateRepository.pisoActual?.nombre ?: "Flatmate Hub"
        val codigoPiso = FlatmateRepository.pisoActual?.codigo ?: ""
        val tituloCabecera = if (codigoPiso.isNotEmpty()) "$nombrePiso ($codigoPiso)" else nombrePiso

        homeView.findViewById<TextView>(R.id.tv_main_title)?.text = tituloCabecera
    }

    /**
     * Configura l'obertura i tancament del calaix lateral i enllaça els tres botons de navegació:
     * Preferències, Canviar Pis i Tancar Sessió.
     */
    private fun configurarMenuLateral() {
        homeView.findViewById<ImageButton>(R.id.btn_open_menu).setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        drawerView.findViewById<ImageButton>(R.id.btn_close_menu).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
        }

        drawerView.findViewById<MaterialButton>(R.id.btn_menu_preferences).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        drawerView.findViewById<MaterialButton>(R.id.btn_menu_change_flat).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            val intent = Intent(this, BienvenidaFlatmate::class.java).apply {
                putExtra("SALTAR_TUTORIAL", true)
            }
            startActivity(intent)
        }

        drawerView.findViewById<MaterialButton>(R.id.btn_menu_logout).setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            FlatmateRepository.cerrarSesion()
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, AuthActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }

    /**
     * Configura els [Chip] de filtre (Tots, Tasques i Despeses) per actualitzar el criteri
     * de visualització del llistat principal.
     */
    private fun configurarFiltros() {
        val chipTodos = homeView.findViewById<Chip>(R.id.chip_filter_all)
        val chipTareas = homeView.findViewById<Chip>(R.id.chip_filter_tasks)
        val chipGastos = homeView.findViewById<Chip>(R.id.chip_filter_expenses)

        chipTodos.setOnClickListener {
            filtroActual = null
            aplicarFiltroYRefrescar()
        }
        chipTareas.setOnClickListener {
            filtroActual = TipoItem.TAREA
            aplicarFiltroYRefrescar()
        }
        chipGastos.setOnClickListener {
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
        val rv = homeView.findViewById<RecyclerView>(R.id.recycler_view_home)
        rv.layoutManager = LinearLayoutManager(this)

        adapter = ItemPisoAdapter(
            listaVisible = mutableListOf(),
            // Obre CreateItemActivity en mode edició passant l'ID de l'element
            onEditar = { item ->
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