package com.app.expresstaxiconductor.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.app.expresstaxiconductor.R
import com.app.expresstaxiconductor.fragments.ChatServiceFragment
import com.app.expresstaxiconductor.models.Servicio
import com.app.expresstaxiconductor.navigation.NavigationDrawer
import com.app.expresstaxiconductor.preferences.PrefsApplication
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if(remoteMessage.data.isNotEmpty()){
            println("Mensaje ->"+remoteMessage.data.toString())
            val tipo = remoteMessage.data["tipo"].toString()
            if(tipo != "Cancelar" && tipo != "Finalizar"){
                PrefsApplication.prefs.save("servicio_id", remoteMessage.data["servicio"].toString())
            }
            crearBroadcast(remoteMessage.data["servicio"].toString().toLong(), tipo)
            showNotification(remoteMessage.data["title"].toString(),remoteMessage.data["body"].toString(), tipo)
        }

//        remoteMessage.notification?.let{
//            println("Firebase notification -> "+it.body.toString())
//            showNotification(it.body.toString())
//        }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        sendRegistrationToServer(newToken)
    }

    private fun sendRegistrationToServer(token:String){
        println("enviando token al web service -> "+token)
    }

    private fun showNotification(title:String, mensaje:String, tipo:String){
        val intent: Intent

        if (tipo == "Chat"){
            intent = Intent(this, ChatServiceFragment::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }else {
            intent = Intent(this, NavigationDrawer::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                    or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT)

        val channelID = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder=NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.ic_location)
            .setContentTitle(title)
            .setContentText(mensaje)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelID, "title",
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        //notificationManager.createNotificationChannel()

        notificationManager.notify(0, notificationBuilder.build())
    }


    fun crearBroadcast(id: Long, tipo: String) {
        val filtro = when (tipo) {
            "Chat" -> {
                "broadcast_chat"
            }
            "Servicio" -> {
                "broadcast_servicio"
            }
            "Llegada" -> {
                "broadcast_default"
            }
            "Inicio" -> {
                "broadcast_default"
            }
            "Cancelar" -> {
                "broadcast_default"
            }
            "Finalizar" -> {
                "broadcast_default"
            }
            else -> {
                "broadcast_default"
            }
        }

        if(filtro == "broadcast_default"){
            PrefsApplication.prefs.delete("servicio_id")
        }

        val intent = Intent(filtro)
        intent.putExtra("id", id.toString())
        intent.putExtra("filtro", filtro)
        intent.putExtra("avance", tipo)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}