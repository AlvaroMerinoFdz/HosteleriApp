package com.example.hosteleriapp.Objetos

data class Comanda(var cliente:String,
                   var mesa:Int,
                   var pedidos:ArrayList<String>,
                   var establecimiento:String,
                   var precio:Double,
                    var completado:Boolean = false)
