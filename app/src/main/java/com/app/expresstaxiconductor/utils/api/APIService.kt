package com.app.expresstaxiconductor.utils.api

import com.app.expresstaxiconductor.models.*
import retrofit2.Call
import retrofit2.http.*

interface APIService {

    @POST("auth/login")
    @Headers("Content-Type: application/json")
    fun login(@Body auth: Auth): Call<JwtResponse>

    @POST("auth/token")
    @Headers("Content-Type: application/json")
    fun registrarToken(@Body token: Token): Call<Token>

    @POST("conductor/registrar")
    @Headers("Content-Type: application/json")
    fun registrarConductor(@Body conductor: Conductor): Call<Conductor>

    @POST("mensaje/registrar")
    @Headers("Content-Type: application/json")
    fun registrarMensaje(@Header("Authorization") token: String, @Body message: Message): Call<Message>

    @PUT("servicio/asignarConductor")
    @Headers("Content-Type: application/json")
    fun asignarConductor(@Header("Authorization") token: String, @Body servicio: Servicio): Call<Servicio>

    @PUT("conductor/actualizarLocalizacion")
    @Headers("Content-Type: application/json")
    fun actualizarUbicacion(@Header("Authorization") token: String, @Body conductor: Conductor): Call<Conductor>

    @PUT("servicio/cambiarEstado")
    @Headers("Content-Type: application/json")
    fun cambiarEstado(@Header("Authorization") token: String, @Body servicio: Servicio): Call<Servicio>

    @GET("auth/token/{tipo}")
    fun obtenerToken(@Header("Authorization") token: String, @Path("tipo") tipo: String): Call<Token>

    @GET("servicio/obtener/{id}")
    fun obtenerServicio(@Header("Authorization") token: String, @Path("id") id: Long): Call<Servicio>

    @GET("mensaje/listarByServicio/{id}")
    fun listarMensajes(@Header("Authorization") token: String, @Path("id") id: Long): Call<List<Message>>

}

object ApiUtils {
    private const val URL = "https://transporte-dasedatos.herokuapp.com/app/"

    val apiService: APIService
    get() =  RetrofitClient.getClient(URL)!!.create(APIService::class.java)
}