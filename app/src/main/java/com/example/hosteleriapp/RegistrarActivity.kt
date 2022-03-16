package com.example.hosteleriapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.hosteleriapp.Objetos.Compartido
import com.example.hosteleriapp.Objetos.Rol
import com.example.hosteleriapp.Objetos.Usuario
import com.example.hosteleriapp.Utiles.Firebase
import com.example.hosteleriapp.Utiles.LogIn
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_registrar.*

class RegistrarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar)

        btnSignUpRegistrar.setOnClickListener {
            if (etEmailRegistrar.text.isNotEmpty() && etPwdRegistrar.text.isNotEmpty() && etNombreRegistrar.text.isNotEmpty() && etApellidosRegistrar.text.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    etEmailRegistrar.text.toString(),
                    etPwdRegistrar.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Compartido.usuario = Usuario(etEmailRegistrar.text.toString(), etPwdRegistrar.text.toString(), Rol.USUARIO, etNombreRegistrar.text.toString(), etApellidosRegistrar.text.toString())
                        Firebase.crearUsuario(Compartido.usuario)
                        Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        LogIn.showAlert(this)
                    }
                }
            }
        }
    }


}
