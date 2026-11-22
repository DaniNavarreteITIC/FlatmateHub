package com.example.flatmatehub

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

/**
 * Pantalla intermedia para elegir qué hacer con el piso:
 * abrir [CreateFlatFragment] para crear uno nuevo o [JoinFlatFragment]
 * para entrar a uno ya existente con su código.
 */
class WelcomeFlatFragment : Fragment() {

    /**
     * Infla el diseño de selección de piso (`fragment_welcome_flat`).
     *
     * @param inflater Objeto para convertir el XML en vista.
     * @param container Contenedor padre donde se coloca el fragment.
     * @param savedInstanceState Estado anterior guardado si lo hubiera.
     * @return La vista ya inflada de bienvenida al piso.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_welcome_flat, container, false)

    /**
     * Prepara los dos botones principales: uno para ir a crear piso ([CreateFlatFragment])
     * y otro para unirse mediante código ([JoinFlatFragment]).
     *
     * @param view Vista raíz del fragment ya creada.
     * @param savedInstanceState Estado anterior guardado si lo hubiera.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_go_create_flat).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_bienvenida, CreateFlatFragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<Button>(R.id.btn_go_join_flat).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_bienvenida, JoinFlatFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}