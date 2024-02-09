package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.FragmentCartasBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CartasFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CartasFragment : Fragment() {
    private lateinit var bind: FragmentCartasBinding
    private lateinit var lista: MutableList<Carta>
    private lateinit var db_ref: DatabaseReference
    private lateinit var recycler: RecyclerView
    private lateinit var adaptador: CartaAdaptador


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        bind = FragmentCartasBinding.inflate(layoutInflater)
        db_ref = FirebaseDatabase.getInstance().reference
        lista = mutableListOf()

        //se coge la lista de productos de la base de datos
        db_ref.child("Tienda").child("Cartas").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                snapshot.children.forEach { hijo: DataSnapshot? ->
                    val pojocarta = hijo!!.getValue(Carta::class.java)


                    var admin = Utilidades.cogerAdmin(requireContext())
                    if (admin == "0") {
                        if (pojocarta?.disponible == true) {
                            lista.add(pojocarta!!)
                        }
                    } else {
                        lista.add(pojocarta!!)
                    }
                }
                recycler.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })

        //se crea el adaptador y se le pasa la lista de productos
        adaptador = CartaAdaptador(lista)
        //se le pasa el adaptador al recycler
        recycler = bind.scCartas
        recycler.adapter = adaptador
        //se le pasa el layout manager
        recycler.layoutManager = LinearLayoutManager(applicationContext@ context)
        //se le dice que el tamaño del recycler no cambiara
        recycler.setHasFixedSize(true)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var admin = Utilidades.cogerAdmin(requireContext())
        if (admin == "0") {
            bind.btnAgregarCarta.visibility = View.GONE
        }else{
            bind.btnAgregarCarta.visibility = View.VISIBLE
        }


        // Inflate the layout for this fragment with binding
        return bind.root


    }

    override fun onStart() {
        super.onStart()
        bind.btnAgregarCarta.setOnClickListener {
            val fragment = AddCartaFragment()
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()

        }

        bind.lupa.setOnClickListener {
            mostrarBusqueda()
        }

        bind.close.setOnClickListener {
            ocultarBusqueda()
        }

        bind.buscarEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                null
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Este método se llama cuando el texto cambia
                val textoIngresado = s.toString()
                // Puedes hacer algo con el texto ingresado aquí
                // Por ejemplo, puedes imprimirlo en la consola
                adaptador.filter.filter((textoIngresado))
            }

            override fun afterTextChanged(p0: Editable?) {
                null
            }

        })

        bind.opciones.setOnClickListener {
            Utilidades.showPopupMenuOptions(it, requireContext())
        }

    }


    fun mostrarBusqueda() {
        bind.close.visibility = View.VISIBLE
        bind.lupa.visibility = View.INVISIBLE
        bind.buscarEt.visibility = View.VISIBLE
        bind.opciones.visibility = View.INVISIBLE
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(bind.buscarEt, InputMethodManager.SHOW_IMPLICIT)
    }

    fun ocultarBusqueda() {
        bind.close.visibility = View.INVISIBLE
        bind.lupa.visibility = View.VISIBLE
        bind.opciones.visibility = View.VISIBLE
        bind.buscarEt.visibility = View.INVISIBLE
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(bind.buscarEt.windowToken, 0)
        bind.buscarEt.setText("")
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CartasFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}