package com.bimce.aeroreservas

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

data class OpcionGenero(val id: Int, val nombre: String)


class OpcionGeneroAdapter(context: Context, private val opciones: List<OpcionGenero>) :
    ArrayAdapter<OpcionGenero>(context, android.R.layout.simple_spinner_item, opciones) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        (view.findViewById<View>(android.R.id.text1) as TextView).text = opciones[position].nombre
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        (view.findViewById<View>(android.R.id.text1) as TextView).text = opciones[position].nombre
        return view
    }
}
