package com.example.flatmatehub

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import java.util.Calendar
import java.util.Date

/**
 * Activitat de formulari dual encarregada tant de crear noves tasques o despeses compartides
 * (`fragment_add_item.xml`) com d'editar o eliminar elements ja existents (`fragment_edit_item.xml`).
 */
class CreateItemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val itemEditandoId = intent.getIntExtra("ITEM_ID", -1)
        val itemExistente = FlatmateRepository.items.find { it.id == itemEditandoId }

        if (itemExistente != null) {
            configurarModoEdicion(itemExistente)
        } else {
            configurarModoCreacion()
        }
    }

    /**
     * Carrega `fragment_add_item.xml` i gestiona la creació d'una nova tasca o despesa al pis actiu.
     */
    private fun configurarModoCreacion() {
        setContentView(R.layout.fragment_add_item)

        val btnAtras = findViewById<ImageButton>(R.id.btn_back_add)
        val chipTarea = findViewById<Chip>(R.id.chip_type_task)
        val chipGasto = findViewById<Chip>(R.id.chip_type_expense)
        val etTitulo = findViewById<TextInputEditText>(R.id.et_item_title)
        val etDescripcion = findViewById<TextInputEditText>(R.id.et_item_desc)
        val etAsignar = findViewById<TextInputEditText>(R.id.et_item_assignee)
        val etFecha = findViewById<TextInputEditText>(R.id.et_item_date)
        val btnGuardar = findViewById<MaterialButton>(R.id.btn_save_new_item)

        btnAtras.setOnClickListener { finish() }

        // Omple prèviament la persona assignada amb l'usuari actual si existeix
        etAsignar.setText(FlatmateRepository.usuarioActual?.nombre ?: "Alex")

        // Selector de data amb calendari
        etFecha.isFocusable = false
        etFecha.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, y, m, d ->
                    cal.set(y, m, d)
                    etFecha.setText(cal.time.aTextoCorto())
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnGuardar.setOnClickListener {
            val titulo = etTitulo.text?.toString()?.trim() ?: ""
            if (titulo.isEmpty()) {
                etTitulo.error = "Introduce un nombre"
                return@setOnClickListener
            }

            val tipo = if (chipGasto.isChecked) TipoItem.GASTO else TipoItem.TAREA
            val desc = etDescripcion.text?.toString()?.trim() ?: ""
            val asignado = etAsignar.text?.toString()?.trim()?.ifEmpty { "Alex" } ?: "Alex"
            val fecha = etFecha.text?.toString()?.trim()?.ifEmpty { Date().aTextoCorto() } ?: Date().aTextoCorto()
            val codigoPiso = FlatmateRepository.pisoActual?.codigo ?: "P-4821"

            val nuevo = ItemPiso(
                id = FlatmateRepository.siguienteIdItem(),
                codigoPiso = codigoPiso,
                titulo = titulo,
                descripcion = desc,
                tipo = tipo,
                asignadoA = asignado,
                fechaLimite = fecha,
                importe = if (tipo == TipoItem.GASTO) 0.0 else null
            )

            FlatmateRepository.items.add(0, nuevo)
            Log.d("FlatmateHub", "AÑADIDO: $titulo ($tipo)")
            Toast.makeText(this, "Añadido: $titulo", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /**
     * Carrega `fragment_edit_item.xml` amb les dades de l'[ItemPiso] seleccionat i permet
     * actualitzar-lo o eliminar-lo directament.
     *
     * @param item Element existent que s'està editant.
     */
    private fun configurarModoEdicion(item: ItemPiso) {
        setContentView(R.layout.fragment_edit_item)

        val btnAtras = findViewById<ImageButton>(R.id.btn_back_edit_item)
        val etTitulo = findViewById<TextInputEditText>(R.id.et_edit_item_title)
        val etDescripcion = findViewById<TextInputEditText>(R.id.et_edit_item_desc)
        val etAsignar = findViewById<TextInputEditText>(R.id.et_edit_item_assignee)
        val btnGuardar = findViewById<MaterialButton>(R.id.btn_update_item)
        val btnEliminar = findViewById<MaterialButton>(R.id.btn_delete_item)

        etTitulo.setText(item.titulo)
        etDescripcion.setText(item.descripcion)
        etAsignar.setText(item.asignadoA)

        btnAtras.setOnClickListener { finish() }

        btnGuardar.setOnClickListener {
            val titulo = etTitulo.text?.toString()?.trim() ?: ""
            if (titulo.isEmpty()) {
                etTitulo.error = "Introduce un nombre"
                return@setOnClickListener
            }

            item.titulo = titulo
            item.descripcion = etDescripcion.text?.toString()?.trim() ?: ""
            item.asignadoA = etAsignar.text?.toString()?.trim()?.ifEmpty { item.asignadoA } ?: item.asignadoA

            Log.d("FlatmateHub", "MODIFICADO: ${item.titulo}")
            Toast.makeText(this, "Modificado: ${item.titulo}", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnEliminar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Eliminar elemento")
                .setMessage("¿Eliminar '${item.titulo}' de la lista del piso?")
                .setPositiveButton("Eliminar") { _, _ ->
                    FlatmateRepository.items.remove(item)
                    Log.d("FlatmateHub", "ELIMINADO: ${item.titulo}")
                    Toast.makeText(this, "Eliminado: ${item.titulo}", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }
}