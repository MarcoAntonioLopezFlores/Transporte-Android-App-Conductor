package com.app.expresstaxiconductor.models

import java.util.*

data class Message(
    val id: Long?,
    val descripcion: String,
    val fechaRegistro: Date?,
    val usuario: Usuario,
    val servicio: Servicio
)