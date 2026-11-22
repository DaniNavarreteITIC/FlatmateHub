package com.example.flatmatehub

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment

/**
 * Fragment para registrar una cuenta nueva en la app.
 * Recoge los datos del usuario, valida que estén bien rellenos y coincidan las contraseñas,
 * y si todo va bien guarda el usuario en [FlatmateRepository] y pasa a [BienvenidaFlatmate].
 */
class RegisterFragment : Fragment() {

    /**
     * Infla el diseño del formulario de registro (`fragment_register`).
     *
     * @param inflater Objeto para convertir el XML en vista.
     * @param container Contenedor padre donde se muestra el fragment.
     * @param savedInstanceState Estado anterior guardado si lo hubiera.
     * @return La vista ya inflada del fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    /**
     * Prepara los campos del formulario, el enlace para volver al login y el botón de registro.
     * Revisa que no haya campos vacíos, que el email tenga formato válido, que la clave tenga
     * al menos 4 caracteres y que ambas contraseñas sean iguales antes de crear la cuenta.
     *
     * @param view Vista raíz del fragment ya creada.
     * @param savedInstanceState Estado anterior guardado si lo hubiera.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etName = view.findViewById<EditText>(R.id.et_name)
        val etSurname = view.findViewById<EditText>(R.id.et_surname)
        val etPhone = view.findViewById<EditText>(R.id.et_phone)
        val etEmail = view.findViewById<EditText>(R.id.et_email)
        val etPassword = view.findViewById<EditText>(R.id.et_password)
        val etPasswordRepeat = view.findViewById<EditText>(R.id.et_password_repeat)
        val btnDoRegister = view.findViewById<Button>(R.id.btn_do_register)
        val tvGoLogin = view.findViewById<TextView>(R.id.tv_go_login)

        tvGoLogin.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }

        btnDoRegister.setOnClickListener {
            val nombre = etName.text.toString().trim()
            val apellidos = etSurname.text.toString().trim()
            val telefono = etPhone.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val passwordRepeat = etPasswordRepeat.text.toString()

            when {
                nombre.isEmpty() || apellidos.isEmpty() || telefono.isEmpty() || email.isEmpty() || password.isEmpty() -> {
                    Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    etEmail.error = "Introduce un correo válido"
                }
                password.length < 4 -> {
                    etPassword.error = "Mínimo 4 caracteres"
                }
                password != passwordRepeat -> {
                    etPasswordRepeat.error = "Las contraseñas no coinciden"
                }
                else -> {
                    val nuevoUsuario = FlatmateRepository.registrarUsuario("$nombre $apellidos", email, password)
                    if (nuevoUsuario != null) {
                        Toast.makeText(requireContext(), "Cuenta creada para $nombre", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(requireContext(), BienvenidaFlatmate::class.java))
                        requireActivity().finish()
                    } else {
                        etEmail.error = "Este correo ya está registrado"
                    }
                }
            }
        }
    }
}