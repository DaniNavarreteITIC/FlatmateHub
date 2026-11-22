package com.example.flatmatehub

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Actividad que controla el flujo de bienvenida después de entrar a la app.
 * Dependiendo de si el usuario es nuevo o ya tiene cuenta, decide si mostrarle
 * el tutorial paso a paso o mandarlo directo a elegir/crear piso.
 */
class BienvenidaFlatmate : AppCompatActivity() {

    /**
     * Inicia la vista y lee el dato `"SALTAR_TUTORIAL"` que llega desde el Intent
     * para cargar [WelcomeFlatFragment] o empezar por [Tutorial1Fragment].
     *
     * @param savedInstanceState Estado guardado de la actividad si se recrea la vista.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenida_flatmate)

        if (savedInstanceState == null) {
            val saltarTutorial = intent.getBooleanExtra("SALTAR_TUTORIAL", false)
            val fragmentInicial = if (saltarTutorial) {
                WelcomeFlatFragment()
            } else {
                Tutorial1Fragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container_bienvenida, fragmentInicial)
                .commit()
        }
    }
}