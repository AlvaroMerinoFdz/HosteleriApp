package com.example.hosteleriapp.Objetos

import android.content.Context
import androidx.appcompat.app.AppCompatActivity

object Compartido {
    lateinit var producto: Producto
    lateinit var usuario : Usuario
    public var usuarios: ArrayList<Usuario> = ArrayList()
    lateinit var context: Context
    lateinit var appCompatActivity: AppCompatActivity
    lateinit var establecimiento: Establecimiento
    var vectorComanda:ArrayList<Pedido> = ArrayList()
    var precio:Double = 0.0
    lateinit var comanda:Comanda
    val codigo_camara = 1888
    var codigo_galeria = 1887
    var codigo_file = 1
}