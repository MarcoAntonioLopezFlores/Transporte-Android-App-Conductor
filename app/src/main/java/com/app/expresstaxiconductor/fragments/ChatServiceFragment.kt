package com.app.expresstaxiconductor.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.expresstaxiconductor.R
import com.app.expresstaxiconductor.adapters.MessageAdapter
import com.app.expresstaxiconductor.models.*
import com.app.expresstaxiconductor.preferences.PrefsApplication
import com.app.expresstaxiconductor.utils.api.APIFirebase
import com.app.expresstaxiconductor.utils.api.APIService
import com.app.expresstaxiconductor.utils.api.RetrofitClient
import kotlinx.android.synthetic.main.fragment_chat_service.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChatServiceFragment : AppCompatActivity() {
    private var messageList = ArrayList<Message>()
    private  lateinit var messageAdapter:MessageAdapter
    private val FILTER_CHAT = "broadcast_chat"
    companion object{
        var chatActive = false
    }
    private val KEY = "AAAADYblWbE:APA91bF3zj6eBR1Hbl75OTVMd_k7dnR4znuw2BiNxY0iKrKRrP0ZNxnlDevqSbWeAdYmoyU-KJ8F3CKuFEB6CeDykvzDNe_P_JByhLl792zh40pcZXYzL--uPoJrSOI8MtdpUKcECVK2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_chat_service)
        setSupportActionBar(findViewById(R.id.toolbarBackDetails))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Chat"

//        messageList.add(Message("Hola","A12S3X",1))
//        messageList.add(Message("Adios","xaxaxa",2))

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(broadcast, IntentFilter(FILTER_CHAT))

        listarMensajes()
        println(PrefsApplication.prefs.getData("servicio_id"))
        btnSendMessage.setOnClickListener{
            if (edtMessage.text!!.isNotEmpty()){
                sendMessage(edtMessage.text.toString())
            }else{
                Toast.makeText(this, "Ingrese un mensaje, por favor", Toast.LENGTH_LONG).show()
            }
        }
    }

    val broadcast = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            listarMensajes()
        }

    }

    private fun sendMessage(message:String){
        val apiService: APIService = RetrofitClient.getAPIService()
        val idUsuario = PrefsApplication.prefs.getData("user_id").toLong()
        val idServicio = PrefsApplication.prefs.getData("servicio_id").toLong()
        val correo = PrefsApplication.prefs.getData("correo")

        val usuario = Usuario(idUsuario, "","",correo, "","","","",true, Rol(null, "",""))
        val servicio = Servicio(idServicio, null, null, 0.0, 0.0, 0.0, 0.0, null, null, null)
        val TOKEN = "Bearer ${PrefsApplication.prefs.getData("token")}"

        apiService.registrarMensaje(TOKEN, Message(null, message, null, usuario, servicio)).enqueue(object: Callback<Message>{
            override fun onResponse(call: Call<Message>, response: Response<Message>) {
                if(response.isSuccessful){
                    enviarmensaje(response.body() as Message)
                    listarMensajes()
                }
            }

            override fun onFailure(call: Call<Message>, t: Throwable) {
                mostrarMensaje()
            }

        })
    }

    fun enviarmensaje(message: Message){
        val apiFirebase: APIFirebase = RetrofitClient.getAPIFirebase()
        val notificacion = Notificacion(PrefsApplication.prefs.getData("tokenclientfb"), Datos(PrefsApplication.prefs.getData("servicio_id"), "Chat","Nuevo mensaje",message.descripcion))

        apiFirebase.enviarNotificacion("key=$KEY", notificacion).enqueue(object:
            Callback<JSONObject> {
            override fun onResponse(
                call: Call<JSONObject>,
                response: Response<JSONObject>
            ) {
                if(response.isSuccessful){
                    println("Se envió la notificación")
                }
            }

            override fun onFailure(call: Call<JSONObject>, t: Throwable) {
                mostrarMensaje()
            }
        })
    }

    fun listarMensajes(){
        val apiService: APIService = RetrofitClient.getAPIService()
        val idServicio = PrefsApplication.prefs.getData("servicio_id").toLong()
        val TOKEN = "Bearer ${PrefsApplication.prefs.getData("token")}"

        apiService.listarMensajes(TOKEN, idServicio).enqueue(object: Callback<List<Message>>{
            override fun onResponse(call: Call<List<Message>>, response: Response<List<Message>>) {
                if(response.isSuccessful){
                    asignarLista(response.body() as ArrayList<Message>)
                }
            }

            override fun onFailure(call: Call<List<Message>>, t: Throwable) {
                mostrarMensaje()
            }

        })
    }

    fun asignarLista(messages: ArrayList<Message>){
        messageList = messages
        messageAdapter = MessageAdapter(this, messageList)
        recyclerViewChat.adapter = messageAdapter
        edtMessage.setText("")
        messageAdapter.notifyDataSetChanged()
        recyclerViewChat.setSelection(messageAdapter.count - 1)
    }

    fun mostrarMensaje(){
        Toast.makeText(this, "Ocurrió un error, intente de nuevo", Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()
        chatActive=true
    }

    override fun onStop() {
        super.onStop()
        chatActive=false
    }

    /*override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val viewRoot =inflater.inflate(R.layout.fragment_chat_service, container, false)

        val messageList = ArrayList<Message>()
        viewRoot.recyclerViewChat.layoutManager= LinearLayoutManager(context)
        messageList.add(Message("Hola","A12S3X",1))
        messageList.add(Message("Adios","xaxaxa",2))
        var myAdapterMessage = activity?.let {
            MessageAdapter(it,messageList)
        }

        viewRoot.recyclerViewChat.adapter=myAdapterMessage

        myAdapterMessage!!.notifyDataSetChanged()
        viewRoot.btnSendMessage.setOnClickListener{
            Toast.makeText(context, "Mensaje", Toast.LENGTH_SHORT).show()
        }
        return viewRoot

    }*/


}