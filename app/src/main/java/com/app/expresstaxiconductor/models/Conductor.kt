package com.app.expresstaxiconductor.models

import java.util.*

data class Conductor(
    val id: Long?,
    val fechaRegistro: Date?,
    val vehiculo: Vehiculo?,
    val usuario: Usuario,
    val localizacion: Localizacion?
)
