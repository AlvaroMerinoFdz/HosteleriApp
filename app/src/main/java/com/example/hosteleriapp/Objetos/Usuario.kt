package com.example.hosteleriapp.Objetos

import java.io.Serializable

data class Usuario(val correo:String, val contraseña:String, var rol:Rol = Rol.USUARIO, var nombre:String? = "", var apellidos:String? = ""):
    Serializable {
}