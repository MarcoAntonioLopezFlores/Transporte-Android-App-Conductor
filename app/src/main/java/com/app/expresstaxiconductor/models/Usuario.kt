package com.app.expresstaxiconductor.models

data class Usuario(
    val id: Long?,
    val contrasenia: String,
    val telefono: String,
    val correo: String,
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String,
    val foto: String,
    val enabled: Boolean,
    val rol: Rol?
)
