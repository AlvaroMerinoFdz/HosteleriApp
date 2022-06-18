package com.example.hosteleriapp.adaptadores

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.hosteleriapp.Objetos.Compartido
import com.example.hosteleriapp.Objetos.Producto
import com.example.hosteleriapp.R
import com.example.hosteleriapp.Utiles.Firebase
import java.io.File

class AdaptadorVerCarta(var productos: ArrayList<Producto>, var context: AppCompatActivity) :
    RecyclerView.Adapter<AdaptadorVerCarta.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_producto, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = productos[position]
        holder.nombre.text = producto.nombre
        holder.descripcion.text = producto.descripcion
        holder.precio.text = producto.precio.toString()
        cargarImagenAdaptador(producto.imagen, Compartido.carpetaProductos, holder.imagen)
    }

    private fun cargarImagenAdaptador(nombreImagen: String?, carpeta: String, imagen: ImageView) {
        var spaceRef = Firebase.storageRef.child("$carpeta/$nombreImagen.jpg")
        val localfile = File.createTempFile("tempImage", "jpg")
        spaceRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            imagen.setImageBitmap(bitmap)
        }.addOnFailureListener {
            Toast.makeText(context, R.string.download_failed, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int {
        return productos.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre = view.findViewById<TextView>(R.id.txtNombreProducto)
        val descripcion = view.findViewById<TextView>(R.id.txtDescripcionProducto)
        val precio = view.findViewById<TextView>(R.id.txtPrecioProducto)
        val imagen = view.findViewById<ImageView>(R.id.imgMostrarProducto)
    }
}