package com.example.hosteleriapp.adaptadores

import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.hosteleriapp.Bar.EditarProductoActivity
import com.example.hosteleriapp.Objetos.Compartido
import com.example.hosteleriapp.Objetos.Producto
import com.example.hosteleriapp.R
import com.example.hosteleriapp.Utiles.Firebase

class AdaptadorProductos(var productos: ArrayList<Producto>, var context: AppCompatActivity) :
    RecyclerView.Adapter<AdaptadorProductos.ViewHolder>() {

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
        holder.bind(context, position, this, productos)

    }

    override fun getItemCount(): Int {
        return productos.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre = view.findViewById<TextView>(R.id.txtNombreProducto)
        val descripcion = view.findViewById<TextView>(R.id.txtDescripcionProducto)
        val precio = view.findViewById<TextView>(R.id.txtPrecioProducto)

        fun bind(
            context: AppCompatActivity,
            pos: Int,
            adaptadorProductos: AdaptadorProductos,
            productos: ArrayList<Producto>
        ) {
            itemView.setOnClickListener {
                // build alert dialog
                val dialogBuilder = AlertDialog.Builder(context)

                // set message of alert dialog
                dialogBuilder.setMessage(R.string.manage_products)
                    // if the dialog is cancelable
                    .setCancelable(true)
                    // positive button text and action
                    .setPositiveButton(
                        R.string.delete_product,
                        DialogInterface.OnClickListener { dialog, id ->
                            borrarProducto(adaptadorProductos.productos[pos], productos)
                            Toast.makeText(context, R.string.producto_added, Toast.LENGTH_LONG)
                                .show()

                            adaptadorProductos.notifyDataSetChanged()
                        })
                    // negative button text and action
                    .setNegativeButton(
                        R.string.editar_producto,
                        DialogInterface.OnClickListener { dialog, id ->
                            editarProducto(adaptadorProductos.productos[pos], context)
                            adaptadorProductos.notifyDataSetChanged()
                        })

                // create dialog box
                val alert = dialogBuilder.create()
                alert.show()

            }
        }

        private fun editarProducto(producto: Producto, context: AppCompatActivity) {
            Compartido.producto = producto
            var intentEditarProducto = Intent(context, EditarProductoActivity::class.java).apply { }
            context.startActivity(intentEditarProducto)
        }

        private fun borrarProducto(producto: Producto, productos: ArrayList<Producto>) {
            Firebase.borrarProducto(producto)
            productos.remove(producto)
        }

    }
}