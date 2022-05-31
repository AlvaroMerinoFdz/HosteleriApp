package com.example.hosteleriapp.Utiles

import android.content.ContentValues
import android.util.Log
import com.example.hosteleriapp.Objetos.*
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object Firebase {

    private val db = Firebase.firestore

    fun crearUsuario(usuario: Usuario) {
        db.collection("usuarios").document(usuario.correo)
            .set(usuario)
            .addOnSuccessListener {
                Log.e(ContentValues.TAG, "Usuario añadido")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error añadiendo usuario", e.cause)
            }
    }

    fun obtenerUsuario(datos: QuerySnapshot?, correo: String) {
        var miArray: ArrayList<Usuario> = ArrayList()
        for (dc: DocumentChange in datos?.documentChanges!!) {
            if (dc.type == DocumentChange.Type.ADDED) {
                var rolS: String = dc.document.get("rol") as String
                var rol = Rol.ADMIN
                if (rolS == Rol.USUARIO.toString()) {
                    rol = Rol.USUARIO
                } else if (rolS == Rol.BAR.toString()) {
                    rol = Rol.BAR
                }
                var usuario = Usuario(
                    dc.document.get("correo").toString(),
                    dc.document.get("contraseña").toString(),
                    rol,
                    dc.document.get("nombre") as String,
                    dc.document.get("apellidos") as String
                )
                Log.e("Alvaro", usuario.toString())
                miArray.add(usuario)
            }
        }
        for (user in miArray) {
            if (correo == user.correo) {
                Compartido.usuario = user
            }
        }
    }

    public fun modUsuario(usuario: Usuario) {
        val db = Firebase.firestore
        db.collection("usuarios").document(usuario.correo)
            .set(usuario)
            .addOnSuccessListener {
                Log.e(ContentValues.TAG, "Usuario modificado")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error modificando usuario", e.cause)
            }
    }

    fun borrarUsuario(usuario: Usuario) {
        val db = Firebase.firestore
        val TAG = "Alvaro"
        if (usuario.rol == Rol.BAR) {
            borrarEstablecimiento(
                Establecimiento(
                    usuario.correo,
                    usuario.contraseña,
                    usuario.nombre,
                    usuario.apellidos,
                    null
                )
            )
        }
        db.collection("usuarios").document(usuario.correo).delete()
            .addOnSuccessListener { Log.d(TAG, "Documento borrado.!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error al borrar el documento.", e) }
    }

    fun obtenerUsuarios(datos: QuerySnapshot?): ArrayList<Usuario> {
        var usuarios: ArrayList<Usuario> = ArrayList<Usuario>()
        for (dc: DocumentChange in datos?.documentChanges!!) {
            if (dc.type == DocumentChange.Type.ADDED) {
                var rolS: String = dc.document.get("rol") as String
                var rol = Rol.ADMIN
                if (rolS == Rol.USUARIO.toString()) {
                    rol = Rol.USUARIO
                } else if (rolS == Rol.BAR.toString()) {
                    rol = Rol.BAR
                }
                var usuario = Usuario(
                    dc.document.get("correo").toString(),
                    dc.document.get("contraseña").toString(),
                    rol,
                    dc.document.get("nombre") as String,
                    dc.document.get("apellidos") as String
                )
                Log.e("Alvaro", usuario.toString())
                Log.e("Alvaro", usuario.toString())
                if (usuario.rol != Rol.ADMIN) {
                    usuarios.add(usuario)
                    Compartido.usuarios.add(usuario)
                }
            }
        }
        return usuarios
    }

    fun borrarEstablecimiento(usuario: Establecimiento) {
        val db = Firebase.firestore
        val TAG = "Alvaro"
        db.collection("establecimientos").document(usuario.correo).delete()
            .addOnSuccessListener { Log.d(TAG, "Documento borrado.!") }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al borrar el documento.", e)
            }
    }

    fun crearEstablecimiento(establecimiento: Establecimiento) {
        db.collection("establecimientos").document(establecimiento.correo)
            .set(establecimiento)
            .addOnSuccessListener {
                Log.e(ContentValues.TAG, "Establecimiento añadido")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error añadiendo establecimiento", e.cause)
            }
    }

    fun obtenerCarta(querySnapshot: QuerySnapshot?, correo: String): ArrayList<Producto> {
        var a = Producto("a","a","a",3)
        var b = Producto("b","b","b",3)
        var lista :ArrayList<Producto> = ArrayList()
        lista.add(a)
        lista.add(b)
        return lista
    }
}