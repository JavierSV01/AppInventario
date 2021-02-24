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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_creacion_categoria.*

class CreacionCategoria : AppCompatActivity() {

    lateinit var storage: FirebaseStorage
    lateinit var storageRef: StorageReference
    lateinit var database: FirebaseDatabase
    lateinit var myRef: DatabaseReference
    lateinit var fotoUrl: String

    companion object {
        const val SELECCIONAR_FOTO = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = FirebaseDatabase.getInstance()
        fotoUrl =
            "https://firebasestorage.googleapis.com/v0/b/gestioninventario-45504.appspot.com/o/default.jpg?alt=media&token=70a364ac-a0b7-499d-a14c-1c54f95b74b7"
        try {
            myRef = database.getReference("categorias")
        } catch (e: Exception) {
            println(e.message)
        }
        storage = FirebaseStorage.getInstance()
        storageRef = storage.getReference("imagenes")

        setContentView(R.layout.activity_creacion_categoria)
    }

    override fun onStart() {
        super.onStart()
        Glide.with(this).load(Uri.parse(fotoUrl)).into(circleImageView2)
        subirDatos()
        btCargarFoto.setOnClickListener(View.OnClickListener {

            val i = Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, SELECCIONAR_FOTO);

        })
    }

    fun subirDatos() {
        btAñadir.setOnClickListener {

            if (!ProductoNombre.text.toString().equals("") && !txIva.text.toString()
                    .equals("") && !ProductoDescripcion.text.toString().equals("")
            ) {
                val categoria: Categorias

                categoria = Categorias(
                    ProductoNombre.text.toString(),
                    ProductoDescripcion.text.toString(),
                    txIva.text.toString(),
                    fotoUrl
                )

                myRef.child(categoria.nombre.toString()).setValue(categoria)
                Toast.makeText(this, "Datos subidos correctamente", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Debes rellenar todos los campos", Toast.LENGTH_LONG).show()
            }

        }

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