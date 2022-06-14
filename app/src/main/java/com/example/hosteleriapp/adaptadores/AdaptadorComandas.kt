package com.example.hosteleriapp.adaptadores

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.hosteleriapp.Bar.VisualizarComanda
import com.example.hosteleriapp.Objetos.Comanda
import com.example.hosteleriapp.Objetos.Compartido
import com.example.hosteleriapp.Objetos.Producto
import com.example.hosteleriapp.R
import com.example.hosteleriapp.Utiles.Firebase

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
        holder.bind(comanda,context,position, this,comandas)

    }

    override fun getItemCount(): Int {
        return comandas.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cliente = view.findViewById<TextView>(R.id.txtClienteComanda)
        val numeroMesa = view.findViewById<TextView>(R.id.txtNumeroMesaComanda)

        fun bind(comanda:Comanda, context: AppCompatActivity,pos:Int, adaptadorComandas: AdaptadorComandas,comandas:ArrayList<Comanda>){
            with(cliente){
                if(comanda.completado){
                    this.setTypeface(Typeface.DEFAULT_BOLD)
                    this.setTextColor(resources.getColor(R.color.colorLetra))
                }else{
                    this.setTypeface(Typeface.SERIF)
                    this.setTextColor(resources.getColor(R.color.colorLetra))
                }
            }
            with(numeroMesa){
                if(comanda.completado){
                    this.setTypeface(Typeface.DEFAULT_BOLD)
                    this.setTextColor(resources.getColor(R.color.colorLetra))
                }else{
                    this.setTypeface(Typeface.SERIF)
                    this.setTextColor(resources.getColor(R.color.colorLetra))
                }
            }
            itemView.setOnClickListener(View.OnClickListener {
                var valor:Int
                if(!comanda.completado){
                    valor =  R.string.completar_pedido
                }else{
                    valor = R.string.marcar_no_complete
                }

                val builder = AlertDialog.Builder(context)
                builder.setMessage("Opciones de la comanda")
                    .setPositiveButton(valor,DialogInterface.OnClickListener { dialog, which ->
                        if(!comanda.completado){
                            comanda.completado = true
                            Firebase.crearPedido(comanda)
                        }else{
                            comanda.completado = false
                            Firebase.crearPedido(comanda)
                        }
                    })
                    .setNegativeButton(R.string.visualizar_comanda, DialogInterface.OnClickListener { dialog, which ->
                        Compartido.comanda = comanda
                        Log.e("Alvaro",comanda.pedidos[0].producto)
                        val visualizarComandaIntent = Intent(Compartido.appCompatActivity, VisualizarComanda::class.java).apply {
                        }
                        context.startActivity(visualizarComandaIntent)
                    })
                builder.create().show()
                adaptadorComandas.notifyDataSetChanged()

            })
            itemView.setOnLongClickListener(View.OnLongClickListener  {
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Opciones de la comanda")
                    .setPositiveButton("Borrar Comanda", DialogInterface.OnClickListener { dialog, which ->
                            Firebase.borrarComanda(comanda)
                            comandas.remove(comanda)
                            adaptadorComandas.notifyDataSetChanged()
                        })
                builder.create().show()
                true

            })
        }
    }

}