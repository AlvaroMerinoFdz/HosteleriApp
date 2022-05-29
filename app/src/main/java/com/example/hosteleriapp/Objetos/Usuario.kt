package com.example.hosteleriapp.Objetos

import java.io.Serializable

open class Usuario(
    open val correo: String,
    open val contraseña: String,
    var rol: Rol = Rol.USUARIO,
    open var nombre: String? = "",
    open var apellidos: String? = ""
) :
    Serializable {
}