package com.example.hosteleriapp.Objetos

import android.content.Context

object Compartido {
    lateinit var usuario : Usuario
    public var usuarios: ArrayList<Usuario> = ArrayList()
    lateinit var context: Context
}