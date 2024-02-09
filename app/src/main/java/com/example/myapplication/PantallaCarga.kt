package com.example.myapplication


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PantallaCarga : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_carga)
        //quitar menu superior
        supportActionBar?.hide()

        var sp = PreferenceManager.getDefaultSharedPreferences(this)

        var db_ref = FirebaseDatabase.getInstance().reference
        var user:Usuario? = null

        CoroutineScope(Dispatchers.IO).launch {
            user = Utilidades.cogerUsuario()
            sp.edit().apply(){
                putString("nombre_user", user?.nombre)
                putString("email_user", user?.email)
                putString("admin_user", user?.admin)
                putString("id_user", user?.id)
                putString("admin", user?.admin)
                apply()
            }

            withContext(Dispatchers.Main){
                if (user?.admin == "1") {
                    var intent = Intent(this@PantallaCarga, Administrador::class.java)
                    startActivity(intent)
                }else if (user?.admin == "0"){
                    var intent = Intent(this@PantallaCarga, Cliente::class.java)
                    startActivity(intent)
                }
            }


        }



    }
}