package com.app.expresstaxiconductor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AutoRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_register)

        val btnCancelAuto = findViewById<Button>(R.id.btnCancelRegisterAuto)
        val btnFinish = findViewById<Button>(R.id.btnFinish)

        btnFinish.setOnClickListener {
            startActivity(Intent(this, AutoRegisterActivity::class.java))
        }

        btnCancelAuto.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}