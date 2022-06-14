package com.example.hosteleriapp.Bar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hosteleriapp.Objetos.Compartido
import com.example.hosteleriapp.Objetos.Pedido
import com.example.hosteleriapp.Objetos.Producto
import com.example.hosteleriapp.R
import com.example.hosteleriapp.Utiles.Firebase
import com.example.hosteleriapp.adaptadores.AdaptadorComandas
import com.example.hosteleriapp.adaptadores.AdaptadorMostrarComanda
import kotlinx.android.synthetic.main.activity_visualizar_comanda.*
import kotlinx.android.synthetic.main.fragment_gestionar_comandas.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class VisualizarComanda : AppCompatActivity() {
     var pedidos = ArrayList<Pedido>()
    lateinit var miAdapter:AdaptadorMostrarComanda
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizar_comanda)

        pedidos = Compartido.comanda.pedidos
        miAdapter = AdaptadorMostrarComanda(pedidos,this)
        rv_mostrar_comanda.setHasFixedSize(true)
        rv_mostrar_comanda.layoutManager = LinearLayoutManager(Compartido.appCompatActivity)
        rv_mostrar_comanda.adapter = miAdapter
    }
}