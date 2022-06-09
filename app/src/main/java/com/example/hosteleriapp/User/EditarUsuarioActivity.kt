package com.example.hosteleriapp.User

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.hosteleriapp.Objetos.Compartido
import com.example.hosteleriapp.Objetos.Usuario
import com.example.hosteleriapp.R
import com.example.hosteleriapp.Utiles.Firebase
import kotlinx.android.synthetic.main.activity_add_producto.*
import kotlinx.android.synthetic.main.activity_editar_usuario.*

class EditarUsuarioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_usuario)

        var usuarioEditar: Usuario = Compartido.usuario

        etEmailEditar.setText(usuarioEditar.correo)
        etNombreEditar.setText(usuarioEditar.nombre)
        etApellidosEditar.setText(usuarioEditar.apellidos)
        etPwdEditar.setText(usuarioEditar.contraseña)

        btnEditar.setOnClickListener {
            if (etPwdEditar.text.isNotEmpty() && etNombreEditar.text.isNotEmpty() && etApellidosEditar.text.isNotEmpty()) {
                usuarioEditar.nombre = etNombreEditar.text.toString()
                usuarioEditar.apellidos = etApellidosEditar.text.toString()
                usuarioEditar.contraseña = etPwdEditar.text.toString()

                Firebase.crearUsuario(usuarioEditar)
                Toast.makeText(this, R.string.usuario_editado, Toast.LENGTH_LONG).show()
                onBackPressed()
            } else {
                Toast.makeText(this, R.string.product_failed, Toast.LENGTH_LONG).show()
            }
        }

    }
}