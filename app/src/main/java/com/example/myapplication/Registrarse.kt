package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.myapplication.databinding.ActivityRegistrarseBinding
import com.google.firebase.auth.FirebaseAuth

class Registrarse : AppCompatActivity() {
    private lateinit var autentication: FirebaseAuth
    private var nombre: String? = null
    private var email: String? = null
    private var password: String? = null
    private lateinit var bind: ActivityRegistrarseBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityRegistrarseBinding.inflate(layoutInflater)
        setContentView(bind.root)

        autentication = FirebaseAuth.getInstance()

        bind.buttonRegistrarse.setOnClickListener {
            if (cogerEmail() != null && cogerPassword() != null && cogerNombre() != null) {
                autentication.createUserWithEmailAndPassword(email!!, password!!)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Registrado correctamente", Toast.LENGTH_SHORT)
                                .show()
                            var intent = Intent(this, MainActivity::class.java)
                            var usuario = Usuario(autentication.uid!!, nombre!!, email!!, password!!)
                            Utilidades.subirUsuario(usuario)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Error al registrarse", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }


    private fun cogerEmail(): String? {
        return if (bind.mail.text.toString().isNullOrBlank()) {
            bind.textInputLayoutEmail.error = "Email esta vacio"
            null
        } else if (!bind.mail.text.toString().contains("@")) {
            bind.textInputLayoutEmail.error = "Email no valido"
            null
        } else {
            bind.textInputLayoutEmail.error = null
            email = bind.mail.text.toString()
            email
        }
    }

    private fun cogerPassword(): String? {
        return if (bind.pass.text.toString().isNullOrBlank()) {
            bind.textInputLayoutPass.error = "Contraseña esta vacia"
            null
        } else {
            bind.textInputLayoutPass.error = null
            password = bind.pass.text.toString()
            password
        }

    }

    private fun cogerNombre(): String? {
        return if (bind.nombre.text.toString().isNullOrBlank()) {
            bind.textInputLayoutNombre.error = "Nombre esta vacio"
            null
        } else {
            bind.textInputLayoutNombre.error = null
            nombre = bind.nombre.text.toString()
            nombre
        }

    }
}