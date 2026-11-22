package com.example.flatmatehub
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import java.util.Date
/**
 * Activitat de formulari dual encarregada tant de crear noves tasques o despeses compartides
 * com d'editar elements ja existents al pis actiu.
 *
 * Si l'[Intent] d'entrada inclou l'extra `"ITEM_ID"`, precarrega les dades de l'element
 * per modificar-les; en cas contrari, actua en mode creació adaptant dinàmicament
 * el camp d'import segons el tipus seleccionat i permetent triar dates amb [DatePickerDialog].
 */
class CreateItemActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_item)

        val btnAtras = findViewById<View>(R.id.btnAtrasCrearItem)
        val etTitulo = findViewById<EditText>(R.id.etTituloItem)
        val etDescripcion = findViewById<EditText>(R.id.etDescripcionItem)
        val rgTipo = findViewById<RadioGroup>(R.id.rgTipoItem)
        val rbTarea = findViewById<RadioButton>(R.id.rbTarea)
        val rbGasto = findViewById<RadioButton>(R.id.rbGasto)
        val etImporte = findViewById<EditText>(R.id.etImporteItem)
        val spinnerAsignar = findViewById<Spinner>(R.id.spinnerAsignarA)
        val etFecha = findViewById<EditText>(R.id.etFechaLimite)
        val btnGuardar = findViewById<Button>(R.id.btnCrearItemConfirmar)

        btnAtras?.setOnClickListener { finish() }

        // Cargar compañeros del piso actual en el Spinner
        val companeros = FlatmateRepository.pisoActual?.miembros ?: listOf("Alex")
        spinnerAsignar.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, companeros)

        // Mostrar campo de euros solo si se selecciona Gasto
        rgTipo.setOnCheckedChangeListener { _, checkedId ->
            etImporte.visibility = if (checkedId == R.id.rbGasto) View.VISIBLE else View.GONE
        }

        // Selector de fecha con calendario
        etFecha.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(this, { _, y, m, d ->
                cal.set(y, m, d)
                etFecha.setText(cal.time.aTextoCorto())
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        // Comprobar si venimos a EDITAR un ítem existente
        val itemEditandoId = intent.getIntExtra("ITEM_ID", -1)
        val itemExistente = FlatmateRepository.items.find { it.id == itemEditandoId }

        if (itemExistente != null) {
            btnGuardar.text = "GUARDAR CAMBIOS"
            etTitulo.setText(itemExistente.titulo)
            etDescripcion.setText(itemExistente.descripcion)
            etFecha.setText(itemExistente.fechaLimite)
            if (itemExistente.tipo == TipoItem.GASTO) {
                rbGasto.isChecked = true
                etImporte.visibility = View.VISIBLE
                etImporte.setText(itemExistente.importe?.toString() ?: "")
            } else {
                rbTarea.isChecked = true
            }
            val idxCompanero = companeros.indexOf(itemExistente.asignadoA)
            if (idxCompanero >= 0) spinnerAsignar.setSelection(idxCompanero)
        }

        btnGuardar.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()
            if (titulo.isEmpty()) {
                etTitulo.error = "Introduce un nombre"
                return@setOnClickListener
            }

            val tipo = if (rbGasto.isChecked) TipoItem.GASTO else TipoItem.TAREA
            val importe = if (tipo == TipoItem.GASTO) etImporte.text.toString().toDoubleOrNull() ?: 0.0 else null
            val asignado = spinnerAsignar.selectedItem?.toString() ?: "Alex"
            val fecha = etFecha.text.toString().ifEmpty { Date().aTextoCorto() }
            val desc = etDescripcion.text.toString().trim()

            if (itemExistente != null) {
                // EVENTO MODIFICAR
                itemExistente.titulo = titulo
                itemExistente.descripcion = desc
                itemExistente.tipo = tipo
                itemExistente.importe = importe
                itemExistente.asignadoA = asignado
                itemExistente.fechaLimite = fecha

                Log.d("FlatmateHub", "MODIFICADO: $titulo")
                Toast.makeText(this, "Modificado: $titulo", Toast.LENGTH_SHORT).show()
            } else {
                // EVENTO AÑADIR
                val codigoPiso = FlatmateRepository.pisoActual?.codigo ?: "PISO-4821"
                val nuevo = ItemPiso(
                    id = FlatmateRepository.siguienteIdItem(),
                    codigoPiso = codigoPiso,
                    titulo = titulo,
                    descripcion = desc,
                    tipo = tipo,
                    asignadoA = asignado,
                    fechaLimite = fecha,
                    importe = importe
                )
                FlatmateRepository.items.add(0, nuevo)
                Log.d("FlatmateHub", "AÑADIDO: $titulo ($tipo)")
                Toast.makeText(this, "Añadido: $titulo", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }
}