package com.example.hosteleriapp.Bar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hosteleriapp.Objetos.Compartido
import com.example.hosteleriapp.Objetos.Producto
import com.example.hosteleriapp.R
import com.example.hosteleriapp.Utiles.Firebase
import kotlinx.android.synthetic.main.activity_add_producto.*

class AddProductoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_producto)

        btnAddProductBBDD.setOnClickListener{
            var nombre = txtNombreProductoAdd.text.toString()
            var descripcion = txtDescripcionProductoAdd.text.toString()
            var precio = txtPrecioProductoAdd.text.toString().toDouble()
            var producto = Producto(Compartido.usuario.correo, nombre,descripcion,precio)
            Firebase.addProducto(producto)
            Toast.makeText(this,R.string.producto_added,Toast.LENGTH_LONG).show()
            onBackPressed()
        }

        btnCancelAddProductBBDD.setOnClickListener {
            onBackPressed()
        }
    }
}