package com.example.hosteleriapp.Bar

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hosteleriapp.Objetos.Compartido
import com.example.hosteleriapp.Objetos.Producto
import com.example.hosteleriapp.R
import com.example.hosteleriapp.Utiles.Firebase
import com.example.hosteleriapp.adaptadores.AdaptadorProductos
import kotlinx.android.synthetic.main.fragment_gestionar_carta.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FragmentGestionarCarta : Fragment() {
    private var productos: ArrayList<Producto> = ArrayList()
    lateinit var miAdapter: AdaptadorProductos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runBlocking {
            val job: Job = launch(context = Dispatchers.Default) {
                productos =
                    Firebase.obtenerCarta(Compartido.usuario.correo)  //'Destripamos' la colección y la metemos en nuestro ArrayList
            }
            //Con este método el hilo principal de onCreate se espera a que la función acabe y devuelva la colección con los datos.
            job.join() //Esperamos a que el método acabe: https://dzone.com/articles/waiting-for-coroutines
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gestionar_carta, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        miAdapter = AdaptadorProductos(productos, Compartido.appCompatActivity)
        rv_productos.setHasFixedSize(true)
        rv_productos.layoutManager = LinearLayoutManager(Compartido.appCompatActivity)
        rv_productos.adapter = miAdapter

        btnAddProducto.setOnClickListener {
            var intentAddproducto = Intent(context, AddProductoActivity::class.java).apply { }
            startActivity(intentAddproducto)
        }
    }

    override fun onResume() {
        super.onResume()

        runBlocking {
            val job: Job = launch(context = Dispatchers.Default) {
                productos =
                    Firebase.obtenerCarta(Compartido.usuario.correo)  //'Destripamos' la colección y la metemos en nuestro ArrayList
            }
            //Con este método el hilo principal de onCreate se espera a que la función acabe y devuelva la colección con los datos.
            job.join() //Esperamos a que el método acabe: https://dzone.com/articles/waiting-for-coroutines
        }
        miAdapter = AdaptadorProductos(productos, Compartido.appCompatActivity)
        rv_productos.setHasFixedSize(true)
        rv_productos.layoutManager = LinearLayoutManager(Compartido.appCompatActivity)
        rv_productos.adapter = miAdapter
    }
}