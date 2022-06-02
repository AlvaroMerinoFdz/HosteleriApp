package com.example.hosteleriapp.adaptadores

import android.content.DialogInterface
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.hosteleriapp.Objetos.Establecimiento
import com.example.hosteleriapp.Objetos.Rol
import com.example.hosteleriapp.Objetos.Usuario
import com.example.hosteleriapp.R
import com.example.hosteleriapp.Utiles.Firebase.borrarEstablecimiento
import com.example.hosteleriapp.Utiles.Firebase.borrarUsuario
import com.example.hosteleriapp.Utiles.Firebase.crearEstablecimiento
import com.example.hosteleriapp.Utiles.Firebase.modUsuario


class AdaptadorUsuarios(var usuarios: ArrayList<Usuario>, var context: AppCompatActivity) :
    RecyclerView.Adapter<AdaptadorUsuarios.ViewHolder>() {
    companion object {
        var seleccionado: Int = -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.usuarios_card, parent, false)
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val usuario = usuarios[position]
        holder.correo.text = usuario.correo
        holder.bind(usuario, context, position, this,usuarios)
    }

    override fun getItemCount(): Int {
        return usuarios.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val correo = view.findViewById<TextView>(R.id.txtCorreo)


        fun seleccionar(adaptadorUsuarios: AdaptadorUsuarios, pos: Int) {
            seleccionado = if (pos == seleccionado) {
                -1
            } else {
                pos
            }
            //Con la siguiente instrucción forzamos a recargar el viewHolder porque han cambiado los datos. Así pintará al seleccionado.
            adaptadorUsuarios.notifyDataSetChanged()
        }

        fun bind(
            usuario: Usuario,
            context: AppCompatActivity,
            pos: Int,
            adaptadorUsuarios: AdaptadorUsuarios,
            usuarios:ArrayList<Usuario>
        ) {
            with(correo) {
                if (usuario.rol == Rol.USUARIO) {

                    this.setTextColor(resources.getColor(R.color.colorLetra))
                    this.setTypeface(Typeface.SERIF)

                } else {
                    //lo ponemos en negrita
                    this.setTypeface(Typeface.DEFAULT_BOLD)
                    this.setTextColor(resources.getColor(R.color.colorLetra))
                }
            }

            itemView.setOnClickListener(
                View.OnClickListener
                {
                    var valor: Int
                    if (adaptadorUsuarios.usuarios[pos].rol == Rol.USUARIO) {
                        valor = R.string.aBar
                    } else {
                        valor = R.string.aUsuario
                    }

                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("Opciones del usuario")
                        .setPositiveButton(R.string.eliminar,
                            DialogInterface.OnClickListener { dialog, id ->
                                //eliminamos el usuario de la BBDD
                                borrarUsuario(adaptadorUsuarios.usuarios[pos])
                                //Lo eliminamos también de la lista del adapter.
                                usuarios.remove(adaptadorUsuarios.usuarios[pos])
                                adaptadorUsuarios.notifyDataSetChanged()
                            })
                        .setNegativeButton(valor,
                            DialogInterface.OnClickListener { dialog, id ->
                                if (adaptadorUsuarios.usuarios[pos].rol == Rol.BAR) {
                                    adaptadorUsuarios.usuarios[pos].rol = Rol.USUARIO
                                    borrarEstablecimiento(
                                        Establecimiento(
                                            adaptadorUsuarios.usuarios[pos].correo,
                                            adaptadorUsuarios.usuarios[pos].contraseña,
                                            adaptadorUsuarios.usuarios[pos].nombre,
                                            adaptadorUsuarios.usuarios[pos].apellidos,
                                            null
                                        )
                                    )    } else {
                                    adaptadorUsuarios.usuarios[pos].rol = Rol.BAR
                                    var establecimiento = Establecimiento(
                                        correo = adaptadorUsuarios.usuarios[pos].correo,
                                        contraseña = adaptadorUsuarios.usuarios[pos].contraseña,
                                        nombre = adaptadorUsuarios.usuarios[pos].nombre,
                                        apellidos = adaptadorUsuarios.usuarios[pos].apellidos,
                                        ubicacion = null
                                    )
                                    crearEstablecimiento(establecimiento)
                                }
                                modUsuario(adaptadorUsuarios.usuarios[pos])
                            })
                    builder.create().show()
                    adaptadorUsuarios.notifyDataSetChanged()
                })

        }

    }
}
