package com.decadev.ucpromap

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.decadev.ucpromap.ui.FilterFragment

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            val myIntent = Intent(this, FilterFragment::class.java)
            startActivity(myIntent)
            finish()
        }, 3000)
    }
}