package com.example.flatmatehub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

/**
 * Primer paso del tutorial inicial de la aplicación.
 * Muestra la explicación inicial y permite pasar a [Tutorial2Fragment] al pulsar el botón siguiente.
 */
class Tutorial1Fragment : Fragment() {

    /**
     * Infla el diseño del primer paso del tutorial (`fragment_tutorial_1`).
     *
     * @param inflater Objeto para crear la vista desde el XML.
     * @param container Contenedor donde se coloca este fragment.
     * @param savedInstanceState Estado anterior guardado si lo hubiera.
     * @return La vista ya inflada del primer paso del tutorial.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_tutorial_1, container, false)

    /**
     * Configura el botón de siguiente para cambiar a [Tutorial2Fragment]
     * y guardar el paso en la pila por si el usuario quiere volver atrás.
     *
     * @param view Vista raíz del fragment ya creada.
     * @param savedInstanceState Estado anterior guardado si lo hubiera.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_next_tut).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_bienvenida, Tutorial2Fragment())
                .addToBackStack(null)
                .commit()
        }
    }
}