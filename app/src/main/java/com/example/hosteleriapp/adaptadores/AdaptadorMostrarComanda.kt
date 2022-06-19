package com.example.hosteleriapp.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.hosteleriapp.Objetos.Pedido
import com.example.hosteleriapp.R

class AdaptadorMostrarComanda(var pedidos: ArrayList<Pedido>, var context: AppCompatActivity) :
    RecyclerView.Adapter<AdaptadorMostrarComanda.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_pedido, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var pedido = pedidos[position]
        holder.nombre.text = pedido.producto
        holder.cantidad.text = pedido.cantidad.toString()
    }

    override fun getItemCount(): Int {
        return pedidos.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre = view.findViewById<TextView>(R.id.txtNombreProductoMostrar)
        val cantidad = view.findViewById<TextView>(R.id.txtCantidadProductoMostrar)
    }

}