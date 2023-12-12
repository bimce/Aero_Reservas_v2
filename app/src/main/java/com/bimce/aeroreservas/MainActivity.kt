package com.bimce.aeroreservas

import ApiService
import Credenciales
import RespuestaAutenticacion
import android.content.Context
import android.content.Intent
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private val apiBaseUrl = "http://10.0.2.2:3001/"
    private val sharedPreferencesKey = "user_data"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val edtUsername = findViewById<EditText>(R.id.edtUsername)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnLogin.setOnClickListener {
            realizarAutenticacion(edtUsername.text.toString(), edtPassword.text.toString())
        }
        btnRegister.setOnClickListener {
            abrirActividadRegistro()
        }
    }


    private fun abrirActividadRegistro() {
        val intent = Intent(this, RegistroActivity::class.java)
        startActivity(intent)
    }

    private fun realizarAutenticacion(email: String, contraseña: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val credenciales = Credenciales(email, contraseña)

        mostrarMensaje("Autenticando...")

        val call = apiService.autenticarUsuario(credenciales)

        call.enqueue(object : Callback<RespuestaAutenticacion> {
            override fun onResponse(
                call: Call<RespuestaAutenticacion>,
                response: Response<RespuestaAutenticacion>
            ) {
                ocultarMensaje()

                if (response.isSuccessful) {
                    val respuesta = response.body()

                    if (respuesta?.usuario != null) {
                        val mensaje = respuesta.mensaje ?: "Inicio de sesión exitoso"
                        mostrarMensaje("$mensaje - Rut del  usuario: ${respuesta.usuario.rut}")
                        guardarRutUsuario(respuesta.usuario.rut)
                        val intent = Intent(this@MainActivity, home_Activity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        mostrarError("La respuesta no incluye información de usuario ")
                    }
                } else {
                    // Si la respuesta no es exitosa, mostrar el mensaje de error personalizado
                    val mensajeError = try {
                        // Intentar extraer el mensaje de error directamente desde el JSON
                        JSONObject(response.errorBody()?.string() ?: "").getString("mensaje")
                    } catch (e: JSONException) {
                        // Si hay algún error al extraer el mensaje, mostrar un mensaje genérico
                        "Error desconocido"
                    }

                    mostrarError(mensajeError)
                }
            }

            override fun onFailure(call: Call<RespuestaAutenticacion>, t: Throwable) {
                ocultarMensaje()
                mostrarError(t.message ?: "Error de red")
            }
        })
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun ocultarMensaje() {
        // Puedes implementar lógica adicional si es necesario
    }

    private fun guardarRutUsuario(rut: String) {
        val sharedPreferences = getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("rut", rut)
        editor.apply()
    }

}


