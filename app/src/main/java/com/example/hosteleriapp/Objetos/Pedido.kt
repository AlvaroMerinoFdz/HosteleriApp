package com.example.hosteleriapp.Objetos

import java.io.Serializable

data class Pedido(var producto: String, var cantidad: Int = 0):Serializable
