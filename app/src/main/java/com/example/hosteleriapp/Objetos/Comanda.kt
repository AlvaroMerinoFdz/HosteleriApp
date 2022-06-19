package com.example.hosteleriapp.Objetos

import com.example.hosteleriapp.Utiles.Utiles
import java.io.Serializable

data class Comanda(
    var cliente: String,
    var mesa: Int,
    var pedidos: ArrayList<Pedido>,
    var establecimiento: String,
    var precio: Double,
    var completado: Boolean = false,
    var fecha: String = Utiles.getCurrentDateTime().toString()
): Serializable
