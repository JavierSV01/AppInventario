package com.example.gestioninventario.modelos

data class Producto(val nombre: String? = "",
                    var descripcion: String? = "",
                    var precio: Double? = 0.0,
                    var cantidad:Int? = 0,
                    var uri: String? = "",
                    var categoria: String? = "")