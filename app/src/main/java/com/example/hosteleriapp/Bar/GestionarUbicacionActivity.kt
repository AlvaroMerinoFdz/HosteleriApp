package com.example.hosteleriapp.Bar

import android.Manifest
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.hosteleriapp.Objetos.Compartido
import com.example.hosteleriapp.Objetos.Establecimiento
import com.example.hosteleriapp.R
import com.example.hosteleriapp.User.EditarUsuarioActivity
import com.example.hosteleriapp.Utiles.Firebase
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class GestionarUbicacionActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMyLocationClickListener {

    private val LOCATION_REQUEST_CODE: Int = 0
    private lateinit var map: GoogleMap
    private var ubicacion: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gestionar_ubicacion)
        createMapFragment()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.ubicacion_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.opcSatelite -> {
                map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            };R.id.opcNormal -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
        };R.id.opcHibrido -> map.mapType = GoogleMap.MAP_TYPE_HYBRID
            R.id.opcTerreno -> map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            R.id.opcEditarUsuario -> {
                val editarIntent = Intent(this, EditarUsuarioActivity::class.java).apply {
                }
                startActivity(editarIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Asociamos el fragmento al mapa y lo invocamos para que se cargue de forma asíoncrona.
     * Cuando se cargue se lanzará el método: onMapReady
     */
    fun createMapFragment() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapAcceder1) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        runBlocking {
            val job: Job = launch(context = Dispatchers.Default) {
                enableMyLocation()
            }
            //Con este método el hilo principal de onCreate se espera a que la función acabe y devuelva la colección con los datos.
            job.join() //Esperamos a que el método acabe: https://dzone.com/articles/waiting-for-coroutines
        }
        map.setOnMyLocationClickListener(this)
        createMarker()
    }

    private fun createMarker() {
        runBlocking {
            val job: Job = launch(context = Dispatchers.Default) {
                val datos =
                    Firebase.getDataFromFireStore("establecimientos") as QuerySnapshot //Obtenermos la colección
                ubicacion = Firebase.getUbicacion(datos, Compartido.usuario.correo)
            }
            //Con este método el hilo principal de onCreate se espera a que la función acabe y devuelva la colección con los datos.
            job.join() //Esperamos a que el método acabe: https://dzone.com/articles/waiting-for-coroutines
        }

        if (ubicacion != null) {
            ubicacion?.let { MarkerOptions().position(it).title(Compartido.usuario.correo) }
                ?.let { map.addMarker(it) }
        } else {
            Toast.makeText(this, "Este local todavía no tiene ubicacion", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * función que usaremos a lo largo de nuestra app para comprobar si el permiso ha sido aceptado o no.
     */
    fun isPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED


    /**
     * función que primero compruebe si el mapa ha sido inicializado, si no es así saldrá de la función gracias
     * a la palabra return, si por el contrario map ya ha sido inicializada, es decir que el mapa ya ha cargado,
     * pues comprobaremos los permisos.
     */
    @SuppressLint("MissingPermission")
    fun enableMyLocation() {
        if (!::map.isInitialized) return
        if (isPermissionsGranted()) {
            map.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }
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
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
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
                map.isMyLocationEnabled = true
            } else {
                Toast.makeText(
                    this,
                    "Para activar la localización ve a ajustes y acepta los permisos",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {}
        }
    }

    override fun onMyLocationClick(p0: Location) {
        var establecimiento: Establecimiento? = null
        val builder = AlertDialog.Builder(this)
        builder.setMessage(R.string.guardar_ubicacion)
            .setPositiveButton(R.string.yes, DialogInterface.OnClickListener { dialog, which ->
                runBlocking {
                    val job: Job = launch(context = Dispatchers.Default) {
                        val datos =
                            Firebase.getDataFromFireStore("establecimientos") as QuerySnapshot //Obtenermos la colección
                        establecimiento =
                            Firebase.getEstablecimiento(datos, Compartido.usuario.correo)
                    }
                    //Con este método el hilo principal de onCreate se espera a que la función acabe y devuelva la colección con los datos.
                    job.join() //Esperamos a que el método acabe: https://dzone.com/articles/waiting-for-coroutines
                }
                establecimiento?.ubicacion = LatLng(p0.latitude, p0.longitude)
                Firebase.crearEstablecimiento(establecimiento!!)
            })
            .setNegativeButton(R.string.no, DialogInterface.OnClickListener { dialog, which ->

            })
        builder.create().show()

    }
}