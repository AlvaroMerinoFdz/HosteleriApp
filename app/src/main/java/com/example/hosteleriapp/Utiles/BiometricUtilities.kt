package com.example.hosteleriapp.Utiles

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.hosteleriapp.Objetos.Compartido
import com.example.hosteleriapp.R


interface BiometricAuthCallback {
    fun onSuccess()
    fun onError()
    fun onNotRecognized()
}

object BiometricUtilities {
    fun isDeviceReady(context: Context): Boolean {
        return BiometricManager.from(context).canAuthenticate(BIOMETRIC_WEAK) == BIOMETRIC_SUCCESS
    }

    var context: Context = Compartido.context

    fun showPrompt(
        tittle: String = context.getString(R.string.autenticacion),
        subtitle: String = context.getString(R.string.introduce_credenciales),
        description: String = context.getString(R.string.introduce_huella),
        cancelButton: String = context.getString(R.string.cancelar),
        activity: AppCompatActivity,
        callback: BiometricAuthCallback
    ) {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(tittle)
            .setSubtitle(subtitle)
            .setDescription(description)
            .setAllowedAuthenticators(BIOMETRIC_WEAK)
            .setNegativeButtonText(cancelButton)
            .build()

        val executor = ContextCompat.getMainExecutor(activity)
        val authenticationCallBack = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                callback.onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                callback.onError()

            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                callback.onNotRecognized()
            }
        }
        val prompt = BiometricPrompt(activity, executor, authenticationCallBack)
        prompt.authenticate(promptInfo)
    }
}