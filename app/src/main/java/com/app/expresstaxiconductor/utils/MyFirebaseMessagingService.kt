package com.app.expresstaxiconductor.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.app.expresstaxiconductor.LoginActivity
import com.app.expresstaxiconductor.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if(remoteMessage.data.isNotEmpty()){
            println("Mensaje ->"+remoteMessage.data.toString())
        }

        remoteMessage.notification?.let{
            println("Firebase notification -> "+it.body.toString())
            showNotification(it.body.toString())
        }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        sendRegistrationToServer(newToken)
    }

    private fun sendRegistrationToServer(token:String){
        println("enviando token al web service -> "+token)
    }

    private fun showNotification(mensaje:String){
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_ONE_SHOT)

        val channelID = getString(R.string.app_name)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder=NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.ic_location)
            .setContentTitle("Titulo")
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
}