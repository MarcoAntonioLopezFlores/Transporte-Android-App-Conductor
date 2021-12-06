package com.app.expresstaxiconductor.models

import java.util.*

data class Servicio(
    val id:Long?,
    val fechaRegistro: Date?,
    val calificacion: Float?,
    val latitudInicial: Double,
    val longitudInicial: Double,
    val latitudFinal: Double,
    val longitudFinal: Double,
    var estado: Estado?,
    val cliente: Cliente?,
    val conductor: Conductor?
    )