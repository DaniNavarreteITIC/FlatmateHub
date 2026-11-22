package com.example.flatmatehub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

/**
 * Tercer y último paso del tutorial inicial.
 * Al pulsar el botón de terminar el tutorial, cambia a [WelcomeFlatFragment]
 * para que el usuario elija si quiere crear un piso o unirse a uno.
 */
class Tutorial3Fragment : Fragment() {

    /**
     * Infla el diseño del tercer paso del tutorial (`fragment_tutorial_3`).
     *
     * @param inflater Objeto para crear la vista desde el XML.
     * @param container Contenedor donde se muestra este fragment.
     * @param savedInstanceState Estado anterior guardado si lo hubiera.
     * @return La vista ya inflada del tercer paso del tutorial.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_tutorial_3, container, false)

    /**
     * Configura el botón final del tutorial para llevar al usuario a [WelcomeFlatFragment].
     *
     * @param view Vista raíz del fragment ya creada.
     * @param savedInstanceState Estado anterior guardado si lo hubiera.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_finish_tut).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_bienvenida, WelcomeFlatFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}