package com.example.tfg_android_ra

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.DialogFragment
import java.io.File

class DialogFragment2 : DialogFragment() {
    private var integrantes: Map<String, String>? = null
    private lateinit var imagen: File

    fun configurarIntegrantes(integrantes: Map<String, String>, localFile: File) {
        this.integrantes = integrantes
        this.imagen = localFile
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_fragment_2, container, false)
        val linearLayout = view.findViewById<LinearLayout>(R.id.ly_scroll)

        // Verificar que los integrantes no sean nulos y que el LinearLayout exista
        if (integrantes != null && linearLayout != null) {
            // Recorrer el mapa de integrantes y crear un TextView para cada integrante
            integrantes?.forEach { (rol, nombre) ->
                val cardView = CardView(requireContext()).apply {
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    val card = inflater.inflate(R.layout.cardi_integrantes, this, false)
                    val ivFoto = card.findViewById<ImageView>(R.id.iv_foto)
                    val tvNombre = card.findViewById<TextView>(R.id.tv_nombre)
                    val tvRol = card.findViewById<TextView>(R.id.tv_rol)

                    val bitmap = BitmapFactory.decodeFile(imagen.absolutePath) //transformar la imagen a bitmap y asignarla al imageview
                    ivFoto.setImageBitmap(bitmap)
                    tvNombre.text = nombre
                    tvRol.text = rol

                    linearLayout.addView(card) //Anadir el cardview al layout
                }
            }
        }

        return view
    }
}
