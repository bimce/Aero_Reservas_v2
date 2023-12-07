package com.bimce.aeroreservas

import ApiService
import Comando
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RegistroActivity : AppCompatActivity() {
    private lateinit var editTextRut: EditText
    private lateinit var editTextNombre: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextContraseña: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        editTextRut = findViewById(R.id.editTextRut)
        editTextNombre = findViewById(R.id.editTextNombre)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextContraseña = findViewById(R.id.editTextContraseña)

        // Asigna un listener al botón de registro
        findViewById<Button>(R.id.btnRegistrar).setOnClickListener {
            registrarEnMongoDB()
            registrarEnSQL()
            redirigirAInicioSesion()

        }
    }

    private fun registrarEnMongoDB() {
        val comandoMongoDB = Comando(
            operacion = "mongodb",
            rut = editTextRut.text.toString(),
            nombre = editTextNombre.text.toString(),
            email = editTextEmail.text.toString(),
            contraseña = editTextContraseña.text.toString()
        )

        val retrofitMongoDB = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/") // Cambia esto por la URL de tu servidor Node.js
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiServiceMongoDB = retrofitMongoDB.create(ApiService::class.java)

        val callMongoDB = apiServiceMongoDB.registrarEnMongoDBAndroid(comandoMongoDB)

        callMongoDB.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val mensaje = response.body()

                } else {
                    // Manejar respuesta no exitosa de MongoDB
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // Manejar el fallo de la solicitud a MongoDB
            }
        })
    }

    private fun registrarEnSQL() {
        val comandoSQL = Comando(
            operacion = "sql",
            rut = editTextRut.text.toString(),
            nombre = editTextNombre.text.toString(),
            email = editTextEmail.text.toString(),
            contraseña = editTextContraseña.text.toString()
        )

        // Crea una instancia de Retrofit para SQL (puedes cambiar la URL según tu configuración)
        val retrofitSQL = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/") // Cambia esto por la URL de tu servidor SQL
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Crea una instancia de ApiService para SQL
        val apiServiceSQL = retrofitSQL.create(ApiService::class.java)

        // Realiza la llamada al endpoint de registro en SQL
        val callSQL = apiServiceSQL.registrarEnSQLAndroid(comandoSQL)

        callSQL.enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val mensaje = response.body()

                } else {
                    // Manejar respuesta no exitosa de SQL
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // Manejar el fallo de la solicitud a SQL
            }
        })
    }

    private fun redirigirAInicioSesion() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Cierra la actividad actual para que el usuario no pueda volver atrás
    }


    private fun mostrarToast(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

}
