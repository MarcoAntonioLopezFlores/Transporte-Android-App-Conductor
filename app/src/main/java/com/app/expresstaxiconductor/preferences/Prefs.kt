package com.app.expresstaxiconductor.preferences

import android.content.Context
import android.content.SharedPreferences

class Prefs(context: Context) {
    val PREFS_NAME = "mx.edu.utez"
    val TOKEN= "token"
    val TOKEN_FB= "tokenfb"
    val TOKEN_CLIENT_FB= "tokenclientfb"
    val USER_ID="user_id"
    val SEVICIO_ID="servicio_id"
    val CORREO="correo"
    val ROL="rol"
    val IS_SERVICE="is_service"
    val AVANCE="avance"

    val STORAGE: SharedPreferences = context.getSharedPreferences(PREFS_NAME,0)


    fun save(key:String,value:String){
        STORAGE.edit().putString(key, value).apply()
    }

    fun getData(key:String):String{
        return STORAGE.getString(key,"")!!
    }

    fun delete(key: String){
        STORAGE.edit().remove(key).apply()
    }

    fun deleteAll(){
        STORAGE.edit().clear().apply()
    }
}