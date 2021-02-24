package com.example.gestioninventario.reciclerViewProductos

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.gestioninventario.modelos.Producto
import com.example.gestioninventario.ProductosEnDetalle
import com.example.gestioninventario.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.item_producto.view.*
import java.lang.IllegalArgumentException

class RecyclerAdapter(
    val context: Context,
    val listaProductos: MutableList<Producto>
) : RecyclerView.Adapter<BaseViewHolder<*>>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return ProductosViewHolder(LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false))
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        if (holder is ProductosViewHolder) {
            holder.bind(listaProductos[position], position)
        } else throw IllegalArgumentException("Error: viewHolder erroneo")
    }

    override fun getItemCount(): Int {
        return listaProductos.size
    }

    inner class ProductosViewHolder(itemView: View) : BaseViewHolder<Producto>(itemView) {
        override fun bind(item: Producto, position: Int) {
            Glide.with(context).load(Uri.parse(item.uri)).into(itemView.circleImageView)
            itemView.txNombreProducto.text = item.nombre
            itemView.txCantidad.text = item.cantidad.toString()
            itemView.txCategoria.text = item.categoria

            itemView.btEliminar.setOnClickListener(View.OnClickListener {

                val builder = AlertDialog.Builder(context)
                builder.setTitle("Atención")
                builder.setMessage("¿Está seguro de que desea borrar el producto?")

                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    var database:FirebaseDatabase = FirebaseDatabase.getInstance()
                    var myRef:DatabaseReference = database.getReference("productos").child(item.nombre.toString())
                    myRef.removeValue()
                }

                builder.setNegativeButton(android.R.string.no) { dialog, which ->

                }
                builder.show()


            })

            itemView.fila.setOnClickListener(View.OnClickListener {
                val i:Intent = Intent(context, ProductosEnDetalle::class.java)
                i.putExtra("nombre",  item.nombre)
                i.putExtra("cantidad", item.cantidad.toString())
                i.putExtra("precio", item.precio.toString())
                i.putExtra("descripcion", item.descripcion)
                i.putExtra("uriFoto", item.uri)
                i.putExtra("categoria", item.categoria)
                context.startActivity(i)

            })
        }
    }

}