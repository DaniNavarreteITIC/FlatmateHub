package com.example.flatmatehub

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

/**
 * Pantalla de inicio (splash) que se ve al abrir la aplicación.
 * Hace una pequeña animación de entrada con el logo y, pasados 2 segundos,
 * abre [AuthActivity] cerrando esta pantalla para no volver atrás a ella.
 */
class SplashActivity : AppCompatActivity() {

    /**
     * Carga `activity_splash`, anima la opacidad y el tamaño del logo y programa
     * el salto automático hacia [AuthActivity].
     *
     * @param savedInstanceState Estado anterior guardado de la actividad si existe.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val ivLogo = findViewById<ImageView>(R.id.ivLogoSplash)
        ivLogo.alpha = 0f
        ivLogo.scaleX = 0.75f
        ivLogo.scaleY = 0.75f

        ivLogo.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(1100L)
            .start()

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }, 2000L)
    }
}