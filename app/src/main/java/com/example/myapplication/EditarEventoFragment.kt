package com.example.myapplication

import android.R
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.FragmentEditarEventoBinding
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

class EditarEventoFragment : Fragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main
    lateinit var bind: FragmentEditarEventoBinding
    private lateinit var db_ref: DatabaseReference
    private var urlimg: Uri? = null
    private lateinit var listaeventos: MutableList<Evento>

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

        listaeventos = Utilidades.obtenerListaEventos()


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bind = FragmentEditarEventoBinding.inflate(layoutInflater)
        db_ref = FirebaseDatabase.getInstance().reference

        // Obtener los datos del Bundle
        val nombre = arguments?.getString("nombre")
        val fecha = arguments?.getString("fecha")
        val precio = arguments?.getString("precio")
        val imagen = arguments?.getString("imagen")
        val aforomax = arguments?.getString("aforomax")
        val id = arguments?.getString("id")

        bind.etNombre.setText(nombre)
        bind.etFecha.setText(fecha)
        bind.etPrecio.setText(precio)
        bind.etAforo.setText(aforomax)
        bind.img.setImageURI(imagen!!.toUri())

        Glide.with(requireContext())
            .load(imagen)
            .apply(Utilidades.opcionesGlide(requireContext()))
            .transition(Utilidades.transicion)
            .into(bind.img)


        // Inflate the layout for this fragment
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
            bind.img.setImageURI(selectedImage)
        }
    }

    override fun onStart() {
        super.onStart()

        bind.card.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 100)
        }

        bind.btnCrear.setOnClickListener {
            if (validar()) {
                var nombre = bind.etNombre.text.toString()
                var precio = bind.etPrecio.text.toString()
                var fecha = bind.etFecha.text.toString()
                var aforo = bind.etAforo.text.toString()
                var id_generado: String? = arguments?.getString("id")

                var url_foto_firebase: String
                launch {
                    if (urlimg == null) {
                        url_foto_firebase = arguments?.getString("imagen")!!
                    } else {
                        url_foto_firebase = Utilidades.guardarImagenCarta(id_generado!!, urlimg!!)
                    }

                    var nuevoevento = Evento(
                        id_generado,
                        nombre,
                        fecha,
                        precio.toDouble(),
                        0,
                        aforo.toInt(),
                        url_foto_firebase
                    )

                    Utilidades.subirEvento(nuevoevento)
                }

                Toast.makeText(requireContext(), "Evento editada con exito", Toast.LENGTH_SHORT)
                    .show()
                limpiar()
            }
        }

    }

    private fun validar(): Boolean {
        var nombre = true
        var precio = true
        var aforo = true
        var fecha = true
        var existe = true

        if (bind.etNombre.text.isNullOrBlank()) {
            bind.tilNombre.error = "El evento debe tener un nombre"
            nombre = false
        } else {
            bind.tilNombre.error = null
            nombre = true
        }

        if (bind.etPrecio.text.isNullOrBlank() || bind.etPrecio.text.toString().toDouble() < 0) {
            bind.tilPrecio.error = "El evento debe tener un precio"
            precio = false
        } else {
            bind.tilPrecio.error = null
            precio = true
        }

        if (bind.etAforo.text.isNullOrBlank() || bind.etAforo.text.toString().toInt() < 0) {
            bind.tilAforo.error = "El evento debe tener un aforo"
            aforo = false
        } else {
            bind.tilAforo.error = null
            aforo = true
        }

        if (bind.etFecha.text.isNullOrBlank()) {
            bind.tilFecha.error = "El evento debe tener una fecha"
            fecha = false
        } else {
            bind.tilFecha.error = null
            fecha = true
        }

        if (Utilidades.existeEvento(listaeventos, bind.etNombre.text.toString().trim())) {
            Toast.makeText(requireContext(), "Ese evento ya existe", Toast.LENGTH_SHORT)
                .show()
            existe = false
        } else {
            existe = true
        }



        return nombre && precio && aforo && fecha && existe

    }

    private fun limpiar() {
        bind.etNombre.text = null
        bind.etPrecio.text = null
        bind.etFecha.text = null
        bind.etAforo.text = null
        bind.img.setImageResource(R.drawable.ic_menu_camera)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditarEventoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditarEventoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}