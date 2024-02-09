package com.example.myapplication

import android.R
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.myapplication.databinding.FragmentAddEventoBinding
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


class AddEventoFragment : Fragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private lateinit var db_ref: DatabaseReference
    private lateinit var bind: FragmentAddEventoBinding
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

        bind = FragmentAddEventoBinding.inflate(layoutInflater)
        db_ref = FirebaseDatabase.getInstance().reference

        listaeventos = Utilidades.obtenerListaEventos()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
                val id_generado: String? = db_ref.child("Tienda").child("Eventos").push().key
                val nombre = bind.etNombre.text.toString()
                val precio = bind.etPrecio.text.toString()
                val fecha = bind.etFecha.text.toString()
                val aforomax = bind.etAforo.text.toString()
                launch {
                    val urlimgfirebase = Utilidades.guardarImagenEvento(id_generado!!, urlimg!!)
                    val nuevoevento = Evento(id_generado, nombre, fecha, precio.toDouble(), 0, aforomax.toInt(), urlimgfirebase)

                    Utilidades.subirEvento(nuevoevento)
                }

                Toast.makeText(requireContext(), "Evento creado con exito", Toast.LENGTH_SHORT).show()
                limpiar()
            }
        }

        bind.etFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate: String
                if (selectedMonth + 1 < 10){
                    selectedDate = "${selectedDay}/0${selectedMonth + 1}/$selectedYear"
                }else{
                    selectedDate = "${selectedDay}/${selectedMonth + 1}/$selectedYear"
                }
                bind.etFecha.setText(selectedDate)
            }, year, month, day)

            datePickerDialog.show()
        }
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

    private fun validar(): Boolean {
        var nombre = true
        var precio = true
        var foto = true
        var aforo = true
        var fecha = true
        var existe = true

        if (bind.etNombre.text.isNullOrBlank()){
            bind.tilNombre.error = "El evento debe tener un nombre"
            nombre = false
        }else{
            bind.tilNombre.error = null
            nombre = true
        }

        if (bind.etPrecio.text.isNullOrBlank() || bind.etPrecio.text.toString().toDouble() < 0){
            bind.tilPrecio.error = "El evento debe tener un precio"
            precio = false
        }else{
            bind.tilPrecio.error = null
            precio = true
        }

        if (bind.etAforo.text.isNullOrBlank() || bind.etAforo.text.toString().toInt() < 0) {
            bind.tilAforo.error = "El evento debe tener un aforo"
            aforo = false
        }else{
            bind.tilAforo.error = null
            aforo = true
        }

        if (bind.etFecha.text.isNullOrBlank()) {
            bind.tilFecha.error = "El evento debe tener una fecha"
            fecha = false
        }else{
            bind.tilFecha.error = null
            fecha = true
        }

        if (urlimg == null){
            Toast.makeText(requireContext(), "Selecciona una imagen", Toast.LENGTH_SHORT).show()
            foto = false
        }else{
            foto = true
        }

        if (Utilidades.existeEvento(listaeventos, bind.etNombre.text.toString().trim())) {
            Toast.makeText(requireContext(), "Ese evento ya existe", Toast.LENGTH_SHORT)
                .show()
            existe = false
        } else {
            existe = true
        }


        return nombre && precio && foto && aforo && fecha && existe

    }

    private fun limpiar(){
        bind.etNombre.text = null
        bind.etPrecio.text = null
        bind.etFecha.text = null
        bind.etAforo.text = null
        bind.img.setImageResource(R.drawable.ic_menu_camera)
    }



    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddEventoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}