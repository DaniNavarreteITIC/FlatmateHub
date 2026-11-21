package com.example.flatmatehub
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial
/**
 * Activitat de preferències i ajustos del compte i del pis actiu.
 * Permet modificar el nom de l'usuari autenticat, actualitzar el nom del pis,
 * consultar el seu codi únic d'invitació i activar o desactivar el Mode Fosc de la interfície.
 */
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnAtras = findViewById<View>(R.id.btnAtrasPreferencias)
        val btnGuardar = findViewById<Button>(R.id.btnGuardarPreferencias)
        val etNombreUser = findViewById<EditText>(R.id.etEditarNombreUsuario)
        val etNombrePiso = findViewById<EditText>(R.id.etEditarNombrePiso)
        val tvCodigoPiso = findViewById<TextView>(R.id.tvCodigoPisoPreferencias)
        val switchModoOscuro = findViewById<SwitchMaterial>(R.id.switchModoOscuro)

        val user = FlatmateRepository.usuarioActual
        val piso = FlatmateRepository.pisoActual

        etNombreUser.setText(user?.nombre ?: "")
        etNombrePiso.setText(piso?.nombre ?: "")
        tvCodigoPiso.text = "Código único del piso: ${piso?.codigo ?: "---"}"

        switchModoOscuro.isChecked =
            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        switchModoOscuro.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        btnAtras?.setOnClickListener { finish() }

        btnGuardar.setOnClickListener {
            val nuevoNombreUser = etNombreUser.text.toString().trim()
            val nuevoNombrePiso = etNombrePiso.text.toString().trim()

            if (user != null && nuevoNombreUser.isNotEmpty()) {
                val nombreAnterior = user.nombre
                user.nombre = nuevoNombreUser
                piso?.miembros?.replaceAll { if (it == nombreAnterior) nuevoNombreUser else it }
            }
            if (piso != null && nuevoNombrePiso.isNotEmpty()) {
                piso.nombre = nuevoNombrePiso
            }

            Toast.makeText(this, "Preferencias guardadas", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}