package com.example.hosteleriapp.Bar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.hosteleriapp.Objetos.Compartido
import com.example.hosteleriapp.R
import com.google.android.gms.maps.GoogleMap
import kotlinx.android.synthetic.main.activity_bar.*

class BarActivity : AppCompatActivity() {

    lateinit var transaction: FragmentTransaction
    lateinit var fragmentGestionarCarta:Fragment
    lateinit var fragmentGestionarComandas: Fragment
    lateinit var fragmentGestionarLocalizacion:Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar)
        Compartido.appCompatActivity = this
        Toast.makeText(this,Compartido.usuario.correo,Toast.LENGTH_LONG).show()

        fragmentGestionarCarta = FragmentGestionarCarta()
        fragmentGestionarComandas = FragmentGestionarComandas()
        fragmentGestionarLocalizacion = FragmentGestionarLocalizacion()

        supportFragmentManager.beginTransaction().add(R.id.contenedor_fragments_bar,fragmentGestionarComandas).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        transaction = supportFragmentManager.beginTransaction()
        when(item.itemId){
            R.id.opcCarta -> transaction.replace(R.id.contenedor_fragments_bar,fragmentGestionarCarta).commit()
            R.id.opcComandas ->  transaction.replace(R.id.contenedor_fragments_bar,fragmentGestionarComandas).commit()
            R.id.opcLocalizacion ->  transaction.replace(R.id.contenedor_fragments_bar,fragmentGestionarLocalizacion).commit()
        }
        return super.onOptionsItemSelected(item)
    }
}