package com.bimce.aeroreservas

import Avion
import android.app.DatePickerDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AvionAdapter(private var listaAviones: List<Avion>) :
    RecyclerView.Adapter<AvionAdapter.AvionViewHolder>() {

    // ViewHolder para cada elemento de la lista
    class AvionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreVuelo: TextView = itemView.findViewById(R.id.nombreVuelo)
        val origen: TextView = itemView.findViewById(R.id.origen)
        val destino: TextView = itemView.findViewById(R.id.destino)
        val capacidad: TextView = itemView.findViewById(R.id.capacidad)
        val precio: TextView = itemView.findViewById(R.id.precio)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val fechaYHoraDeLlegada: TextView = itemView.findViewById(R.id.fechaYHoraDeLlegada)
        val fechaYHoraDeSalida: TextView = itemView.findViewById(R.id.fechaYHoraDeSalida)
        val botonReservar: Button = itemView.findViewById(R.id.botonReservar)
    }

    // Funciones de ayuda para mostrar el DatePickerDialog y formatear la fecha
    private fun showDatePickerDialog(fechaInicial: String?, textView: TextView) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            textView.context,
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

    // Resto del código

    fun actualizarDatos(nuevaListaAviones: List<Avion>) {
        this.listaAviones = nuevaListaAviones
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_avion, parent, false)
        return AvionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AvionViewHolder, position: Int) {
        val avion = listaAviones[position]

        holder.nombreVuelo.text = "Nombre Aerolínea: ${avion.Nombre_vuelo ?: "No disponible"}"
        holder.origen.text = "Origen: ${avion.Origen ?: "No disponible"}"
        holder.destino.text = "Destino: ${avion.Destino ?: "No disponible"}"
        holder.capacidad.text = "Capacidad: ${avion.Capacidad ?: "No disponible"}"
        holder.precio.text = "Precio: ${avion.Precio ?: "No disponible"}"
        holder.fechaYHoraDeLlegada.text = "Fecha de llegada: ${avion.Fecha_y_hora_de_llegada ?: "No disponible"}"
        holder.fechaYHoraDeSalida.text = "Fecha de salida: ${avion.Fecha_y_hora_de_salida ?: "No disponible"}"

        holder.fechaYHoraDeLlegada.setOnClickListener {
            showDatePickerDialog(avion.Fecha_y_hora_de_llegada, holder.fechaYHoraDeLlegada)
        }

        holder.fechaYHoraDeSalida.setOnClickListener {
            showDatePickerDialog(avion.Fecha_y_hora_de_salida, holder.fechaYHoraDeSalida)
        }
        holder.botonReservar.setOnClickListener {
            // Manejar clic en el botón "Reservar"
            val context = holder.itemView.context
            val intent = Intent(context, ReservaActivity::class.java)

            // Pasa el ID del vuelo a la actividad de reserva
            intent.putExtra("ID_Vuelo", avion.ID_Vuelos) // Asegúrate de tener un campo 'ID_Vuelos' en tu clase Avion

            // Puedes pasar más datos si es necesario
            intent.putExtra("NombreVuelo", avion.Nombre_vuelo)

            context.startActivity(intent)
        }

        // Cargar la imagen usando Glide
        Glide.with(holder.itemView.context)
            .load(avion.image)
            .into(holder.imageView)
    }


    override fun getItemCount(): Int {
        return listaAviones.size
    }
}

