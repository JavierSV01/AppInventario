package com.example.gestioninventario

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.gestioninventario.modelos.Categorias
import com.example.gestioninventario.modelos.Producto
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_creacion_producto.*



class CreacionProductos : AppCompatActivity() {

    lateinit var storage: FirebaseStorage
    lateinit var storageRef: StorageReference
    lateinit var database: FirebaseDatabase
    lateinit var myRefProductos: DatabaseReference
    lateinit var myRefCategoria: DatabaseReference
    lateinit var fotoUrl: String
    lateinit var postListener: ValueEventListener
    lateinit var spinnerAdapter:ArrayAdapter<String>

    var listaCategoriasString = mutableListOf<String>()
    var listaCategorias: MutableList<Categorias> = mutableListOf()

    companion object {
        const val SELECCIONAR_FOTO = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = FirebaseDatabase.getInstance()
        fotoUrl = "https://firebasestorage.googleapis.com/v0/b/gestioninventario-45504.appspot.com/o/default.jpg?alt=media&token=70a364ac-a0b7-499d-a14c-1c54f95b74b7"

        myRefProductos = database.getReference("productos")
        myRefCategoria = database.getReference("categorias")

        storage = FirebaseStorage.getInstance()
        storageRef = storage.getReference("imagenes")

        cargarDatos()

        setContentView(R.layout.activity_creacion_producto)
    }

    fun cargarDatos() {
        postListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                listaCategorias.clear()
                listaCategoriasString.clear()
                dataSnapshot.children.mapNotNullTo(listaCategorias) { it.getValue<Categorias>(
                    Categorias::class.java) }
                for(i in 0..listaCategorias.size - 1){
                    listaCategoriasString.add(listaCategorias.get(i).nombre.toString())
                }
                cargarSpinner()
            }
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        myRefCategoria.addValueEventListener(postListener)
    }

    override fun onStart() {
        super.onStart()
        Glide.with(this).load(Uri.parse(fotoUrl)).into(circleImageView2)
        subirDatos()
    }

    fun cargarSpinner(){
        spinnerAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            listaCategoriasString
        )
        spinerCategoria.adapter = spinnerAdapter
    }

    fun subirDatos() {
        btAñadir.setOnClickListener {

            if (!ProductoNombre.text.toString().equals("") && !ProductoPrecio.text.toString()
                    .equals("") && !ProductoCantidad.text.toString()
                    .equals("") && !ProductoDescripcion.text.toString().equals("")
                && !spinnerAdapter.isEmpty
            ) {
                val producto: Producto
                var posicionCategoriaSeleccionada:Int = -1
                for (i in 0..listaCategorias.size -1){
                    if (listaCategorias.get(i).nombre.equals(spinerCategoria.selectedItem.toString())){
                        posicionCategoriaSeleccionada = i
                    }
                }


                producto = Producto(
                    ProductoNombre.text.toString(),
                    ProductoDescripcion.text.toString(),
                    ProductoPrecio.text.toString().toDouble(),
                    ProductoCantidad.text.toString().toInt(),
                    fotoUrl,
                    listaCategorias.get(posicionCategoriaSeleccionada).nombre
                )

                myRefProductos.child(producto.nombre.toString()).setValue(producto)
                Toast.makeText(this, "Datos subidos correctamente", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Debes rellenar todos los campos", Toast.LENGTH_LONG).show()
            }

        }
        btCargarFoto.setOnClickListener(View.OnClickListener {

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
            Toast.makeText(
                this,
                "Espere a que carga la foto para salvar cambios",
                Toast.LENGTH_LONG
            ).show()
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
                    Toast.makeText(this, "Foto cargada correctamente", Toast.LENGTH_LONG).show()
                    fotoUrl = it.getResult().toString()
                    Glide.with(this).load(Uri.parse(fotoUrl)).into(circleImageView2)
                }
            })
        }
    }
}