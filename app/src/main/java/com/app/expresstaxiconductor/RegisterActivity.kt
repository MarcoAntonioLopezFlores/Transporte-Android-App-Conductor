package com.app.expresstaxiconductor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_auto_register.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnCancel = findViewById<Button>(R.id.btnCancelRegister)
        val btnContinue = findViewById<Button>(R.id.btnContinue)

        btnContinue.setOnClickListener {
            if(edtName.text!!.isNotEmpty() && edtLastname.text!!.isNotEmpty() && edtEmail.text!!.isNotEmpty() && edtPassword.text!!.isNotEmpty() && edtPasswordConfirm.text!!.isNotEmpty()){
                if(edtPassword.text.toString() != edtPasswordConfirm.text.toString()){
                    Toast.makeText(this, "Las contraseñas deben ser iguales", Toast.LENGTH_LONG).show()
                }else{
                    val intent = Intent(this, AutoRegisterActivity::class.java)
                    intent.putExtra("name", edtName.text.toString())
                    intent.putExtra("lastname", edtLastname.text.toString())
                    intent.putExtra("email", edtEmail.text.toString())
                    intent.putExtra("password", edtPassword.text.toString())
                    intent.putExtra("phone", edtPhone.text.toString())
                    startActivity(intent)
                }
            }else{
                Toast.makeText(this, "Los campos marcados con asteriscos son obligatorios", Toast.LENGTH_LONG).show()
            }
        }

        btnCancel.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }




}