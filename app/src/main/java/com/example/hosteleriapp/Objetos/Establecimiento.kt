package com.example.hosteleriapp.Objetos

import java.io.Serializable

class Establecimiento(
    correo: String,
    contraseña: String,
    nombre: String? = "",
    apellidos: String? = "",
    ubicacion: Ubicacion?
) : Usuario(correo, contraseña, Rol.BAR, nombre, apellidos), Serializable {

    override var correo: String = correo
    override var contraseña: String = contraseña
    override var nombre: String? = nombre
    override var apellidos: String? = apellidos
    var ubicacion: Ubicacion? = ubicacion
}
