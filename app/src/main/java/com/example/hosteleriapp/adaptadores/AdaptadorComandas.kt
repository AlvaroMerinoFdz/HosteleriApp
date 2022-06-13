package com.example.hosteleriapp.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.hosteleriapp.Objetos.Comanda
import com.example.hosteleriapp.Objetos.Producto
import com.example.hosteleriapp.R

class AdaptadorComandas(var comandas: ArrayList<Comanda>, var context: AppCompatActivity) :
    RecyclerView.Adapter<AdaptadorComandas.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return AdaptadorComandas.ViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_visualizar_comanda, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comanda = comandas[position]
        holder.cliente.text = comanda.cliente
        holder.numeroMesa.text = comanda.mesa.toString()

    }

    override fun getItemCount(): Int {
        return comandas.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cliente = view.findViewById<TextView>(R.id.txtClienteComanda)
        val numeroMesa = view.findViewById<TextView>(R.id.txtNumeroMesaComanda)
    }

}