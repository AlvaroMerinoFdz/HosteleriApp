package com.example.hosteleriapp.Utiles

import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.lang.Exception

object LogIn{
    public var RC_SIGN_IN = 1
    private val db = Firebase.firestore
     suspend fun getDataFromFireStore(): QuerySnapshot? {
        return try{
            val data = db.collection("usuarios")
                .get()
                .await()
            data
        }catch (e : Exception){
            null
        }
    }
}
