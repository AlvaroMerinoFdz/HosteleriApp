package com.example.hosteleriapp.Bar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hosteleriapp.Objetos.Compartido
import com.example.hosteleriapp.Objetos.Pedido
import com.example.hosteleriapp.R
import com.example.hosteleriapp.adaptadores.AdaptadorMostrarComanda
import kotlinx.android.synthetic.main.activity_visualizar_comanda.*

class VisualizarComanda : AppCompatActivity() {
    var pedidos = ArrayList<Pedido>()
    lateinit var miAdapter: AdaptadorMostrarComanda
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizar_comanda)

        pedidos = Compartido.comanda.pedidos
        miAdapter = AdaptadorMostrarComanda(pedidos, this)
        rv_mostrar_comanda.setHasFixedSize(true)
        rv_mostrar_comanda.layoutManager = LinearLayoutManager(Compartido.appCompatActivity)
        rv_mostrar_comanda.adapter = miAdapter
    }
}