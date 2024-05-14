package com.example.tfg_android_ra

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class DialogFragment1 : DialogFragment() {
    // variable para almacenar la información que va a mostrar
    private var texto: String? = null

    // Metodo para configurar la informacion que se va a mostrar en el dialogo
    fun configurarTexto(texto: String) {
        this.texto = texto
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_fragment_1, container, false)

        // Textview que va a contener el texto
        val textView = view.findViewById<TextView>(R.id.tv_uno)

        // Establecer el texto con la informacion recibida
        textView.text = texto

        return view
    }
}
