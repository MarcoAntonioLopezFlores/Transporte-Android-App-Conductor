package com.app.expresstaxiconductor.utils.api

import com.app.expresstaxiconductor.models.Notificacion
import org.json.JSONObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIFirebase {

    @POST("fcm/send")
    @Headers("Content-Type: application/json")
    fun enviarNotificacion(@Header("Authorization") token: String, @Body notificacion: Notificacion): Call<JSONObject>
}

object ApiFirebaseUtils {
    private const val URL_FIREBASE = "https://fcm.googleapis.com/"

    val apiFirebase: APIFirebase
    get() =  RetrofitClient.getClient(URL_FIREBASE)!!.create(APIFirebase::class.java)
}