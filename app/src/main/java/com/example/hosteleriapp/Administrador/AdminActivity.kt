package com.example.hosteleriapp.Administrador

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hosteleriapp.Objetos.Usuario
import com.example.hosteleriapp.R
import com.example.hosteleriapp.Utiles.Firebase.obtenerUsuarios
import com.example.hosteleriapp.Utiles.LogIn.getDataFromFireStore
import com.example.hosteleriapp.adaptadores.AdaptadorUsuarios
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_admin.*
import kotlinx.android.synthetic.main.activity_usuario.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AdminActivity : AppCompatActivity() {

    private var usuarios:ArrayList<Usuario> = ArrayList()
    lateinit var miAdapter : AdaptadorUsuarios

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        runBlocking {
            val job: Job = launch(context = Dispatchers.Default) {
                val datos: QuerySnapshot =
                    getDataFromFireStore() as QuerySnapshot //Obtenermos la colección
                usuarios = obtenerUsuarios(datos as QuerySnapshot?)  //'Destripamos' la colección y la metemos en nuestro ArrayList
            }
            //Con este método el hilo principal de onCreate se espera a que la función acabe y devuelva la colección con los datos.
            job.join() //Esperamos a que el método acabe: https://dzone.com/articles/waiting-for-coroutines
        }

        miAdapter = AdaptadorUsuarios(usuarios,this)
        rvUsuarios.setHasFixedSize(true)
        rvUsuarios.layoutManager = LinearLayoutManager(this)
        rvUsuarios.adapter = miAdapter


    }

    override fun onResume() {
        super.onResume()
        usuarios.clear()
        runBlocking {
            val job: Job = launch(context = Dispatchers.Default) {
                val datos: QuerySnapshot =
                    getDataFromFireStore() as QuerySnapshot //Obtenermos la colección
                usuarios = obtenerUsuarios(datos as QuerySnapshot?)  //'Destripamos' la colección y la metemos en nuestro ArrayList
            }
            //Con este método el hilo principal de onCreate se espera a que la función acabe y devuelva la colección con los datos.
            job.join() //Esperamos a que el método acabe: https://dzone.com/articles/waiting-for-coroutines
        }

        miAdapter = AdaptadorUsuarios(usuarios,this)
        rvUsuarios.setHasFixedSize(true)
        rvUsuarios.layoutManager = LinearLayoutManager(this)
        rvUsuarios.adapter = miAdapter
    }
}