package com.example.flatmatehub

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch

/**
 * Activitat de preferències i ajustos del compte i del pis actiu (`fragment_preferences.xml`).
 * Permet editar el nom de l'usuari, canviar el nom del pis, consultar els membres i el codi únic
 * d'invitació, i activar o desactivar el Mode Fosc de la interfície.
 */
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_preferences)

        val btnGuardar = findViewById<MaterialButton>(R.id.btn_save_prefs)
        val btnEditarPerfil = findViewById<TextView>(R.id.btn_edit_name_email)
        val btnEditarNombrePiso = findViewById<TextView>(R.id.btn_edit_flat_name)
        val btnVerUsuariosPiso = findViewById<TextView>(R.id.btn_edit_flat_users)
        val btnVerCodigoPiso = findViewById<TextView>(R.id.btn_view_flat_code)
        val switchDarkMode = findViewById<MaterialSwitch>(R.id.switch_dark_mode)

        val user = FlatmateRepository.usuarioActual
        val piso = FlatmateRepository.pisoActual

        // Edita el nom de l'usuari
        btnEditarPerfil.setOnClickListener {
            val input = EditText(this).apply {
                setText(user?.nombre ?: "")
                hint = "Nuevo nombre"
            }
            AlertDialog.Builder(this)
                .setTitle("Editar nombre de usuario")
                .setView(input)
                .setPositiveButton("Guardar") { _, _ ->
                    val nuevoNombre = input.text.toString().trim()
                    if (user != null && nuevoNombre.isNotEmpty()) {
                        val anterior = user.nombre
                        user.nombre = nuevoNombre
                        piso?.miembros?.replaceAll { if (it == anterior) nuevoNombre else it }
                        Toast.makeText(this, "Nombre actualizado a $nuevoNombre", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // Edita el nom del pis
        btnEditarNombrePiso.setOnClickListener {
            val input = EditText(this).apply {
                setText(piso?.nombre ?: "")
                hint = "Nuevo nombre del piso"
            }
            AlertDialog.Builder(this)
                .setTitle("Editar nombre del piso")
                .setView(input)
                .setPositiveButton("Guardar") { _, _ ->
                    val nuevoNombrePiso = input.text.toString().trim()
                    if (piso != null && nuevoNombrePiso.isNotEmpty()) {
                        piso.nombre = nuevoNombrePiso
                        Toast.makeText(this, "Piso renombrado a $nuevoNombrePiso", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // Mostra els companys del pis
        btnVerUsuariosPiso.setOnClickListener {
            val miembros = piso?.miembros?.joinToString("\n• ", prefix = "• ") ?: "Sin miembros"
            AlertDialog.Builder(this)
                .setTitle("Compañeros en ${piso?.nombre ?: "el piso"}")
                .setMessage(miembros)
                .setPositiveButton("Cerrar", null)
                .show()
        }

        // Mostra el codi únic del pis
        btnVerCodigoPiso.setOnClickListener {
            val codigo = piso?.codigo ?: "Sin código"
            AlertDialog.Builder(this)
                .setTitle("Código único del piso")
                .setMessage("Comparte este código para que tus compañeros se unan:\n\n$codigo")
                .setPositiveButton("Aceptar", null)
                .show()
        }

        // Mode fosc
        switchDarkMode.isChecked =
            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        // Botó Desa / Torna
        btnGuardar.setOnClickListener {
            Toast.makeText(this, "Preferencias guardadas", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}