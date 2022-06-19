package com.example.hosteleriapp.Bar

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.hosteleriapp.Objetos.Compartido
import com.example.hosteleriapp.Objetos.Compartido.codigo_camara
import com.example.hosteleriapp.Objetos.Compartido.codigo_galeria
import com.example.hosteleriapp.Objetos.Producto
import com.example.hosteleriapp.R
import com.example.hosteleriapp.Utiles.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_add_producto.*
import java.io.FileNotFoundException
import java.io.InputStream

class AddProductoActivity : AppCompatActivity() {

    var imagen: Bitmap? = null
    private val db = com.google.firebase.ktx.Firebase.firestore
    private val storageRef = com.google.firebase.ktx.Firebase.storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_producto)

        btnAddProductBBDD.setOnClickListener {
            if (txtNombreProductoAdd.text.isNotEmpty() && txtDescripcionProductoAdd.text.isNotEmpty() && txtPrecioProductoAdd.text.isNotEmpty()) {
                var nombre = txtNombreProductoAdd.text.toString()
                var descripcion = txtDescripcionProductoAdd.text.toString()
                var precio = txtPrecioProductoAdd.text.toString().toDouble()
                var nombreImagen: String = nombre + Compartido.usuario.correo
                var producto =
                    Producto(Compartido.usuario.correo, nombre, descripcion, precio, nombreImagen)
                Firebase.addImagen(imagen!!, Compartido.carpetaProductos, nombreImagen)
                Firebase.addProducto(producto)
                Toast.makeText(this, R.string.producto_added, Toast.LENGTH_LONG).show()
                onBackPressed()
            } else {
                Toast.makeText(this, R.string.product_failed, Toast.LENGTH_LONG).show()
            }
        }

        btnCancelAddProductBBDD.setOnClickListener {
            onBackPressed()
        }

        imgbtnAddImagen.setOnClickListener {
            seleccionarImagen()
        }

    }

    private fun seleccionarImagen() {
        AlertDialog.Builder(this)
            .setTitle(R.string.seleccionar_imagen)
            .setMessage(R.string.metodo_utilizar)
            .setPositiveButton(R.string.camara) { view, _ ->
                hacerFotoCamara()
                view.dismiss()
            }
            .setNegativeButton(R.string.galeria) { view, _ ->
                seleccionarDeGaleria()
                view.dismiss()
            }
            .setCancelable(true)
            .create()
            .show()
    }

    private fun hacerFotoCamara() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        )
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                codigo_camara
            )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, codigo_camara)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            codigo_camara -> {
                if (resultCode == Activity.RESULT_OK) {
                    var photo = data?.extras?.get("data") as Bitmap
                    cambiarImagen(photo)
                }
            }
            codigo_galeria -> {
                if (resultCode === Activity.RESULT_OK) {
                    val selectedImage = data?.data

                    val selectedPath: String? = selectedImage?.path
                    if (selectedPath != null) {
                        var imageStream: InputStream? = null
                        try {
                            imageStream = selectedImage.let {
                                contentResolver.openInputStream(
                                    it
                                )
                            }
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        }
                        val bmp = BitmapFactory.decodeStream(imageStream)
                        cambiarImagen(Bitmap.createScaledBitmap(bmp, 400, 400, true))
                    }
                }
            }
        }
    }

    private fun cambiarImagen(image: Bitmap) {
        this.imagen = image
        imgbtnAddImagen.setImageBitmap(image)
        imagen = image
    }

    private fun seleccionarDeGaleria() {
        val intent = Intent()

        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.selecciona_imagen)),
            codigo_galeria
        )
    }


}