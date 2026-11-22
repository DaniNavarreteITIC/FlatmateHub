package com.example.flatmatehub

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Actividad principal de la aplicación a la que se llega cuando el usuario
 * ya tiene sesión iniciada y un piso asignado. De momento carga su layout
 * y ajusta los márgenes de las barras del sistema.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Activa el modo edge-to-edge, carga `activity_main` y aplica el padding
     * de las barras del sistema (arriba y abajo) para que el contenido no quede tapado.
     *
     * @param savedInstanceState Estado guardado anterior de la actividad si existe.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}