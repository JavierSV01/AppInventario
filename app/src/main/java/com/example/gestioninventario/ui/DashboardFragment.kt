package com.example.gestioninventario.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gestioninventario.R
import com.example.gestioninventario.modelos.Categorias
import com.example.gestioninventario.modelos.Producto
import com.example.gestioninventario.reciclerViewProductos.RecyclerAdapter
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_dashboard.*


class DashboardFragment : Fragment() {

    lateinit var database:FirebaseDatabase
    lateinit var myRefProductos:DatabaseReference
    lateinit var postListenerProductos: ValueEventListener
    lateinit var postListenerCategorias: ValueEventListener
    var listaCategoriasString = mutableListOf<String>()
    var listaCategorias: MutableList<Categorias> = mutableListOf()
    lateinit var myRefCategoria: DatabaseReference

    var listaProductos: MutableList<Producto> = mutableListOf()
    var listaProductosAux: MutableList<Producto> = mutableListOf()
    lateinit var contexto:Context

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        contexto = this.requireContext()
        database = FirebaseDatabase.getInstance()
        myRefProductos = database.getReference("productos")
        myRefCategoria = database.getReference("categorias")

        cargarDatos()
        return root
    }

    fun cargarSpinner(){
        val spinnerAdapter = ArrayAdapter<String>(
            contexto,
            android.R.layout.simple_spinner_item,
            listaCategoriasString
        )
        spinnerFiltroCategoria.adapter = spinnerAdapter

        spinnerFiltroCategoria.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                var categoriaSeleccionada:String = spinnerAdapter.getItem(position).toString();

                listaProductosAux.clear()

                if (categoriaSeleccionada.equals("Todas")){
                    for (i in 0..listaProductos.size-1){
                        listaProductosAux.add(listaProductos.get(i))
                    }
                }else{
                    for (i in 0..listaProductos.size-1){
                        if (listaProductos.get(i).categoria.equals(categoriaSeleccionada)){
                            listaProductosAux.add(listaProductos.get(i))
                        }
                    }
                }
                lanzarRV(listaProductosAux)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // your code here
            }
        })


    }

    fun cargarDatosCategorias(){
        postListenerCategorias =  object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                listaCategorias.clear()
                listaCategoriasString.clear()
                dataSnapshot.children.mapNotNullTo(listaCategorias) { it.getValue<Categorias>(
                    Categorias::class.java) }
                listaCategoriasString.add("Todas")
                for(i in 0..listaCategorias.size - 1){
                    listaCategoriasString.add(listaCategorias.get(i).nombre.toString())
                }
                cargarSpinner()
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        }

        myRefCategoria.addValueEventListener(postListenerCategorias)
    }
    fun cargarDatos() {
        postListenerProductos = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                cargarDatosCategorias()
                listaProductos.clear()
                dataSnapshot.children.mapNotNullTo(listaProductos) { it.getValue<Producto>(
                    Producto::class.java) }
                lanzarRV(listaProductos)
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        }


        myRefProductos.addValueEventListener(postListenerProductos)

    }

    override fun onDetach() {
        super.onDetach()
        myRefProductos.removeEventListener(postListenerProductos)
        myRefCategoria.removeEventListener(postListenerCategorias)
    }

    fun lanzarRV(listaProductos:MutableList<Producto>) {

        recyclerViewProductos.layoutManager = LinearLayoutManager(contexto)
        recyclerViewProductos.adapter = RecyclerAdapter(contexto, listaProductos)

    }

}
