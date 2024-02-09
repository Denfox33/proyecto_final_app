package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.FragmentUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.CoroutineContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserFragment : Fragment(), CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main

    private lateinit var bind: FragmentUserBinding
    private var urlimg: Uri? = null
    private lateinit var db_ref :DatabaseReference

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        bind = FragmentUserBinding.inflate(layoutInflater)

        db_ref = FirebaseDatabase.getInstance().reference

        launch {
            var foto = db_ref.child("Tienda").child("Usuarios").child(FirebaseAuth.getInstance().uid.toString()).child("foto").get().await().value.toString()
            if (foto != "null") {
                Glide.with(requireContext())
                    .load(foto)
                    .apply(Utilidades.opcionesGlide(requireContext()))
                    .transition(Utilidades.transicion)
                    .into(bind.foto)
            }else{
                bind.foto.setImageResource(R.drawable.fotodef)
            }

            var user = Utilidades.cogerUsuario()
            bind.nombre.text = user!!.nombre

        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bind = FragmentUserBinding.inflate(inflater, container, false)
        db_ref = FirebaseDatabase.getInstance().reference

        // Inflate the layout for this fragment
        return bind.root
    }


    override fun onStart() {
        super.onStart()

        bind.foto.setOnClickListener {
            val popupMenu = PopupMenu(context, bind.foto)
            popupMenu.menuInflater.inflate(R.menu.menu_foto, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.editar_foto -> {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(intent, 0)

                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == -1 && data != null) {
            urlimg = data.data

            var urlimg_firebase: String
            launch {
                urlimg_firebase = Utilidades.guardarImagenUser(urlimg!!)
                db_ref.child("Tienda").child("Usuarios").child(FirebaseAuth.getInstance().uid.toString()).child("foto").setValue(urlimg_firebase)
            }
            bind.foto.setImageURI(urlimg)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}