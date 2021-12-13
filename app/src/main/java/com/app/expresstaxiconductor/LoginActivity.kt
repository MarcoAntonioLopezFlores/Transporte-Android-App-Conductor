package com.app.expresstaxiconductor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.app.expresstaxiconductor.models.Auth
import com.app.expresstaxiconductor.models.JwtResponse
import com.app.expresstaxiconductor.models.Token
import com.app.expresstaxiconductor.models.UsuarioRead
import com.app.expresstaxiconductor.navigation.NavigationDrawer
import com.app.expresstaxiconductor.preferences.PrefsApplication
import com.app.expresstaxiconductor.utils.LocationService
import com.app.expresstaxiconductor.utils.api.APIService
import com.app.expresstaxiconductor.utils.api.RetrofitClient
import com.auth0.android.jwt.JWT
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.edtEmail
import kotlinx.android.synthetic.main.activity_register.edtPassword
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnToRegister = findViewById<TextView>(R.id.txtSignup)

        if(PrefsApplication.prefs.getData("correo").isNotEmpty()){
            getToken()
            startService(Intent(this, LocationService::class.java))
            startActivity(Intent(this, NavigationDrawer::class.java))
            finish()
        }

        btnToRegister.setOnClickListener{
            startActivity(Intent(this, RegisterActivity::class.java))
        }


        btnLogin.setOnClickListener{
            if(edtEmail.text!!.isEmpty() || edtPassword.text!!.isEmpty()){
                Toast.makeText(this, "Los campos son obligatorios", Toast.LENGTH_LONG).show()
            }else{
                val apiService: APIService = RetrofitClient.getAPIService()

                apiService.login(Auth(edtEmail.text.toString(), edtPassword.text.toString())).enqueue(object:
                    Callback<JwtResponse> {
                    override fun onResponse(call: Call<JwtResponse>, response: Response<JwtResponse>) {
                        if(response.isSuccessful){
                            login(response.body() as JwtResponse)
                        }
                    }

                    override fun onFailure(call: Call<JwtResponse>, t: Throwable) {
                        mostrarMensaje()
                    }
                })
            }
        }
    }

    private fun getToken(){
        FirebaseMessaging.getInstance().isAutoInitEnabled = true

        FirebaseMessaging.getInstance().token.
        addOnCompleteListener(OnCompleteListener {
            if(!it.isSuccessful){
                println("Error in firebase -> "+it.exception)
                return@OnCompleteListener
            }

            val token = it.result
            println("token -> $token")

            val apiService: APIService = RetrofitClient.getAPIService()
            val usuarioRead = UsuarioRead(null, PrefsApplication.prefs.getData("correo"), null, token.toString())

            apiService.registrarToken(usuarioRead).enqueue(object: Callback<UsuarioRead>{
                override fun onResponse(call: Call<UsuarioRead>, response: Response<UsuarioRead>) {
//                    val usuario = response.body() as UsuarioRead
//                    PrefsApplication.prefs.save("tokenfb", usuario.tokenfb.toString())
                }

                override fun onFailure(call: Call<UsuarioRead>, t: Throwable) {
                    println("no Se realizó")
                }
            })
        })
    }

    private fun login(jwtResponse: JwtResponse){
        val token = JWT(jwtResponse.jwtToken)
        val claim = token.getClaim("perfil")
        val usuario: UsuarioRead? = claim.asObject(UsuarioRead::class.java)

        if(usuario != null){
            if(usuario.rol == "ROLE_CONDUCTOR"){
                PrefsApplication.prefs.save("token", jwtResponse.jwtToken)
                PrefsApplication.prefs.save("user_id", usuario.id.toString())
                PrefsApplication.prefs.save("correo", usuario.correo)
                PrefsApplication.prefs.save("rol", usuario.rol)

                getToken()
                startService(Intent(this, LocationService::class.java))
                startActivity(Intent(this, NavigationDrawer::class.java))
                finish()
            }else{
                Toast.makeText(this, "El usuario no cuenta con el rol de conductor", Toast.LENGTH_LONG).show()
            }
        }else{
            Toast.makeText(this, "Intente de nuevo", Toast.LENGTH_LONG).show()
        }
    }

    private fun mostrarMensaje(){
        Toast.makeText(this, "Ocurrió un error, intente de nuevo", Toast.LENGTH_LONG).show()
    }

    private fun obtenerToken(){
        val apiService: APIService = RetrofitClient.getAPIService()
        val token = PrefsApplication.prefs.getData("token")

        apiService.obtenerToken("Bearer $token", "Cliente").enqueue(object: Callback<Token>{
            override fun onResponse(call: Call<Token>, response: Response<Token>) {
                if(response.isSuccessful){
                    PrefsApplication.prefs.save("tokenclientfb", response.body()!!.descripcion)
                    println("tokenclientfb")
                }
            }

            override fun onFailure(call: Call<Token>, t: Throwable) {
                mostrarMensaje()
            }

        })
    }
}