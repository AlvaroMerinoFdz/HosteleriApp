package com.example.hosteleriapp.Utiles

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.example.hosteleriapp.R
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object LogIn {
    public var RC_SIGN_IN = 1
    private val db = Firebase.firestore
    suspend fun getDataFromFireStore(): QuerySnapshot? {
        return try {
            val data = db.collection("usuarios")
                .get()
                .await()
            data
        } catch (e: Exception) {
            null
        }
    }

    fun showAlert(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(R.string.error)
        builder.setMessage(R.string.error_autenticacion)
        builder.setPositiveButton(R.string.yes, null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
