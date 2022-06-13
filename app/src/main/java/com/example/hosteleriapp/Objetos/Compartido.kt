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
    var vectorComanda:ArrayList<String> = ArrayList()
    var precio:Double = 0.0
    lateinit var comanda:Comanda
}