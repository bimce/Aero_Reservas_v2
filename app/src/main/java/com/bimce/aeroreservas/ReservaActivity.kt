package com.bimce.aeroreservas

import ApiService
import Reserva
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReservaActivity : AppCompatActivity() {
    private val sharedPreferencesKey = "user_data"
    val sessionManager = SessionManager(this)
    private lateinit var botonMostrarDatePicker: Button
    private lateinit var botonRealizarReserva: Button
    private lateinit var fechaNacimiento: Calendar
    private lateinit var spinnerSexo: Spinner
    private lateinit var editTextNombreApellido: EditText
    private lateinit var editTextPais: EditText
    private lateinit var editTextNumeroDocumento: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextTelefono: EditText

    private val apiService: ApiService by lazy {
        // Crear una instancia única de ApiService
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/") // Cambia esto por la URL de tu servidor Node.js
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserva)


        // Inicializar vistas y configurar botones
        botonMostrarDatePicker = findViewById(R.id.botonMostrarDatePicker)
        botonRealizarReserva = findViewById(R.id.botonRealizarReserva)
        fechaNacimiento = Calendar.getInstance()
        spinnerSexo = findViewById(R.id.spinnerSexo)
        editTextNombreApellido = findViewById(R.id.editTextNombreApellido)
        editTextPais = findViewById(R.id.editTextPais)
        editTextNumeroDocumento = findViewById(R.id.editTextNumeroDocumento)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextTelefono = findViewById(R.id.editTextTelefono)

        // Configurar el botón para mostrar el DatePicker
        botonMostrarDatePicker.setOnClickListener {
            mostrarDatePicker()
        }

        // Configurar el botón para realizar la reserva
        botonRealizarReserva.setOnClickListener {
            realizarReserva()
            Toast.makeText(
                this@ReservaActivity,
                "Reserva realizada con éxito",
                Toast.LENGTH_SHORT
            ).show()

            // Redirigir al usuario a home_Activity
            val intent = Intent(this@ReservaActivity, home_Activity::class.java)
            startActivity(intent)
        }

        val arrayGenero = resources.getStringArray(R.array.array_genero)
        val adapterGenero = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrayGenero)
        adapterGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSexo.adapter = adapterGenero
        // Configurar la barra de navegación
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
                    val intent = Intent(this, HistorialReservasActivity::class.java)
                    startActivity(intent)
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

    private fun mostrarDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                // Actualizar la variable de fechaNacimiento
                fechaNacimiento.set(Calendar.YEAR, year)
                fechaNacimiento.set(Calendar.MONTH, monthOfYear)
                fechaNacimiento.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                // Puedes mostrar la fecha seleccionada en el botón
                botonMostrarDatePicker.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(fechaNacimiento.time)
            },
            fechaNacimiento.get(Calendar.YEAR),
            fechaNacimiento.get(Calendar.MONTH),
            fechaNacimiento.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    private fun realizarReserva() {
        val idVuelo = intent.getStringExtra("ID_Vuelo") ?: ""
        val nombreApellido = editTextNombreApellido.text?.toString()
        val Pais = editTextPais.text?.toString() ?: ""
        val Numero_de_Documento = editTextNumeroDocumento.text?.toString() ?: ""
        val fechaNacimientoFormatted = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(fechaNacimiento.time)
        val Sexo = spinnerSexo.selectedItem?.toString() ?: ""
        val Email = editTextEmail.text?.toString() ?: ""
        val Telefono = editTextTelefono.text?.toString() ?: ""

        // Obtener el rut del usuario en sesión desde SharedPreferences
        val rutUsuario = obtenerRutEnSesion()
        println("RUT del Usuario en Sesión: $rutUsuario")

        // Crear objeto Reserva
        val reserva = Reserva(
            operacion = "mysql_reserva",
            ID_Vuelos  = idVuelo ?: "", // Usar el ID del vuelo pasado desde el Intent
            Nombre_Apellido = nombreApellido ?: "",  // Asegúrate de que nombreApellido no sea nulo
            Pais = Pais,
            Numero_de_Documento = Numero_de_Documento,
            Fecha_de_Nacimiento = fechaNacimientoFormatted,
            Sexo = Sexo,
            Email = Email,
            Telefono = Telefono,
            rutUsuario = rutUsuario,
            ID_Cliente = rutUsuario,
        )

        // Realizar la llamada a la API de MySQL
        apiService.registrarReservaMySQLAndroid(reserva).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    // Procesar la respuesta exitosa de MySQL
                    val mensaje = response.body()
                    Toast.makeText(
                        this@ReservaActivity,
                        "Reserva realizada con éxito",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Redirigir al usuario a home_Activity
                    val intent = Intent(this@ReservaActivity, home_Activity::class.java)
                    startActivity(intent)
                    // Puedes mostrar un mensaje de éxito o realizar otras acciones
                } else {
                    // Manejar el error de la respuesta de MySQL
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // Manejar el error de la llamada a MySQL
            }
        })

        // Actualizar la operación para MongoDB
        reserva.operacion = "mongodb_reserva"

        // Realizar la llamada a la API de MongoDB
        apiService.registrarReservaMongoDBAndroid(reserva).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    // Procesar la respuesta exitosa de MongoDB
                    val mensaje = response.body()
                    Toast.makeText(
                        this@ReservaActivity,
                        "Reserva realizada con éxito",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Redirigir al usuario a home_Activity
                    val intent = Intent(this@ReservaActivity, home_Activity::class.java)
                    startActivity(intent)
                    // Puedes mostrar un mensaje de éxito o realizar otras acciones
                } else {
                    // Manejar el error de la respuesta de MongoDB
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                // Manejar el error de la llamada a MongoDB
            }
        })
    }


    private fun obtenerRutEnSesion(): String {
        val sharedPreferences = getSharedPreferences(sharedPreferencesKey, MODE_PRIVATE)
        return sharedPreferences.getString("rut", "") ?: ""
    }
}
