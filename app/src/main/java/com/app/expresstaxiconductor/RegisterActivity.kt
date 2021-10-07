package com.app.expresstaxiconductor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnCancel = findViewById<Button>(R.id.btnCancelRegister)
        val btnContinue = findViewById<Button>(R.id.btnContinue)

        btnContinue.setOnClickListener {
            startActivity(Intent(this, AutoRegisterActivity::class.java))
        }

        btnCancel.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }




}