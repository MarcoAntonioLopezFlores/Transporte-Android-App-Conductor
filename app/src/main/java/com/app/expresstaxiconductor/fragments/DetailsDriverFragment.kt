package com.app.expresstaxiconductor.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.expresstaxiconductor.R
import com.app.expresstaxiconductor.models.Datos
import com.app.expresstaxiconductor.models.Estado
import com.app.expresstaxiconductor.models.Notificacion
import com.app.expresstaxiconductor.models.Servicio
import com.app.expresstaxiconductor.navigation.NavigationDrawer
import com.app.expresstaxiconductor.preferences.PrefsApplication
import com.app.expresstaxiconductor.utils.MyFirebaseMessagingService
import com.app.expresstaxiconductor.utils.api.APIFirebase
import com.app.expresstaxiconductor.utils.api.APIService
import com.app.expresstaxiconductor.utils.api.RetrofitClient
import kotlinx.android.synthetic.main.fragment_details_driver.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class DetailsDriverFragment:AppCompatActivity() {
    private val FILTRO = "broadcast_default"
    private val avance = if(PrefsApplication.prefs.getData("avance").isNotEmpty()) PrefsApplication.prefs.getData("avance") else ""
    private val KEY = "AAAADYblWbE:APA91bF3zj6eBR1Hbl75OTVMd_k7dnR4znuw2BiNxY0iKrKRrP0ZNxnlDevqSbWeAdYmoyU-KJ8F3CKuFEB6CeDykvzDNe_P_JByhLl792zh40pcZXYzL--uPoJrSOI8MtdpUKcECVK2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_details_driver)
        setSupportActionBar(findViewById(R.id.toolbarBackHome))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detalles del servicio"

        obtenerSevicio()

//        btnChatService.setOnClickListener{
//            startActivity(Intent(this,ChatServiceFragment::class.java))
//        }

        btnChat.setOnClickListener {
            startActivity(Intent(this,ChatServiceFragment::class.java))
        }

        btnLlegue.setOnClickListener {
            notificar("Llegada","Actualización del viaje", "El conductor ha llegado")
        }

        btnStartService.setOnClickListener{
            cambiarEstado("Iniciado")
        }

        btnCancelService.setOnClickListener{
            cambiarEstado("Cancelado")
        }

        btnFinishService.setOnClickListener{
            cambiarEstado("Terminado")
        }

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcast, IntentFilter(FILTRO))

        when (avance) {
            "Llegada" -> {
                btnStartService.visibility = View.VISIBLE
                btnChat.visibility = View.GONE
                btnLlegue.visibility = View.GONE
                btnCancelService.visibility = View.VISIBLE
            }
            "Inicio" -> {
                btnStartService.visibility = View.GONE
                btnFinishService.visibility = View.VISIBLE
                btnCancelService.visibility = View.GONE
                btnChat.visibility = View.GONE
                btnLlegue.visibility = View.GONE
            }
            "Cancelar" -> {
                cancelar()
            }
            "Finalizar" -> {
                finalizar()
            }
        }
    }

    private val broadcast = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent != null){
                PrefsApplication.prefs.save("avance", intent.getStringExtra("avance").toString())
            }
//            containerBtnChatService.visibility = View.VISIBLE
        }
    }

