package com.example.hosteleriapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.hosteleriapp.Utiles.LogIn
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Con esto lanzamos eventos personalizados a GoogleAnalytics que podemos ver en nuestra consola de FireBase.
        val analy: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message","Integración completada")
        analy.logEvent("InitScreen",bundle)

        //Iniciar Sesion
        btnLogIn.setOnClickListener(){
            if (etEmail.text.isNotEmpty() && etPwd.text.isNotEmpty()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(etEmail.text.toString(),etPwd.text.toString()).addOnCompleteListener {
                    if (it.isSuccessful){
                        //Aqui buscaremos en la base de datos por el email y,
                        // guardamos el valor de ese usuario en el objeto compartido.
                        runBlocking {
                            val job : Job = launch(context = Dispatchers.Default) {
                                val datos : QuerySnapshot = LogIn.getDataFromFireStore() as QuerySnapshot //Obtenermos la colección
                            }
                            //Con este método el hilo principal de onCreate se espera a que la función acabe y devuelva la colección con los datos.
                            job.join() //Esperamos a que el método acabe: https://dzone.com/articles/waiting-for-coroutines
                        }
                    } else {
                        showAlert()
                    }
                }
            }
        }
        //Registrar
        btnSignUp.setOnClickListener{
            var intentRegistrar = Intent(this,::class.java).apply {  }
            startActivity(intentRegistrar)
        }

    }
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}