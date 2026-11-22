package com.example.flatmatehub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

/**
 * Segundo paso del tutorial de bienvenida.
 * Muestra la segunda pantalla explicativa y, al pulsar el botón de siguiente,
 * pasa a [Tutorial3Fragment] guardando el paso anterior en el historial.
 */
class Tutorial2Fragment : Fragment() {

    /**
     * Infla el diseño del segundo paso del tutorial (`fragment_tutorial_2`).
     *
     * @param inflater Objeto para convertir el XML en vista.
     * @param container Contenedor padre donde se coloca este fragment.
     * @param savedInstanceState Estado anterior guardado si lo hubiera.
     * @return La vista ya inflada del segundo paso del tutorial.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_tutorial_2, container, false)

    /**
     * Asigna el clic del botón siguiente para ir a [Tutorial3Fragment]
     * dentro del contenedor de bienvenida.
     *
     * @param view Vista raíz del fragment ya creada.
     * @param savedInstanceState Estado anterior guardado si lo hubiera.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_next_tut_2).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_bienvenida, Tutorial3Fragment())
                .addToBackStack(null)
                .commit()
        }
    }
}