package com.example.myapplication

data class Evento(
    var id: String? = null,
    var nombre: String? = null,
    var fecha: String? = null,
    var precio: Double? = null,
    var aforoactual: Int = 0,
    var aforomax: Int? = null,
    var imagen: String? = null
)