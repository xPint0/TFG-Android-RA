package com.example.tfg_android_ra

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class DialogFragment1 : DialogFragment() {
    // Declarar una variable para almacenar la información que se mostrará en el diálogo
    private var texto: String? = null

    // Método para configurar la información que se mostrará en el diálogo
    fun configurarTexto(texto: String) {
        this.texto = texto
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflar el diseño del diálogo
        val view = inflater.inflate(R.layout.dialog_fragment_1, container, false)

        // Buscar el TextView en el diseño del diálogo
        val textView = view.findViewById<TextView>(R.id.tv_uno)

        // Establecer el texto del TextView con la información recibida
        textView.text = texto

        // Devolver la vista del diálogo
        return view
    }
}
