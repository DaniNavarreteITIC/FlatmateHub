package com.example.flatmatehub

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

/**
 * Fragment de inicio de sesión dentro de la pantalla de autenticación.
 * Permite entrar con un usuario existente o cambiar a [RegisterFragment]
 * si todavía no se tiene cuenta creada.
 */
class LoginFragment : Fragment() {

    /**
     * Infla el layout del formulario de login (`fragment_login`).
     *
     * @param inflater Objeto para convertir el XML en vista.
     * @param container Contenedor padre del fragment.
     * @param savedInstanceState Estado anterior guardado si lo hubiera.
     * @return La vista ya inflada del login.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    /**
     * Prepara los eventos del enlace para registrarse y del botón de iniciar sesión.
     * Valida que haya correo y contraseña, comprueba los datos en [FlatmateRepository]
     * y manda a [MainActivity] si ya tiene piso o a [BienvenidaFlatmate] si aún no tiene.
     *
     * @param view Vista raíz del fragment ya creada.
     * @param savedInstanceState Estado anterior guardado si lo hubiera.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etEmail = view.findViewById<EditText>(R.id.et_email)
        val etPassword = view.findViewById<EditText>(R.id.et_password)
        val btnLogin = view.findViewById<Button>(R.id.btn_login)
        val tvGoRegister = view.findViewById<TextView>(R.id.tv_go_register)

        tvGoRegister.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Rellena correo y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val usuario = FlatmateRepository.iniciarSesion(email, password)
            if (usuario != null) {
                Toast.makeText(requireContext(), "¡Bienvenido, ${usuario.nombre}!", Toast.LENGTH_SHORT).show()
                val destino = if (FlatmateRepository.pisoActual != null) {
                    MainActivity::class.java
                } else {
                    BienvenidaFlatmate::class.java
                }
                startActivity(Intent(requireContext(), destino))
                requireActivity().finish()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Credenciales incorrectas (prueba alex@piso.com / 1234)",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}