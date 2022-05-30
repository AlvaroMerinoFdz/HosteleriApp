package com.example.hosteleriapp.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.hosteleriapp.Objetos.Producto
import com.example.hosteleriapp.R
import org.w3c.dom.Text

class AdaptadorProductos(var productos:ArrayList<Producto>, var context: AppCompatActivity)
    : RecyclerView.Adapter<AdaptadorProductos.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.item_producto,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val producto = productos[position]
        holder.nombre.text = producto.nombre
        holder.descripcion.text = producto.descripcion
        holder.precio.text = producto.precio.toString()

    }

    override fun getItemCount(): Int {
        return productos.size
    }

    class ViewHolder(view:View) : RecyclerView.ViewHolder(view) {
        val nombre = view.findViewById<TextView>(R.id.txtNombreProducto)
        val descripcion = view.findViewById<TextView>(R.id.txtDescripcionProducto)
        val precio = view.findViewById<TextView>(R.id.txtPrecioProducto)


    }
}