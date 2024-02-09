package com.example.myapplication

import android.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.myapplication.databinding.FragmentAddCartaBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class AddCartaFragment: Fragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
    private lateinit var db_ref: DatabaseReference
    private lateinit var bind: FragmentAddCartaBinding
    private var urlimg: Uri? = null
    private lateinit var listacartas: MutableList<Carta>

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        bind = FragmentAddCartaBinding.inflate(layoutInflater)

        db_ref = FirebaseDatabase.getInstance().reference

        listacartas = Utilidades.obtenerListaCartas(db_ref)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //infla el spinner con las categorias
        val categorias = arrayOf("Selecciona categoria", "Blanco", "Negro", "Azul", "Verde")
        val adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, categorias)
        bind.spCat.adapter = adapter


        // Inflate the layout for this fragment
        return bind.root
    }

    override fun onStart() {
        super.onStart()

        bind.card.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }

        bind.btnCrear.setOnClickListener {
            if (validar()){
                var id_generado: String? = db_ref.child("Tienda").child("Cartas").push().key
                var nombre = bind.etNombre.text.toString()
                var precio = bind.etPrecio.text.toString()
                var categoria = bind.spCat.selectedItem.toString()
                var disponible = bind.chkDisponible.isChecked
                launch {
                    var urlimgfirebase = Utilidades.guardarImagenCarta(id_generado!!, urlimg!!)
                    var nuevacarta = Carta(id_generado, nombre, categoria, precio,disponible, urlimgfirebase)

                    Utilidades.subirCarta(nuevacarta)
                }

                Toast.makeText(requireContext(), "Carta guardada con exito", Toast.LENGTH_SHORT).show()
                limpiar()
            }
        }



    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage = data.data
            // Aquí puedes usar la URI de la imagen seleccionada
            urlimg = selectedImage
            // Por ejemplo, puedes establecerla en un ImageView
            bind.img.setImageURI(selectedImage)
        }
    }

    private fun validar():Boolean{
        var nombre = true
        var precio = true
        var categoria = true
        var foto = true
        var extiste = true

        if (bind.etNombre.text.isNullOrBlank()){
            bind.tilNombre.error = "La carta debe tener un nombre"
            nombre = false
        }else{
            bind.tilNombre.error = null
            nombre = true
        }

        if (bind.etPrecio.text.isNullOrBlank() || bind.etPrecio.text.toString().toDouble() < 0){
            bind.tilPrecio.error = "La carta debe tener un precio"
            precio = false
        }else{
            bind.tilPrecio.error = null
            precio = true
        }

        if (bind.spCat.selectedItemPosition == 0){
            Toast.makeText(requireContext(), "Selecciona una categoria", Toast.LENGTH_SHORT).show()
            categoria = false
        }else{
            categoria = true
        }

        if (urlimg == null){
            Toast.makeText(requireContext(), "Selecciona una imagen", Toast.LENGTH_SHORT).show()
            foto = false
        }else{
            foto = true
        }

        if (Utilidades.existeCarta(listacartas, bind.etNombre.text.toString().trim())) {
            Toast.makeText(requireContext(), "Esa carta ya existe", Toast.LENGTH_SHORT)
                .show()
            extiste = false
        } else {
            extiste = true

        }


        return nombre && precio && categoria &&  foto && extiste

    }


    fun limpiar(){
        bind.etNombre.text = null
        bind.etPrecio.text = null
        bind.spCat.setSelection(0)
        bind.chkDisponible.isChecked = false
        bind.img.setImageResource(R.drawable.ic_menu_camera)
    }



    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddCartaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}