package com.example.hosteleriapp.User

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hosteleriapp.Objetos.Compartido
import com.example.hosteleriapp.Objetos.Producto
import com.example.hosteleriapp.R
import com.example.hosteleriapp.Utiles.Firebase
import com.example.hosteleriapp.adaptadores.AdaptadorVerCarta
import kotlinx.android.synthetic.main.activity_ver_carta.*

class VerCartaActivity : AppCompatActivity() {

    private var productos: ArrayList<Producto> = ArrayList()
    lateinit var miAdapter: AdaptadorVerCarta
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_carta)

        productos = Firebase.obtenerCarta(Compartido.establecimiento.correo)

        var nombre = Compartido.establecimiento.nombre + " " + Compartido.establecimiento.apellidos

        txtNombreLocalVerCarta.setText(nombre)

        miAdapter = AdaptadorVerCarta(productos, this)
        rv_productos.setHasFixedSize(true)
        rv_productos.layoutManager = LinearLayoutManager(this)
        rv_productos.adapter = miAdapter
    }
}