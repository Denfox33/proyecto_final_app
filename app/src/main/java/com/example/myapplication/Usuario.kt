package com.example.myapplication

data class Usuario(
    var id: String = "",
    var nombre: String = "",
    var email: String = "",
    var password: String = "",
    var admin: String = "0",
    var foto : String = ""
)