package com.example.hosteleriapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.hosteleriapp.Administrador.AdminActivity
import com.example.hosteleriapp.Bar.BarActivity
import com.example.hosteleriapp.Objetos.Compartido
import com.example.hosteleriapp.Objetos.Rol
import com.example.hosteleriapp.Objetos.Usuario
import com.example.hosteleriapp.User.UsuarioActivity
import com.example.hosteleriapp.Utiles.BiometricAuthCallback
import com.example.hosteleriapp.Utiles.BiometricUtilities
import com.example.hosteleriapp.Utiles.Firebase
import com.example.hosteleriapp.Utiles.Firebase.crearUsuario
import com.example.hosteleriapp.Utiles.LogIn
import com.example.hosteleriapp.Utiles.LogIn.showAlert
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity(), BiometricAuthCallback {

    private var continuar: Boolean = true
    var RC_SIGN_IN = 1
    private val LOCATION_REQUEST_CODE: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Compartido.context = this

        runBlocking {
            val job: Job = launch(context = Dispatchers.Default) {
                enableMyLocation()
            }
            //Con este método el hilo principal de onCreate se espera a que la función acabe y devuelva la colección con los datos.
            job.join() //Esperamos a que el método acabe: https://dzone.com/articles/waiting-for-coroutines
        }

        //Con esto lanzamos eventos personalizados a GoogleAnalytics que podemos ver en nuestra consola de FireBase.
        val analy: FirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("message", "Integración completada")
        analy.logEvent("InitScreen", bundle)

        //Iniciar Sesion
        btnLogIn.setOnClickListener() {
            if (etEmail.text.isNotEmpty() && etPwd.text.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(etEmail.text.toString(), etPwd.text.toString())
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            //Aqui buscaremos en la base de datos por el email y,
                            // guardamos el valor de ese usuario en el objeto compartido.
                            runBlocking {
                                val job: Job = launch(context = Dispatchers.Default) {
                                    val datos: QuerySnapshot =
                                        LogIn.getDataFromFireStore() as QuerySnapshot //Obtenermos la colección
                                    Firebase.obtenerUsuario(
                                        datos as QuerySnapshot?,
                                        etEmail.text.toString()
                                    )  //'Destripamos' la colección y la metemos en nuestro ArrayList
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
        btnSignUp.setOnClickListener {
            var intentRegistrar = Intent(this, RegistrarActivity::class.java).apply { }
            startActivity(intentRegistrar)
        }

        //Login con google
        btnLogInGoogle.setOnClickListener {
            //Configuración
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.request_id_token)) //Esto se encuentra en el archivo google-services.json: client->oauth_client -> client_id
                .requestEmail()
                .build()

            val googleClient = GoogleSignIn.getClient(
                this,
                googleConf
            ) //Este será el cliente de autenticación de Google.
            googleClient.signOut() //Con esto salimos de la posible cuenta  de Google que se encuentre logueada.
            val signInIntent = googleClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        session()
    }

    override fun onResume() {
        super.onResume()
        Compartido.context = this
    }

    private fun irHome() {
        //Aquí tengo que implementar algo para que hasta que no se haga la autenticación el hilo se quede pinchado
        /* checkBiometricCapability()
         showBiometricPrompt()*/

        /*
        runBlocking {
            val job : Job = launch(context = Dispatchers.Default){
                checkBiometricCapability()
                showBiometricPrompt()
            }
            job.join()
        }
         */

        //Si continuar = true, iniciará una ventana según su Rol, sino mostrará un mensaje por pantalla
        if (continuar) {
            if (Compartido.usuario.rol == Rol.ADMIN) {
                val homeIntent = Intent(this, AdminActivity::class.java).apply {
                }
                startActivity(homeIntent)
            } else if (Compartido.usuario.rol == Rol.USUARIO) {
                val homeIntent = Intent(this, UsuarioActivity::class.java).apply {
                }
                startActivity(homeIntent)
            } else {
                val homeIntent = Intent(this, BarActivity::class.java).apply {
                }
                startActivity(homeIntent)
            }
        } else {
            Toast.makeText(this, getString(R.string.huella_no_reconocida), Toast.LENGTH_LONG).show()
        }
    }

    //Métodos necesarios para la biometría.
    private fun checkBiometricCapability() {
        if (!BiometricUtilities.isDeviceReady(this)) {
            Toast.makeText(this, getString(R.string.biometria_no_disponible), Toast.LENGTH_SHORT)
                .show()
            continuar = true
        } else {
            Toast.makeText(this, getString(R.string.biometria_disponible), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun showBiometricPrompt() {
        BiometricUtilities.showPrompt(activity = this, callback = this)
    }

    //Métodos implementados de la interfaz de la Biometría
    override fun onSuccess() {
        Toast.makeText(this, getString(R.string.autenticacion_correcta), Toast.LENGTH_LONG).show()
        continuar = true
    }

    override fun onError() {
        continuar = false
    }

    override fun onNotRecognized() {
        continuar = true
    }

    //******************************** Para la sesión ***************************
    private fun session() {
        val prefs: SharedPreferences = getSharedPreferences(
            getString(R.string.prefs_file),
            Context.MODE_PRIVATE
        ) //Aquí no invocamos al edit, es solo para comprobar si tenemos datos en sesión.
        val email: String? = prefs.getString("email", null)
        val provider: String? = prefs.getString("provider", null)
        if (email != null) {
            irHome()
        }
    }

    //*************************************************************************
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Si la respuesta de esta activity se corresponde con la inicializada es que viene de la autenticación de Google.
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("Alvaro", "firebaseAuthWithGoogle:" + account.id)
                //Ya tenemos la id de la cuenta. Ahora nos autenticamos con FireBase.
                if (account != null) {
                    val credential: AuthCredential =
                        GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                //Añadimos el usuario a la BBDD
                                Compartido.usuario =
                                    Usuario(account.email.toString(), "contraseña", Rol.USUARIO)
                                crearUsuario(Compartido.usuario)
                                irHome()
                            } else {
                                showAlert(this)
                            }
                        }
                }
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("Alvaro", "Google sign in failed", e)
                showAlert(this)
            }
        }
    }//fin ActivityResult
    /**
     * función que primero compruebe si el mapa ha sido inicializado, si no es así saldrá de la función gracias
     * a la palabra return, si por el contrario map ya ha sido inicializada, es decir que el mapa ya ha cargado,
     * pues comprobaremos los permisos.
     */
    @SuppressLint("MissingPermission")
    fun enableMyLocation() {
            requestLocationPermission()
    }

    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    /**
     * Método que solicita los permisos.
     */
    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(this, R.string.ajustes_permisos, Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE
            )
        }
    }

    @SuppressLint("MissingSuperCall", "MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(
                    this,
                    R.string.ajustes_permisos,
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {}
        }
    }
}