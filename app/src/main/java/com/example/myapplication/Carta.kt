package com.example.myapplication

data class Carta(
    var id: String? = null,
    var nombre: String? = null,
    var categoria: String? = null,
    var precio: String? = null,
    var disponible: Boolean = false,
    var imagen: String? = null
)