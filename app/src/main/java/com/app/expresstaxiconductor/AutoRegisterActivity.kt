package com.app.expresstaxiconductor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.app.expresstaxiconductor.models.*
import com.app.expresstaxiconductor.utils.api.APIService
import com.app.expresstaxiconductor.utils.api.RetrofitClient
import kotlinx.android.synthetic.main.activity_auto_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AutoRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auto_register)

        val btnCancelAuto = findViewById<Button>(R.id.btnCancelRegisterAuto)
        val btnFinish = findViewById<Button>(R.id.btnFinish)

        btnFinish.setOnClickListener {
            if(edtModelAuto.text!!.isNotEmpty() && edtBrandAuto.text!!.isNotEmpty() && edtColorAuto.text!!.isNotEmpty() && edtPlacasAuto.text!!.isNotEmpty()){
                val apiService: APIService = RetrofitClient.getAPIService()
                val lastName = intent.getStringExtra("lastname").toString().split(" ")
                var apellidoP = ""; var apellidoM = ""
                if(lastName.size > 1){
                    apellidoP = lastName[0]
                    apellidoM = lastName[1]
                }else{
                    apellidoP = lastName[0]
                }

                val rol = Rol(null,"","")
                val usuario = Usuario(null, intent.getStringExtra("password").toString(), intent.getStringExtra("phone").toString(), intent.getStringExtra("email").toString(),
                    intent.getStringExtra("name").toString(), apellidoP, apellidoM, "", true, rol, null)
                val localizacion = Localizacion(null, 0.0, 0.0)
                val vehiculo = Vehiculo(null, edtPlacasAuto.text.toString(), edtColorAuto.text.toString(), true, edtModelAuto.text.toString(), edtBrandAuto.text.toString())

                apiService.registrarConductor(Conductor(null, null, vehiculo, usuario, localizacion)).enqueue(object: Callback<Conductor>{
                    override fun onResponse(call: Call<Conductor>, response: Response<Conductor>) {
                        if (response.isSuccessful){
                            registrar(response.body() as Conductor)
                        }
                    }

                    override fun onFailure(call: Call<Conductor>, t: Throwable) {
                        mostrarMensaje()
                    }
                })
            }else{
                Toast.makeText(this, "Los campos marcados con asteriscos son obligatorios", Toast.LENGTH_LONG).show()
            }
        }

        btnCancelAuto.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun registrar(conductor: Conductor){
        if(conductor.id as Long > 0){
            Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun mostrarMensaje(){
        Toast.makeText(this, "Ocurrió un error, intente de nuevo", Toast.LENGTH_LONG).show()
    }
}