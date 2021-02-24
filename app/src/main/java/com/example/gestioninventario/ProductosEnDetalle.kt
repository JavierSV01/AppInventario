package com.example.gestioninventario

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.gestioninventario.modelos.Categorias
import com.example.gestioninventario.modelos.Producto

import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_productos_en_detalle.*

class ProductosEnDetalle : AppCompatActivity() {

    lateinit var producto: Producto

    lateinit var firebase: FirebaseDatabase
    lateinit var myRefProducto: DatabaseReference
    lateinit var storage: FirebaseStorage
    lateinit var storageRef: StorageReference
    lateinit var myRefCategoria: DatabaseReference
    lateinit var categoria: Categorias
    var listaCategorias: MutableList<Categorias> = mutableListOf()
    lateinit var categoriaNombre: String

    companion object {
        const val SELECCIONAR_FOTO = 123
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        producto = Producto(intent.getStringExtra("nombre"),
            intent.getStringExtra("descripcion"),
            intent.getStringExtra("precio")?.toDouble(),
            intent.getStringExtra("cantidad")?.toInt(),
            intent.getStringExtra("uriFoto"),
            intent.getStringExtra("categoria").toString()
        )

        categoriaNombre = producto.categoria.toString()

        firebase = Firebase.database
        myRefProducto = firebase.getReference("productos").child(producto.nombre.toString())
        myRefCategoria = firebase.getReference("categorias")
        storage = FirebaseStorage.getInstance()
        storageRef = storage.getReference("imagenes")

        setContentView(R.layout.activity_productos_en_detalle)
    }

    fun cargarDatosCategoria(){
        myRefCategoria.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                dataSnapshot.children.mapNotNullTo(listaCategorias) {
                    it.getValue<Categorias>(
                        Categorias::class.java
                    )
                }

                for (i in 0..listaCategorias.size - 1){
                    if (listaCategorias.get(i).nombre.equals(categoriaNombre)){
                        categoria = listaCategorias.get(i)
                    }
                }

                txNombreProducto.setText(categoria.nombre);
                txIva.setText(categoria.iva)
                txDescripcionCategoria.setText(categoria.descripcion)

                Glide.with(applicationContext).load(Uri.parse(categoria.uri)).into(circleImageView)


            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    override fun onStart() {
        super.onStart()

        cargarDatosCategoria()

        txNombre.text = producto.nombre.toString()
        txCantidad.setText(producto.cantidad.toString())
        txCantidad.isEnabled = false
        txDescripcion.setText(producto.descripcion.toString())
        txDescripcion.isEnabled = false
        txPrecio.setText(producto.precio.toString())
        txPrecio.isEnabled = false

        Glide.with(this).load(Uri.parse(producto.uri)).into(circleImageView3)

        btModificar.setText("Modificar")

        btModificar.setOnClickListener(View.OnClickListener {

            if (btModificar.text.toString().equals("Modificar")) {

                txCantidad.isEnabled = true
                txDescripcion.isEnabled = true
                txPrecio.isEnabled = true

                btModificar.setText("Guardar cambios")

            } else {

                txCantidad.isEnabled = false
                txDescripcion.isEnabled = false
                txPrecio.isEnabled = false

                producto.cantidad = txCantidad.text.toString().toInt()
                producto.descripcion = txDescripcion.text.toString()
                producto.precio = txPrecio.text.toString().toDouble()

                myRefProducto.setValue(producto)

                btModificar.setText("Modificar")
                Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()

            }

        })

        btActualizarFoto.setOnClickListener(View.OnClickListener {
            val i = Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, SELECCIONAR_FOTO);
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECCIONAR_FOTO && resultCode == Activity.RESULT_OK) {
            val fotoUriAux = data!!.data
            val fotoRef: StorageReference =
                storageRef.child(fotoUriAux!!.lastPathSegment.toString())
            fotoRef.putFile(fotoUriAux).continueWithTask(object :
                Continuation<UploadTask.TaskSnapshot?, Task<Uri?>?> {
                @Throws(Exception::class)
                override fun then(task: Task<UploadTask.TaskSnapshot?>): Task<Uri?>? {
                    if (!task.isSuccessful()) {
                        throw task.getException()!!
                    }
                    return fotoRef.getDownloadUrl()
                }
            }).addOnCompleteListener(OnCompleteListener {
                if (it.isSuccessful) {
                    producto.uri = it.getResult().toString()
                    Glide.with(this).load(Uri.parse(producto.uri)).into(circleImageView3)
                    myRefProducto.setValue(producto)
                    Toast.makeText(this, "Foto actualizada", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

}