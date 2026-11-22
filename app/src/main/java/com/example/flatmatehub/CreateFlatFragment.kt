package com.example.flatmatehub

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

/**
 * Fragment para crear un piso nuevo dentro de la app.
 * Pide un nombre para el piso, llama al repositorio para generar su código único
 * y muestra un aviso con el código antes de pasar a la pantalla principal.
 */
class CreateFlatFragment : Fragment() {

    /**
     * Infla el diseño XML del fragment para crear piso (`fragment_create_flat`).
     *
     * @param inflater Objeto para convertir el XML en vista.
     * @param container Contenedor padre donde irá este fragment.
     * @param savedInstanceState Estado guardado anterior si lo hubiera.
     * @return La vista ya inflada del fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_create_flat, container, false)

    /**
     * Prepara los campos y el botón de crear piso una vez que la vista ya existe.
     * Comprueba que el nombre no esté vacío, crea el piso y abre un diálogo con
     * el código generado para ir a [MainActivity] limpiando las pantallas anteriores.
     *
     * @param view Vista raíz del fragment ya creada.
     * @param savedInstanceState Estado guardado anterior si lo hubiera.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etNombrePiso = view.findViewById<EditText>(R.id.et_flat_code)
        val btnCrearPiso = view.findViewById<Button>(R.id.btn_confirm_create)

        btnCrearPiso.setOnClickListener {
            val nombrePiso = etNombrePiso.text.toString().trim()
            if (nombrePiso.isEmpty()) {
                etNombrePiso.error = "Introduce un nombre para el piso"
                return@setOnClickListener
            }

            val pisoCreado = FlatmateRepository.crearPiso(nombrePiso)
            if (pisoCreado != null) {
                AlertDialog.Builder(requireContext())
                    .setTitle("¡Piso creado con éxito!")
                    .setMessage("Tu código único de piso es:\n\n${pisoCreado.codigo}\n\nCompártelo con tus compañeros para que puedan unirse.")
                    .setCancelable(false)
                    .setPositiveButton("Entrar al piso") { _, _ ->
                        val intent = Intent(requireContext(), MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    .show()
            }
        }
    }
}