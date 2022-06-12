package com.example.hosteleriapp.Utiles

import android.content.ContentValues
import android.util.Log
import com.example.hosteleriapp.Objetos.*
import com.example.hosteleriapp.Utiles.LogIn.getDataFromFireStore
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.lang.Exception

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
        var usuarios: ArrayList<Usuario> = ArrayList()
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

    fun obtenerCarta(correo: String): ArrayList<Producto> {
        var productos :ArrayList<Producto> = ArrayList()
        var datos: QuerySnapshot? = null

        runBlocking {
            val job: Job = launch(context = Dispatchers.Default) {
         datos=
            getDataFromFireStore("productos") as QuerySnapshot //Obtenermos la colección
            }
            //Con este método el hilo principal de onCreate se espera a que la función acabe y devuelva la colección con los datos.
            job.join() //Esperamos a que el método acabe: https://dzone.com/articles/waiting-for-coroutines
        }


        for (dc: DocumentChange in datos?.documentChanges!!) {
            if (dc.type == DocumentChange.Type.ADDED) {
                var producto = Producto(
                    dc.document.get("correo")as String,
                    dc.document.get("nombre")as String,
                    dc.document.get("descripcion") as String,
                    dc.document.get("precio") as Double
                )
                Log.e("Alvaro", producto.toString())
                productos.add(producto)
            }
        }
        return productos
    }
    suspend fun getDataFromFireStore(coleccion:String): QuerySnapshot?{
        return try{
            val data = db.collection(coleccion)
                .get()
                .await()
            data
        }catch (e : Exception){
            null
        }
    }


    fun addProducto(producto: Producto){
        db.collection("productos").document(producto.nombre + producto.correo)
            .set(producto)
            .addOnSuccessListener {
                Log.e(ContentValues.TAG, "Producto añadido")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error añadiendo producto", e.cause)
            }
    }

    fun borrarProducto(producto: Producto) {
        val db = Firebase.firestore
        val TAG = "Alvaro"
        db.collection("productos").document(producto.nombre + producto.correo).delete()
            .addOnSuccessListener { Log.d(TAG, "Producto borrado.!") }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al borrar el producto.", e)
            }
    }

    fun getEstablecimientos(datos: QuerySnapshot?): ArrayList<Establecimiento> {
        var establecimientos: ArrayList<Establecimiento> = ArrayList()
        var ubicacion:LatLng? = null
        for (dc: DocumentChange in datos?.documentChanges!!) {
            if (dc.type == DocumentChange.Type.ADDED) {
                var objetoRecibido = dc.document.get("ubicacion") as HashMap<String?,Double?>?

                if (objetoRecibido != null) {
                    ubicacion= LatLng(objetoRecibido.get("latitude")!!, objetoRecibido.get("longitude")!!)
                }
                    var establecimiento = Establecimiento(
                        dc.document.get("correo").toString(),
                        dc.document.get("contraseña").toString(),
                        dc.document.get("nombre") as String,
                        dc.document.get("apellidos") as String,ubicacion)
                    Log.e("Alvaro", establecimiento.toString())
                    Log.e("Alvaro", establecimiento.toString())
                    if(establecimiento.ubicacion != null){
                        establecimientos.add(establecimiento)
                    }
                }
        }
        return establecimientos
    }
}