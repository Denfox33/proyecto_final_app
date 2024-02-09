package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.FragmentEventosBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EventosFragment : Fragment() {
    private lateinit var bind: FragmentEventosBinding
    private lateinit var lista: MutableList<Evento>
    private lateinit var db_ref: DatabaseReference
    private lateinit var recycler: RecyclerView
    private lateinit var adaptador: EventoAdaptador

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        bind = FragmentEventosBinding.inflate(layoutInflater)
        db_ref = FirebaseDatabase.getInstance().reference
        lista = mutableListOf()

        //se coge la lista de productos de la base de datos
        db_ref.child("Tienda").child("Eventos").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                lista.clear()
                snapshot.children.forEach { hijo: DataSnapshot? ->
                    val pojoevento = hijo!!.getValue(Evento::class.java)

                    var sp = PreferenceManager.getDefaultSharedPreferences(context)
                    var admin = sp.getString("admin", "0")
                    if (admin == "0") {
                        if (pojoevento?.aforoactual!! < pojoevento?.aforomax!!){
                            lista.add(pojoevento!!)
                        }
                    }else{
                        lista.add(pojoevento!!)
                    }


                }
                recycler.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println(error.message)
            }
        })


        //se crea el adaptador y se le pasa la lista de productos
        adaptador = EventoAdaptador(lista)
        //se le pasa el adaptador al recycler
        recycler = bind.scEventos
        recycler.adapter = adaptador
        //se le pasa el layout manager
        recycler.layoutManager = LinearLayoutManager(applicationContext@ context)
        //se le dice que el tamaño del recycler no cambiara
        recycler.setHasFixedSize(true)




    }

    override fun onStart() {
        super.onStart()
        bind.btnAgregarEvento.setOnClickListener {
            val fragment = AddEventoFragment()
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
        bind.buscarEt.visibility = View.INVISIBLE
        bind.opciones.visibility = View.VISIBLE
        val inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(bind.buscarEt.windowToken, 0)
        bind.buscarEt.setText("")
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var admin = Utilidades.cogerAdmin(requireContext())
        if (admin == "0") {
            bind.btnAgregarEvento.visibility = View.GONE
        }else{
            bind.btnAgregarEvento.visibility = View.VISIBLE
        }

        // Inflate the layout for this fragment
        return bind.root
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EventosFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}