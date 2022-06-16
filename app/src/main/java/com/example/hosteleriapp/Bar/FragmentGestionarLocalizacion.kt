package com.example.hosteleriapp.Bar

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.hosteleriapp.Objetos.Compartido
import com.example.hosteleriapp.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class FragmentGestionarLocalizacion : Fragment(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private val LOCATION_REQUEST_CODE: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createMapFragment()
    }

    private fun createMapFragment() {
        val mapFragment = requireActivity().supportFragmentManager
            .findFragmentById(R.id.mapAcceder) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gestionar_localizacion, container, false)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableMyLocation()
        createMarker() //--> Nos coloca varios marcadores en el mapa y nos coloca en el CIFP Virgen de Gracia con un Zoom.

    }

    private fun createMarker() {
        TODO("Not yet implemented")
    }

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

    /**
     * función que usaremos a lo largo de nuestra app para comprobar si el permiso ha sido aceptado o no.
     */
    fun isPermissionsGranted() = ContextCompat.checkSelfPermission(
        requireContext(),
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED


    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    /**
     * Método que solicita los permisos.
     */
    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                Compartido.appCompatActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(context, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                Compartido.appCompatActivity,
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
                    context,
                    "Para activar la localización ve a ajustes y acepta los permisos",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {}
        }
    }

}