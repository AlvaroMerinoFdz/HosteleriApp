package com.example.hosteleriapp.Utiles

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import com.example.hosteleriapp.Objetos.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.File

object Firebase {

    private val db = Firebase.firestore
    val storageRef = Firebase.storage.reference

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
        var productos: ArrayList<Producto> = ArrayList()
        var datos: QuerySnapshot? = null

        runBlocking {
            val job: Job = launch(context = Dispatchers.Default) {
                datos =
                    getDataFromFireStore(
                        "productos",
                        "correo",
                        correo
                    ) as QuerySnapshot //Obtenermos la colección
            }
            //Con este método el hilo principal de onCreate se espera a que la función acabe y devuelva la colección con los datos.
            job.join() //Esperamos a que el método acabe: https://dzone.com/articles/waiting-for-coroutines
        }

        for (dc: DocumentChange in datos?.documentChanges!!) {
            if (dc.type == DocumentChange.Type.ADDED) {
                var producto = Producto(
                    dc.document.get("correo") as String,
                    dc.document.get("nombre") as String,
                    dc.document.get("descripcion") as String,
                    dc.document.get("precio") as Double
                )
                Log.e("Alvaro", producto.toString())
                productos.add(producto)
            }
        }
        return productos
    }

    suspend fun getDataFromFireStore(coleccion: String): QuerySnapshot? {
        return try {
            val data = db.collection(coleccion)
                .get()
                .await()
            data
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getDataFromFireStore(
        coleccion: String,
        nombreCampo: String,
        valorCampo: String
    ): QuerySnapshot? {
        return try {
            val data = db.collection(coleccion).whereEqualTo(nombreCampo, valorCampo)
                .get()
                .await()
            data
        } catch (e: Exception) {
            null
        }
    }


    fun addProducto(producto: Producto) {
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
        var ubicacion: LatLng? = null
        for (dc: DocumentChange in datos?.documentChanges!!) {
            if (dc.type == DocumentChange.Type.ADDED) {
                var objetoRecibido = dc.document.get("ubicacion") as HashMap<String?, Double?>?

                if (objetoRecibido != null) {
                    ubicacion =
                        LatLng(objetoRecibido.get("latitude")!!, objetoRecibido.get("longitude")!!)
                }
                var establecimiento = Establecimiento(
                    dc.document.get("correo").toString(),
                    dc.document.get("contraseña").toString(),
                    dc.document.get("nombre") as String,
                    dc.document.get("apellidos") as String, ubicacion
                )
                Log.e("Alvaro", establecimiento.toString())
                Log.e("Alvaro", establecimiento.toString())
                if (establecimiento.ubicacion != null) {
                    establecimientos.add(establecimiento)
                }
            }
        }
        return establecimientos
    }

    fun crearPedido(comanda: Comanda) {
        db.collection("comandas")
            .document(comanda.mesa.toString() + comanda.cliente + comanda.establecimiento + comanda.fecha.toString())
            .set(comanda)
            .addOnSuccessListener {
                Log.e(ContentValues.TAG, "Comanda añadido")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error añadiendo Comanda", e.cause)
            }
    }

    fun obtenerComandas(correo: String): ArrayList<Comanda> {
        var comandas: ArrayList<Comanda> = ArrayList()
        var datos: QuerySnapshot? = null

        runBlocking {
            val job: Job = launch(context = Dispatchers.Default) {
                datos =
                    getDataFromFireStore(
                        "comandas",
                        "establecimiento",
                        correo
                    ) as QuerySnapshot //Obtenermos la colección
            }
            //Con este método el hilo principal de onCreate se espera a que la función acabe y devuelva la colección con los datos.
            job.join() //Esperamos a que el método acabe: https://dzone.com/articles/waiting-for-coroutines
        }
        for (dc: DocumentChange in datos?.documentChanges!!) {
            var pedidos = ArrayList<Pedido>()
            var objetoRecibido: ArrayList<HashMap<String, Any>> =
                dc.document.get("pedidos") as ArrayList<HashMap<String, Any>>
            for (objeto in objetoRecibido) {
                var cantidad = objeto.get("cantidad") as Long
                var pedido: Pedido = Pedido(objeto.get("producto") as String, cantidad.toInt())
                pedidos.add(pedido)
            }
            if (dc.type == DocumentChange.Type.ADDED) {
                var mesa = dc.document.get("mesa") as Long
                var comanda = Comanda(
                    dc.document.get("cliente") as String, mesa.toInt(),
                    pedidos,
                    dc.document.get("establecimiento") as String,
                    dc.document.get("precio") as Double,
                    dc.document.get("completado") as Boolean,
                    dc.document.get("fecha") as String
                )
                comandas.add(comanda)
            }
        }
        return comandas
    }

    fun borrarComanda(comanda: Comanda) {
        val db = Firebase.firestore
        val TAG = "Alvaro"
        db.collection("comandas")
            .document(comanda.mesa.toString() + comanda.cliente + comanda.establecimiento + comanda.fecha.toString())
            .delete()
            .addOnSuccessListener { Log.d(TAG, "Comanda borrado.!") }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error al Comanda el producto.", e)
            }
    }

    fun getUbicacion(datos: QuerySnapshot?, correo: String): LatLng? {
        var ubicacion: LatLng? = null
        for (dc: DocumentChange in datos?.documentChanges!!) {
            if (dc.type == DocumentChange.Type.ADDED) {
                var objetoRecibido = dc.document.get("ubicacion") as HashMap<String?, Double?>?

                if (objetoRecibido != null) {
                    ubicacion =
                        LatLng(objetoRecibido.get("latitude")!!, objetoRecibido.get("longitude")!!)
                }
                var establecimiento = Establecimiento(
                    dc.document.get("correo").toString(),
                    dc.document.get("contraseña").toString(),
                    dc.document.get("nombre") as String,
                    dc.document.get("apellidos") as String, ubicacion
                )
                if (establecimiento.correo.equals(correo)) {
                    return ubicacion
                }
            }
        }
        return null
    }

    fun getEstablecimiento(datos: QuerySnapshot, correo: String): Establecimiento? {
        var ubicacion: LatLng? = null

        for (dc: DocumentChange in datos?.documentChanges!!) {
            if (dc.type == DocumentChange.Type.ADDED) {
                var objetoRecibido = dc.document.get("ubicacion") as HashMap<String?, Double?>?

                if (objetoRecibido != null) {
                    ubicacion =
                        LatLng(objetoRecibido.get("latitude")!!, objetoRecibido.get("longitude")!!)
                }
                var establecimiento = Establecimiento(
                    dc.document.get("correo").toString(),
                    dc.document.get("contraseña").toString(),
                    dc.document.get("nombre") as String,
                    dc.document.get("apellidos") as String, ubicacion
                )
                if (establecimiento.correo.equals(correo)) {
                    return establecimiento
                }
            }

        }
        return null
    }

    //Gestión de imágenes
    fun addImagen(image: Bitmap, carpeta: String, nombreImagen: String) {
        val imgRef = storageRef.child("$carpeta/$nombreImagen.jpg")
        imgRef.putBytes(getBytes(image)!!)
    }

    private fun getBytes(bitmap: Bitmap): ByteArray? {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }

}