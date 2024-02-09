package com.example.myapplication

import android.R
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.FragmentEditarCartaBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class EditarCartaFragment : Fragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
    lateinit var bind: FragmentEditarCartaBinding
    private lateinit var db_ref: DatabaseReference
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

        db_ref = FirebaseDatabase.getInstance().reference
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bind = FragmentEditarCartaBinding.inflate(layoutInflater)

        db_ref = FirebaseDatabase.getInstance().reference

        listacartas = Utilidades.obtenerListaCartas(db_ref)

        // Obtener los datos del Bundle
        val nombre = arguments?.getString("nombre")
        val precio = arguments?.getString("precio")
        val categoria = arguments?.getString("categoria")
        val imagen = arguments?.getString("imagen")
        val disponible = arguments?.getBoolean("disponible")


        val categorias = arrayOf("Selecciona categoria", "Blanco", "Negro", "Azul", "Verde")
        val adapter =
            ArrayAdapter(requireContext(), R.layout.simple_spinner_dropdown_item, categorias)
        bind.spCat.adapter = adapter

        // Encontrar el índice de la categoría en el array de categorías
        val index = categorias.indexOf(categoria)

        // Establecer la categoría como la opción seleccionada en el Spinner
        bind.spCat.setSelection(index)


        // Usar los datos obtenidos...
        bind.etNombre.setText(nombre)
        bind.etPrecio.setText(precio)
        bind.chkDisponible.isChecked = disponible!!
        bind.imgEditar.setImageURI(imagen!!.toUri())

        Glide.with(requireContext())
            .load(imagen)
            .apply(Utilidades.opcionesGlide(requireContext()))
            .transition(Utilidades.transicion)
            .into(bind.imgEditar)


        return bind.root
    }

    //Coge la foto seleccionada y la pone en el ImageView
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage = data.data
            // Aquí puedes usar la URI de la imagen seleccionada
            urlimg = selectedImage
            // Por ejemplo, puedes establecerla en un ImageView
            bind.imgEditar.setImageURI(selectedImage)
        }
    }

    override fun onStart() {
        super.onStart()

        bind.card.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }

        bind.btnCrear.setOnClickListener {
            if (validar()){
                var nombre = bind.etNombre.text.toString()
                var precio = bind.etPrecio.text.toString()
                var categoria = bind.spCat.selectedItem.toString()
                var disponible = bind.chkDisponible.isChecked
                var id_generado: String? = arguments?.getString("id")

                var url_foto_firebase : String
                launch {
                    if (urlimg == null){
                        url_foto_firebase = arguments?.getString("imagen")!!
                    }else{
                        url_foto_firebase = Utilidades.guardarImagenCarta(id_generado!!, urlimg!!)
                    }

                    var nuevacarta = Carta(id_generado, nombre, categoria, precio,disponible, url_foto_firebase)

                    Utilidades.subirCarta(nuevacarta)
                }

                Toast.makeText(requireContext(), "Carta editada con exito", Toast.LENGTH_SHORT).show()
                limpiar()
            }
        }

    }

    private fun validar():Boolean{
        var nombre = true
        var precio = true
        var categoria = true
        var bexiste = true

        Log.d("lista", listacartas.toString())

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

        if(Utilidades.existeCarta(listacartas, bind.etNombre.text.toString().trim())) {
            Toast.makeText(requireContext(), "Esa carta ya existe", Toast.LENGTH_SHORT)
                .show()
            bexiste = false
        }else{
            bexiste = true
        }


        return nombre && precio && categoria && bexiste

    }


    fun limpiar(){
        bind.etNombre.text = null
        bind.etPrecio.text = null
        bind.spCat.setSelection(0)
        bind.chkDisponible.isChecked = false
        bind.imgEditar.setImageResource(R.drawable.ic_menu_camera)
    }


    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditarCartaFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}