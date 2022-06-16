package com.example.hosteleriapp.Bar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hosteleriapp.Objetos.Comanda
import com.example.hosteleriapp.Objetos.Compartido
import com.example.hosteleriapp.R
import com.example.hosteleriapp.Utiles.Firebase
import com.example.hosteleriapp.adaptadores.AdaptadorComandas
import kotlinx.android.synthetic.main.fragment_gestionar_comandas.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FragmentGestionarComandas : Fragment() {
    private var comandas: ArrayList<Comanda> = ArrayList()
    lateinit var miAdapter: AdaptadorComandas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runBlocking {
            val job: Job = launch(context = Dispatchers.Default) {
                Log.e("Alvaro", Compartido.usuario.correo)
                comandas =
                    Firebase.obtenerComandas(Compartido.usuario.correo)  //'Destripamos' la colección y la metemos en nuestro ArrayList
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
        return inflater.inflate(R.layout.fragment_gestionar_comandas, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        miAdapter = AdaptadorComandas(comandas, Compartido.appCompatActivity)
        rv_gestionar_comandas.setHasFixedSize(true)
        rv_gestionar_comandas.layoutManager = LinearLayoutManager(Compartido.appCompatActivity)
        rv_gestionar_comandas.adapter = miAdapter
    }
}