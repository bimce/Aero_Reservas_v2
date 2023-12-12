package com.bimce.aeroreservas

import android.content.Context
import android.content.Intent

class SessionManager(private val context: Context) {

    private val sharedPreferencesKey = "user_data"

    fun cerrarSesion() {
        // Limpiar el Rut al cerrar sesión
        limpiarRutUsuario()

        // Redirigir a la pantalla de inicio de sesión
        iniciarActividad(MainActivity::class.java)
    }

    private fun limpiarRutUsuario() {
        val sharedPreferences = context.getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("rut")
        editor.apply()
    }

    private fun iniciarActividad(clase: Class<*>) {
        val intent = Intent(context, clase)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }
}