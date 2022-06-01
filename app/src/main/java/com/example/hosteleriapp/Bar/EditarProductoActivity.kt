package com.example.hosteleriapp.Bar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hosteleriapp.Objetos.Compartido
import com.example.hosteleriapp.Objetos.Producto
import com.example.hosteleriapp.R
import com.example.hosteleriapp.Utiles.Firebase
import kotlinx.android.synthetic.main.activity_add_producto.*
import kotlinx.android.synthetic.main.activity_editar_producto.*

class EditarProductoActivity : AppCompatActivity() {
    lateinit var producto : Producto
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_producto)

       producto  = Compartido.producto


        txtNombreProductoEditar.setText(producto.nombre)
        txtDescripcionProductoEditar.setText(producto.descripcion)
        txtPrecioProductoEditar.setText(producto.precio.toString())

        txtNombreProductoEditar.isEnabled=false

        btnEditarProducto.setOnClickListener{
            if(txtNombreProductoEditar.text.isNotEmpty() && txtDescripcionProductoEditar.text.isNotEmpty() && txtPrecioProductoEditar.text.isNotEmpty()) {
                var nombre = txtNombreProductoEditar.text.toString()
                var descripcion = txtDescripcionProductoEditar.text.toString()
                var precio = txtPrecioProductoEditar.text.toString().toDouble()
                var producto = Producto(producto.correo, nombre, descripcion, precio)
                Firebase.addProducto(producto)
                Toast.makeText(this, R.string.producto_edited, Toast.LENGTH_LONG).show()
                onBackPressed()
            }else{
                Toast.makeText(this,R.string.product_failed,Toast.LENGTH_LONG).show()
            }
        }

        btnCancelEditarProducto.setOnClickListener {
            onBackPressed()
        }
    }
}