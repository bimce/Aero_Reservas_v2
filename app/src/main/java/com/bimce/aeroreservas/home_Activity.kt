package com.bimce.aeroreservas

import ApiService
import Avion
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class home_Activity : AppCompatActivity() {
    val sessionManager = SessionManager(this)
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AvionAdapter
    private var listaAviones: List<Avion> = emptyList()
    private lateinit var apiService: ApiService

    private lateinit var editTextFechaSalida: TextView
    private lateinit var editTextFechaLlegada: TextView
    private lateinit var botonBuscar: Button
    private lateinit var editTextOrigen: EditText
    private lateinit var editTextDestino: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.recyclerView)
        adapter = AvionAdapter(listaAviones)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        botonBuscar = findViewById(R.id.botonBuscar)
        editTextOrigen = findViewById(R.id.editTextOrigen)
        editTextDestino = findViewById(R.id.editTextDestino)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3001/") // Cambia esto por la URL de tu servidor Node.js
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        editTextFechaSalida = findViewById(R.id.editTextFechaSalida)
        editTextFechaLlegada = findViewById(R.id.editTextFechaLlegada)

        // Configurar el EditText y el botón de búsqueda
        editTextFechaSalida.setOnClickListener {
            mostrarCalendario(editTextFechaSalida)
        }

        editTextFechaLlegada.setOnClickListener {
            mostrarCalendario(editTextFechaLlegada)
        }

        botonBuscar.setOnClickListener {
            val destino = editTextDestino.text.toString()
            val origen = editTextOrigen.text.toString()
            val fechaSalida = editTextFechaSalida.text.toString()
            val fechaLlegada = editTextFechaLlegada.text.toString()

            if (destino.isNotEmpty()) {
                realizarBusqueda(origen, destino, fechaSalida, fechaLlegada)
            } else {
                obtenerListaDeAviones()
            }
        }

        obtenerListaDeAviones()

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.barraNavegacion)

        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_home -> {
                    Toast.makeText(this, "volviendo al inicio", Toast.LENGTH_SHORT).show()
                    if (this::recyclerView.isInitialized) {
                        listaAviones = emptyList()
                        adapter.actualizarDatos(listaAviones)
                        obtenerListaDeAviones()
                    } else {
                        if (javaClass != home_Activity::class.java) {
                            val intent = Intent(this, home_Activity::class.java)
                            startActivity(intent)
                        }
                    }
                    true
                }
                R.id.menu_reservas -> {
                    val intent = Intent(this, HistorialReservasActivity::class.java)
                    startActivity(intent)

                    true
                }
                R.id.cerrar_sesion -> {
                    Toast.makeText(this, " Cerrando  Sesión", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@home_Activity, MainActivity::class.java)
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

    private fun mostrarCalendario(textView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = formatDate(selectedYear, selectedMonth, selectedDay)
                textView.text = selectedDate
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    private fun obtenerListaDeAviones() {
        val call = apiService.obtenerAviones()

        call.enqueue(object : Callback<List<Avion>> {
            override fun onResponse(call: Call<List<Avion>>, response: Response<List<Avion>>) {
                if (response.isSuccessful) {
                    val aviones = response.body()
                    aviones?.let {
                        runOnUiThread {
                            Log.d("Aviones", it.toString())
                            adapter.actualizarDatos(it)
                        }
                    }
                } else {
                    // Manejar respuesta no exitosa
                }
            }

            override fun onFailure(call: Call<List<Avion>>, t: Throwable) {
                // Manejar el fallo de la solicitud
            }
        })
    }

    private fun realizarBusqueda(origen: String, destino: String, fechaSalida: String?, fechaLlegada: String?) {
        val call = apiService.buscarVuelos(origen = origen, destino = destino, fechaLlegada = fechaLlegada, fechaSalida = fechaSalida)

        call.enqueue(object : Callback<List<Avion>> {
            override fun onResponse(call: Call<List<Avion>>, response: Response<List<Avion>>) {
                if (response.isSuccessful) {
                    val aviones = response.body()
                    aviones?.let {
                        runOnUiThread {
                            Log.d("BusquedaAviones", it.toString())
                            adapter.actualizarDatos(it)
                            editTextFechaSalida.text = ""
                            editTextFechaLlegada.text = ""
                            botonBuscar.requestFocus()
                            editTextOrigen.text = Editable.Factory.getInstance().newEditable("")
                            editTextDestino.text = Editable.Factory.getInstance().newEditable("")
                        }
                    }
                } else {
                    // Manejar respuesta no exitosa
                }
            }

            override fun onFailure(call: Call<List<Avion>>, t: Throwable) {
                // Manejar el fallo de la solicitud
            }
        })
    }

}



