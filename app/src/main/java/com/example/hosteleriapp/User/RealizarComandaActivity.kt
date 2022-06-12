package com.example.hosteleriapp.User

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hosteleriapp.Objetos.Comanda
import com.example.hosteleriapp.Objetos.Compartido
import com.example.hosteleriapp.Objetos.Producto
import com.example.hosteleriapp.R
import com.example.hosteleriapp.Utiles.Firebase
import com.example.hosteleriapp.adaptadores.AdaptadorRealizarComanda
import kotlinx.android.synthetic.main.activity_realizar_comanda.*
import kotlinx.android.synthetic.main.activity_ver_carta.*

class RealizarComandaActivity : AppCompatActivity() {

    private var productos: ArrayList<Producto> = ArrayList()
    lateinit var miAdapter: AdaptadorRealizarComanda

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realizar_comanda)

        productos = Firebase.obtenerCarta(Compartido.establecimiento.correo)

        miAdapter = AdaptadorRealizarComanda(productos, this)
        rv_realizar_comanda.setHasFixedSize(true)
        rv_realizar_comanda.layoutManager = LinearLayoutManager(this)
        rv_realizar_comanda.adapter = miAdapter
        Compartido.vectorComanda.clear()

        btnRealizarPedido.setOnClickListener {
            if(txtNumeroMesa.text.toString()!=""){
                Log.e("Alvaro",Compartido.vectorComanda[0].producto.nombre)
                Log.e("Alvaro",Compartido.vectorComanda[0].cantidad.toString())
                var comanda = Comanda(Compartido.usuario.correo,txtNumeroMesa.text.toString().toInt(),Compartido.vectorComanda)
                Firebase.crearPedido(comanda)
            }else{
                Toast.makeText(this,"Debe de seleccionar un número de mesa", Toast.LENGTH_LONG).show()
            }
        }
    }
}