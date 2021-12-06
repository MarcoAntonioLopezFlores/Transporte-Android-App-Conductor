package com.app.expresstaxiconductor.models

data class Vehiculo(
    val id: Long?,
    val placas: String,
    val color: String,
    val estado: Boolean,
    val modelo: String,
    val marca: String
)
