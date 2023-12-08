package com.bimce.aeroreservas

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReservaActivity : AppCompatActivity() {
    private lateinit var botonMostrarDatePicker: MaterialButton
    private var fechaNacimiento: Calendar = Calendar.getInstance()
    private val opcionesSexo = listOf(
        OpcionGenero(0, "Seleccionar Género"),
        OpcionGenero(1, "Mujer"),
        OpcionGenero(2, "Hombre")
    )
    private lateinit var spinnerSexo: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reserva)

        botonMostrarDatePicker = findViewById(R.id.botonMostrarDatePicker)
        botonMostrarDatePicker.setOnClickListener {
            mostrarDatePicker()
        }
        spinnerSexo = findViewById(R.id.spinnerSexo)
        val adapter = OpcionGeneroAdapter(this, opcionesSexo)
        spinnerSexo.adapter = adapter


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

}

