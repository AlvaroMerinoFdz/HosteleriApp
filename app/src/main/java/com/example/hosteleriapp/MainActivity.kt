package com.example.hosteleriapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.hosteleriapp.Objetos.Compartido
import com.example.hosteleriapp.Objetos.Rol
import com.example.hosteleriapp.Utiles.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity(), BiometricAuthCallback {

    private var continuar:Boolean = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Compartido.context = this

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
                                Firebase.obtenerUsuario(datos as QuerySnapshot?, etEmail.text.toString())  //'Destripamos' la colección y la metemos en nuestro ArrayList
                            }
                            //Con este método el hilo principal de onCreate se espera a que la función acabe y devuelva la colección con los datos.
                            job.join() //Esperamos a que el método acabe: https://dzone.com/articles/waiting-for-coroutines
                        }
                        irHome()
                    } else {
                        LogIn.showAlert(this)
                    }
                }
            }
        }
        //Registrar
        btnSignUp.setOnClickListener{
            var intentRegistrar = Intent(this,RegistrarActivity::class.java).apply {  }
            startActivity(intentRegistrar)
        }
    }

    override fun onResume() {
        super.onResume()
        Compartido.context = this
    }

    private fun irHome(){
        checkBiometricCapability()
        showBiometricPrompt()

        if(continuar){
            if(Compartido.usuario.rol == Rol.ADMIN){
                /*val homeIntent = Intent(this, AdminActivity::class.java).apply {
                }
                startActivity(homeIntent)*/
            }else if (Compartido.usuario.rol == Rol.USUARIO){
                /* val homeIntent = Intent(this, UsuariosActivity::class.java).apply {
                 }
                 startActivity(homeIntent)*/
            }else{
                /* val homeIntent = Intent(this, BarActivity::class.java).apply {
                 }
                 startActivity(homeIntent)*/
            }
        }else{
            Toast.makeText(this,getString(R.string.huella_no_reconocida), Toast.LENGTH_LONG).show()
        }
    }

    //Métodos necesarios para la biometría.
    private fun checkBiometricCapability(){
        if(!BiometricUtilities.isDeviceReady(this)){
            Toast.makeText(this,getString(R.string.biometria_no_disponible), Toast.LENGTH_SHORT).show()
            continuar = true
        }else{
            Toast.makeText(this,getString(R.string.biometria_disponible), Toast.LENGTH_SHORT).show()
        }
    }

    private  fun showBiometricPrompt(){
        BiometricUtilities.showPrompt(activity = this, callback = this)
    }

    //Métodos implementados de la interfaz de la Biometría
    override fun onSuccess() {
        Toast.makeText(this,getString(R.string.autenticacion_correcta), Toast.LENGTH_LONG).show()
        continuar = true
    }

    override fun onError() {
        continuar = false
    }

    override fun onNotRecognized() {
        continuar = true
    }
}