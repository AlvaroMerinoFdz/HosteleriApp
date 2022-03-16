package com.example.hosteleriapp.Utiles

import android.content.ContentValues
import android.util.Log
import com.example.hosteleriapp.Objetos.Compartido
import com.example.hosteleriapp.Objetos.Rol
import com.example.hosteleriapp.Objetos.Usuario
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

     fun obtenerUsuario(datos: QuerySnapshot?, correo:String) {
         var miArray : ArrayList<Usuario> = ArrayList()
        for (dc: DocumentChange in datos?.documentChanges!!) {
            if (dc.type == DocumentChange.Type.ADDED) {
                var rolS :String = dc.document.get("rol") as String
                var rol = Rol.ADMIN
                if(rolS == Rol.USUARIO.toString()){
                    rol = Rol.USUARIO
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
         for(user in miArray){
             if(correo == user.correo){
                 Compartido.usuario = user
             }
         }
    }
}