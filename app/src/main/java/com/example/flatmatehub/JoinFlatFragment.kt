package com.example.flatmatehub

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment

/**
 * Fragment para unirse a un piso que ya existe escribiendo su código.
 * Comprueba que no esté vacío, lo pasa a mayúsculas y busca si coincide con algún
 * piso guardado en [FlatmateRepository] para entrar directamente a [MainActivity].
 */
class JoinFlatFragment : Fragment() {

    /**
     * Infla el diseño del fragment para unirse a un piso (`fragment_join_flat`).
     *
     * @param inflater Objeto para crear la vista a partir del XML.
     * @param container Contenedor donde se coloca este fragment.
     * @param savedInstanceState Estado anterior guardado si lo hubiera.
     * @return La vista ya inflada del fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_join_flat, container, false)

    /**
     * Configura el campo del código y el botón de confirmar cuando la vista ya está creada.
     * Si el código existe, muestra un mensaje, limpia el historial de pantallas anteriores
     * y abre [MainActivity]; si no, marca error en el campo de texto.
     *
     * @param view Vista raíz del fragment ya creada.
     * @param savedInstanceState Estado anterior guardado si lo hubiera.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etCodigoPiso = view.findViewById<EditText>(R.id.et_flat_code)
        val btnConfirmJoin = view.findViewById<Button>(R.id.btn_confirm_join)

        btnConfirmJoin.setOnClickListener {
            val codigo = etCodigoPiso.text.toString().trim().uppercase()

            if (codigo.isEmpty()) {
                etCodigoPiso.error = "Introduce el código del piso"
                return@setOnClickListener
            }

            val pisoEncontrado = FlatmateRepository.unirseAPiso(codigo)
            if (pisoEncontrado != null) {
                Toast.makeText(
                    requireContext(),
                    "Te has unido a ${pisoEncontrado.nombre}",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(requireContext(), MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                requireActivity().finish()
            } else {
                etCodigoPiso.error = "Código no encontrado (prueba P-4821)"
            }
        }
    }
}