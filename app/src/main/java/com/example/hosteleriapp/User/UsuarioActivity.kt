package com.example.hosteleriapp.User

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.hosteleriapp.Objetos.Compartido
import com.example.hosteleriapp.Objetos.Establecimiento
import com.example.hosteleriapp.R
import com.example.hosteleriapp.Utiles.Firebase
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UsuarioActivity : AppCompatActivity(), OnMapReadyCallback,
    GoogleMap.OnMyLocationClickListener, GoogleMap.OnMarkerClickListener {
    private val LOCATION_REQUEST_CODE: Int = 0
    private lateinit var map: GoogleMap
    private var ubicacion: Location? = null
    private var establecimientos: ArrayList<Establecimiento> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario)
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

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableMyLocation() //--> Habilita, pidiendo permisos, la localización actual.
        runBlocking {
            val job: Job = launch(context = Dispatchers.Default) {
                localizarMiUbicacion()
            }
            job.join()
        }

        map.setOnMyLocationClickListener(this)
        map.setOnMarkerClickListener(this)
        createMarker() //--> Nos coloca varios marcadores en el mapa y nos coloca en el CIFP Virgen de Gracia con un Zoom.
    }

    /**
     * Nos dice cuál es nuestra ubicación actual, lo usaremos principalmente para pintar el círculo alrededor.
     */
    @SuppressLint("MissingPermission")
    private fun localizarMiUbicacion() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        ubicacion = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    }

    /**
     * Método en el que crearemos algunos marcadores de ejemplo.
     */
    private fun createMarker() {
        //map.addMarker(MarkerOptions().position(markerCIFP).title("Mi CIFP favorito!"))
        establecimientos.clear()
        runBlocking {
            val job: Job = launch(context = Dispatchers.Default) {
                val datos =
                    Firebase.getDataFromFireStore("establecimientos") as QuerySnapshot //Obtenermos la colección
                establecimientos =
                    Firebase.getEstablecimientos(datos as QuerySnapshot?)  //'Destripamos' la colección y la metemos en nuestro ArrayList
            }
            //Con este método el hilo principal de onCreate se espera a que la función acabe y devuelva la colección con los datos.
            job.join() //Esperamos a que el método acabe: https://dzone.com/articles/waiting-for-coroutines
        }
        for (establecimiento in establecimientos) {
            establecimiento.ubicacion?.let {
                MarkerOptions().position(it)
                    .title(establecimiento.nombre + " " + establecimiento.apellidos)
            }
                ?.let { map.addMarker(it) }

            establecimiento.ubicacion?.let { pintarCirculoCentro(it) }
        }
    }


    /**
     * Asociamos el fragmento al mapa y lo invocamos para que se cargue de forma asíoncrona.
     * Cuando se cargue se lanzará el método: onMapReady
     */
    fun createMapFragment() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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

    /**
     * Se dispara cuando pulsamos en nuestra localización exacta donde estámos ahora (punto azul).
     */
    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this, "Estás en ${p0.latitude}, ${p0.longitude}", Toast.LENGTH_SHORT).show()

        //Firebase.crearEstablecimiento(Establecimiento("santi@gmail.com","12345678","bar","santi",LatLng(p0.latitude+0.0005,p0.longitude+0.0005)))
    }


    private fun pintarCirculoCentro(miUbicacion: LatLng) {
        map.addCircle(CircleOptions().run {
            center(miUbicacion)
            radius(100.0)
            strokeColor(Color.GREEN)
        })
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        val localizacionEstablecimiento = Location("")
        localizacionEstablecimiento.longitude = p0.position.longitude
        localizacionEstablecimiento.latitude = p0.position.latitude
        val establecimiento: Establecimiento = comprobarLocalizacion(localizacionEstablecimiento)
        if (establecimiento.ubicacion!!.longitude != null) {
            Compartido.establecimiento = establecimiento

        }
        val distanciaEnMetros = localizacionEstablecimiento.distanceTo(ubicacion)

            if (distanciaEnMetros > 100) {
                Toast.makeText(this, "Demasiado lejos, pero puedes ver la carta", Toast.LENGTH_SHORT)
                    .show()
                val verCartaIntent = Intent(this, VerCartaActivity::class.java).apply {
                }
                startActivity(verCartaIntent)
            } else {
                val realizarComandaIntent = Intent(this, RealizarComandaActivity::class.java).apply {
                }
                startActivity(realizarComandaIntent)
            }
        return true
    }

    /**
     * Con este método obtenemos el establecimiento en el que hemos pinchado
     */
    private fun comprobarLocalizacion(localizacionEstablecimiento: Location): Establecimiento {
        val ubicacionEstablecimiento =
            LatLng(localizacionEstablecimiento.latitude, localizacionEstablecimiento.longitude)
        var seleccionado: Establecimiento? = null
        for (establecimiento in establecimientos) {
            if (establecimiento.ubicacion!! == ubicacionEstablecimiento) {
                seleccionado = establecimiento
            }
        }
        return seleccionado!!
    }

}