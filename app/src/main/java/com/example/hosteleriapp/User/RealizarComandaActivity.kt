package com.example.hosteleriapp.User

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hosteleriapp.Objetos.Comanda
import com.example.hosteleriapp.Objetos.Compartido
import com.example.hosteleriapp.Objetos.Producto
import com.example.hosteleriapp.R
import com.example.hosteleriapp.Utiles.Firebase
import com.example.hosteleriapp.adaptadores.AdaptadorRealizarComanda
import kotlinx.android.synthetic.main.activity_realizar_comanda.*

class RealizarComandaActivity : AppCompatActivity() {

    private var productos: ArrayList<Producto> = ArrayList()
    lateinit var miAdapter: AdaptadorRealizarComanda

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_realizar_comanda)

        productos = Firebase.obtenerCarta(Compartido.establecimiento.correo)

        var nombre = Compartido.establecimiento.nombre + " " +  Compartido.establecimiento.apellidos

        txtNombreLocalRealizarComanda.setText(nombre)

        miAdapter = AdaptadorRealizarComanda(productos, this)
        rv_realizar_comanda.setHasFixedSize(true)
        rv_realizar_comanda.layoutManager = LinearLayoutManager(this)
        rv_realizar_comanda.adapter = miAdapter
        Compartido.vectorComanda.clear()

        btnRealizarPedido.setOnClickListener {
            if (txtNumeroMesa.text.toString() != "" && Compartido.precio > 0) {
                var comanda = Comanda(
                    Compartido.usuario.correo,
                    txtNumeroMesa.text.toString().toInt(),
                    Compartido.vectorComanda,
                    Compartido.establecimiento.correo,
                    Compartido.precio
                )
                Firebase.crearPedido(comanda)
                Compartido.precio = 0.0
                onBackPressed()
            } else {
                Toast.makeText(
                    this,
                    R.string.comprobar_datos,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}