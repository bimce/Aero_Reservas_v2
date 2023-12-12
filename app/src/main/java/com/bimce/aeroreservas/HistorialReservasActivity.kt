package com.bimce.aeroreservas

import ApiService
import Reservahistorial
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HistorialReservasActivity : AppCompatActivity() {
    private val BASE_URL = "http://10.0.2.2:3001/"
    private val sharedPreferencesKey = "user_data"
    val sessionManager = SessionManager(this)

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_reservas)

        recyclerView = findViewById(R.id.recyclerViewReservas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Obtén el rut del usuario almacenado en SharedPreferences
        val rutUsuario = obtenerRutUsuario()

        if (rutUsuario != null) {
            obtenerHistorialReservas(rutUsuario)
        } else {
            Log.e("Historial Reservas", "RUT del usuario no disponible")
        }
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.barraNavegacion)

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_home -> {
                    // Implementa la lógica para ir a la actividad home
                    val intent = Intent(this, home_Activity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_reservas -> {
                    // Ya estás en la actividad de reservas, no es necesario hacer nada
                    true
                }
                R.id.cerrar_sesion -> {
                    // Implementa la lógica para cerrar sesión
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    sessionManager.cerrarSesion()
                    true
                }
                else -> false
            }
        }


    }


    private fun obtenerRutUsuario(): String? {
        val sharedPreferences = getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        return sharedPreferences.getString("rut", null)
    }

    private fun obtenerHistorialReservas(usuario_rut: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        val call: Call<List<Reservahistorial>> = apiService.obtenerHistorialReservas(usuario_rut)

        call.enqueue(object : Callback<List<Reservahistorial>> {
            override fun onResponse(call: Call<List<Reservahistorial>>, response: Response<List<Reservahistorial>>) {
                if (response.isSuccessful) {
                    val historialReservas: List<Reservahistorial>? = response.body()

                    if (historialReservas != null) {
                        // Configura y muestra el RecyclerView
                        val adapter = ReservaAdapter(historialReservas)
                        recyclerView.adapter = adapter
                    }
                } else {
                    Log.e("API Error", "Error en la respuesta de la API")
                }
            }

            override fun onFailure(call: Call<List<Reservahistorial>>, t: Throwable) {
                Log.e("API Error", "Error en la solicitud a la API", t)
            }
        })
    }
}

