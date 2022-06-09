package com.example.hosteleriapp.Objetos

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

class Establecimiento(
    correo: String,
    contraseña: String,
    nombre: String? = "",
    apellidos: String? = "",
    ubicacion: LatLng?
) : Usuario(correo, contraseña, Rol.BAR, nombre, apellidos), Serializable {

    override var correo: String = correo
    override var contraseña: String = contraseña
    override var nombre: String? = nombre
    override var apellidos: String? = apellidos
    var ubicacion: LatLng? = ubicacion
}
