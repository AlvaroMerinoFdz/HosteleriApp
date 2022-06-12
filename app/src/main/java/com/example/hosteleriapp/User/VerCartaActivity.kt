package com.example.hosteleriapp.User

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hosteleriapp.Objetos.Compartido
import com.example.hosteleriapp.Objetos.Producto
import com.example.hosteleriapp.R
import com.example.hosteleriapp.Utiles.Firebase
import com.example.hosteleriapp.adaptadores.AdaptadorProductos
import com.example.hosteleriapp.adaptadores.AdaptadorVerCarta
import kotlinx.android.synthetic.main.activity_ver_carta.*
import kotlinx.android.synthetic.main.fragment_gestionar_carta.*

class VerCartaActivity : AppCompatActivity() {

    private var productos: ArrayList<Producto> = ArrayList()
    lateinit var miAdapter:AdaptadorVerCarta
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_carta)

        productos = Firebase.obtenerCarta("bar@gmail.com")

        miAdapter = AdaptadorVerCarta(productos, this)
        rv_ver_carta.setHasFixedSize(true)
        rv_ver_carta.layoutManager = LinearLayoutManager(this)
        rv_ver_carta.adapter = miAdapter
    }
}