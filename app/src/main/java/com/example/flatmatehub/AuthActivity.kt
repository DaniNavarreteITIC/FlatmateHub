package com.example.flatmatehub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Actividad principal para la parte de autenticación (login y registro).
 * Sirve como contenedor para ir cargando los distintos fragments de acceso
 * dentro de `fragment_container`.
 */
class AuthActivity : AppCompatActivity() {

    /**
     * Carga el layout de la actividad y muestra por defecto el [LoginFragment]
     * al abrir la pantalla por primera vez.
     *
     * @param savedInstanceState Guarda el estado de la pantalla por si se gira el móvil y no repetir el fragment.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, LoginFragment())
                .commit()
        }
    }
}