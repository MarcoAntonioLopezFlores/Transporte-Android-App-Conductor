package com.app.expresstaxiconductor.models

import java.util.*

data class Cliente(
    val id: Long?,
    val fechaRegistro: Date?,
    val usuario: Usuario,
    val localizacion: Localizacion
)
