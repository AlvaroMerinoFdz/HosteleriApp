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
import com.example.hosteleriapp.Objetos.Producto
import com.example.hosteleriapp.R
import com.example.hosteleriapp.Utiles.Firebase
import kotlinx.android.synthetic.main.activity_add_producto.*
import kotlinx.android.synthetic.main.activity_editar_producto.*
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

class EditarProductoActivity : AppCompatActivity() {

    lateinit var producto: Producto
    var imagen: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_producto)

        producto = Compartido.producto

        if(!producto.imagen.isNullOrEmpty()){
            cargarImagen(producto.imagen,Compartido.carpetaProductos)
        }


        txtNombreProductoEditar.setText(producto.nombre)
        txtDescripcionProductoEditar.setText(producto.descripcion)
        txtPrecioProductoEditar.setText(producto.precio.toString())

        txtNombreProductoEditar.isEnabled = false

        btnEditarProducto.setOnClickListener {
            if (txtNombreProductoEditar.text.isNotEmpty() && txtDescripcionProductoEditar.text.isNotEmpty() && txtPrecioProductoEditar.text.isNotEmpty()) {
                var nombre = txtNombreProductoEditar.text.toString()
                var descripcion = txtDescripcionProductoEditar.text.toString()
                var precio = txtPrecioProductoEditar.text.toString().toDouble()
                var producto = Producto(producto.correo, nombre, descripcion, precio)
                Firebase.addProducto(producto)
                Toast.makeText(this, R.string.producto_edited, Toast.LENGTH_LONG).show()
                onBackPressed()
            } else {
                Toast.makeText(this, R.string.product_failed, Toast.LENGTH_LONG).show()
            }
        }

        btnCancelEditarProducto.setOnClickListener {
            onBackPressed()
        }
        imgbtnEditarImagen.setOnClickListener {
            seleccionarImagen()
        }
    }

    private fun seleccionarImagen() {
        AlertDialog.Builder(this)
            .setTitle("Seleccionar Imagen")
            .setMessage("¿Qué método deseas utilizar?")
            .setPositiveButton("Cámara") { view, _ ->
                hacerFotoCamara()
                view.dismiss()
            }
            .setNegativeButton("Galería") { view, _ ->
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
                Compartido.codigo_camara
            )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, Compartido.codigo_camara)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Compartido.codigo_camara -> {
                if (resultCode == Activity.RESULT_OK) {
                    var photo = data?.extras?.get("data") as Bitmap
                    cambiarImagen(photo)
                }
            }
            Compartido.codigo_galeria -> {
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
            Intent.createChooser(intent, "Seleccione una imagen"),
            Compartido.codigo_galeria
        )
    }
    fun cargarImagen(nombreImagen: String?, carpeta:String) {
        var spaceRef = Firebase.storageRef.child("$carpeta/$nombreImagen.jpg")
        val localfile  = File.createTempFile("tempImage","jpg")
        spaceRef.getFile(localfile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localfile.absolutePath)
            imgbtnEditarImagen.setImageBitmap(bitmap)
        }.addOnFailureListener{
            Toast.makeText(this,"Algo ha fallado en la descarga", Toast.LENGTH_SHORT).show()
        }

    }
}