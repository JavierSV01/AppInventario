package com.example.gestioninventario.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.gestioninventario.CreacionCategoria
import com.example.gestioninventario.CreacionProductos
import com.example.gestioninventario.ProductosEnDetalle
import com.example.gestioninventario.modelos.Producto
import com.example.gestioninventario.R.layout.fragment_notifications
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_notifications.*

class NotificationsFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btCrearCategoria.setOnClickListener(View.OnClickListener {
            val i:Intent = Intent(context, CreacionCategoria::class.java)
            this.startActivity(i)

        })
        btCrearProducto.setOnClickListener(View.OnClickListener {
            val i:Intent = Intent(context, CreacionProductos::class.java)
            this.startActivity(i)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(fragment_notifications, container, false)
    }




}