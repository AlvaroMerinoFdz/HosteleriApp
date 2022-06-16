package com.example.hosteleriapp.Objetos

import java.io.Serializable

data class Producto(
    val correo: String,
    val nombre: String,
    var descripcion: String,
    var precio: Double,
    var imagen: String? = null
) : Serializable
