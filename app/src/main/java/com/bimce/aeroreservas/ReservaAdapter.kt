package com.bimce.aeroreservas

import Reserva
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bimce.aeroreservas.R

class ReservaAdapter(private val reservas: List<Reserva>) : RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder>() {

    class ReservaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewUsuarioRut: TextView = itemView.findViewById(R.id.rut)
        val textViewIdVuelos: TextView = itemView.findViewById(R.id.idvuelo)
        val textViewNombreApellido: TextView = itemView.findViewById(R.id.NombreApellido)
        val textViewPais: TextView = itemView.findViewById(R.id.Pais)
        val textViewNumeroDocumento: TextView = itemView.findViewById(R.id.NumeroDocumento)
        val textViewFechaNacimiento: TextView = itemView.findViewById(R.id.FechaNacimiento)
        val textViewSexo: TextView = itemView.findViewById(R.id.Sexo)
        val textViewEmail: TextView = itemView.findViewById(R.id.Email)
        val textViewTelefono: TextView = itemView.findViewById(R.id.Telefono)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservaViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_reserva, parent, false)
        return ReservaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReservaViewHolder, position: Int) {
        val reserva = reservas[position]

        holder.textViewUsuarioRut.text = "Usuario Rut: ${reserva.usuario_rut}"
        holder.textViewIdVuelos.text = "ID de Vuelos: ${reserva.ID_Vuelos}"
        holder.textViewNombreApellido.text = "Nombre y Apellido: ${reserva.Nombre_Apellido}"
        holder.textViewPais.text = "País: ${reserva.Pais}"
        holder.textViewNumeroDocumento.text = "Número de Documento: ${reserva.Numero_de_Documento}"
        holder.textViewFechaNacimiento.text = "Fecha de Nacimiento: ${reserva.Fecha_de_Nacimiento}"
        holder.textViewSexo.text = "Sexo: ${reserva.Sexo}"
        holder.textViewEmail.text = "Email: ${reserva.Email}"
        holder.textViewTelefono.text = "Teléfono: ${reserva.Telefono}"
    }

    override fun getItemCount(): Int {
        return reservas.size
    }
}