//    private val broadcastInicio = object: BroadcastReceiver(){
//        override fun onReceive(context: Context?, intent: Intent?) {
//
////            containerBtnChatService.visibility = View.VISIBLE
//        }
//    }
//
//    private val broadcastCancelar = object: BroadcastReceiver(){
//        override fun onReceive(context: Context?, intent: Intent?) {
//            cancelar()
//        }
//    }
//
//    private val broadcastFinalizar = object: BroadcastReceiver(){
//        override fun onReceive(context: Context?, intent: Intent?) {
//            finalizar()
//        }
//    }

    fun finalizar(){
        startActivity(Intent(this, NavigationDrawer::class.java))
        PrefsApplication.prefs.delete("is_service")
        PrefsApplication.prefs.delete("servicio_id")
        PrefsApplication.prefs.delete("avance")
        finish()
    }

    fun notificar(tipo: String, title: String, body: String){
        val apiFirebase: APIFirebase = RetrofitClient.getAPIFirebase()
        val notificacion = Notificacion(PrefsApplication.prefs.getData("tokenclientfb"), Datos(PrefsApplication.prefs.getData("servicio_id"), tipo,title,body))

        apiFirebase.enviarNotificacion("key=$KEY", notificacion).enqueue(object: Callback<JSONObject>{
            override fun onResponse(
                call: Call<JSONObject>,
                response: Response<JSONObject>
            ) {
                if(response.isSuccessful){
                    when (tipo) {
                        "Llegada" -> {
                            btnStartService.visibility = View.VISIBLE
                            btnChat.visibility = View.GONE
                            btnLlegue.visibility = View.GONE
                            btnCancelService.visibility = View.VISIBLE
//                            containerBtnChatService.visibility = View.VISIBLE
                            val myFirebaseMessagingService = MyFirebaseMessagingService()
                            myFirebaseMessagingService.crearBroadcast(PrefsApplication.prefs.getData("servicio_id").toLong(), "Llegada")
                        }
                        "Cancelar" -> {
                            cancelar()
                        }
                        "Inicio" -> {
                            btnStartService.visibility = View.GONE
                            btnFinishService.visibility = View.VISIBLE
                            btnCancelService.visibility = View.GONE
//                            containerBtnChatService.visibility = View.VISIBLE
                        }
                        "Finalizar" -> {
                            finalizar()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<JSONObject>, t: Throwable) {
                mostrarMensaje()
            }
        })
    }

    fun obtenerSevicio() {
        val apiService: APIService = RetrofitClient.getAPIService()
        println(PrefsApplication.prefs.SEVICIO_ID)
        val id = PrefsApplication.prefs.getData("servicio_id").toLong()
        val TOKEN = "Bearer ${PrefsApplication.prefs.getData("token")}"

        apiService.obtenerServicio(TOKEN, id).enqueue(object: Callback<Servicio>{
            override fun onResponse(call: Call<Servicio>, response: Response<Servicio>) {
                if(response.isSuccessful){
                    llenarCampos(response.body() as Servicio)
                }
            }

            override fun onFailure(call: Call<Servicio>, t: Throwable) {
                mostrarMensaje()
            }

        })
    }

    fun mostrarMensaje(){
        Toast.makeText(this, "Ocurrió un error, intente de nuevo", Toast.LENGTH_LONG).show()
    }

    fun llenarCampos(servicio: Servicio){
        if(servicio.id!! > 0){
            txtNameCustomer.text = servicio.cliente!!.usuario.nombre
            txtLastnameCustomer.text = servicio.cliente.usuario.apellidoPaterno + " " + servicio.cliente.usuario.apellidoMaterno
            txtOriginService.text = encontrarDireccion(servicio.latitudInicial, servicio.longitudInicial)
            txtDestinationService.text = encontrarDireccion(servicio.latitudFinal, servicio.longitudFinal)
        }
    }

    fun encontrarDireccion(latitud: Double, longitud: Double): String{
        val geocoder = Geocoder(this, Locale.getDefault())
        val address = geocoder.getFromLocation(latitud, longitud, 5)

        return if(address.size > 0){
            address[0].getAddressLine(0)
        }else{
            "No se encontró"
        }
    }

    fun cambiarEstado(estado: String){
        val apiService: APIService = RetrofitClient.getAPIService()
        val servicio = Servicio(PrefsApplication.prefs.getData("servicio_id").toLong(), null, 0f, 0.0,0.0,0.0,0.0,
            Estado(null, estado), null, null)
        val TOKEN = "Bearer ${PrefsApplication.prefs.getData("token")}"

        apiService.cambiarEstado(TOKEN, servicio).enqueue(object: Callback<Servicio>{
            override fun onResponse(call: Call<Servicio>, response: Response<Servicio>) {
                if(response.isSuccessful){
                    when (estado) {
                        "Cancelado" -> {
                            notificar("Cancelar","Actualización del viaje","El conductor ha cancelado el viaje")
                        }
                        "Iniciado" -> {
                            notificar("Inicio", "Actualización del viaje", "El conductor ha iniciado el viaje")
                        }
                        "Terminado" -> {
                            notificar("Finalizar", "Actualización del viaje", "El conductor ha finalizado el viaje")
                        }
                    }
                }
            }
            override fun onFailure(call: Call<Servicio>, t: Throwable) {
                println("No se cambió el estado")
            }

        })
    }

    fun cancelar(){
        startActivity(Intent(this, NavigationDrawer::class.java))
        finish()
        PrefsApplication.prefs.delete("is_service")
        PrefsApplication.prefs.delete("avance")
        PrefsApplication.prefs.delete("servicio_id")
    }

    /*override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewRoot = inflater.inflate(R.layout.fragment_details_driver, container, false)

        viewRoot.btnChatService.setOnClickListener{
            findNavController().navigate(R.id.action_detailsFragment_to_chatServiceFragment)
        }

        viewRoot.btnStartService.setOnClickListener{
            viewRoot.btnStartService.visibility = View.GONE
            viewRoot.btnFinishService.visibility = View.VISIBLE
            viewRoot.btnCancelService.visibility = View.GONE
            viewRoot.containerBtnChatService.visibility = View.VISIBLE
        }

        viewRoot.btnCancelService.setOnClickListener{

            findNavController().navigate(R.id.action_detailsFragment_to_mapsFragment)

        }

        viewRoot.btnFinishService.setOnClickListener{
            findNavController().navigate(R.id.action_detailsFragment_to_mapsFragment)

        }

        return viewRoot
    }*/


}