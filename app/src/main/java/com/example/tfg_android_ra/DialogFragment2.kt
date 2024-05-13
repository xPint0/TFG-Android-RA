package com.example.tfg_android_ra

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.StyleSpan
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment

class DialogFragment2 : DialogFragment() {
    private var integrantes: Map<String, String>? = null

    fun configurarIntegrantes(integrantes: Map<String, String>) {
        this.integrantes = integrantes
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_fragment_2, container, false)
        val linearLayout = view.findViewById<LinearLayout>(R.id.layoutPrincipal)

        // Verificar que los integrantes no sean nulos y que el LinearLayout exista
        if (integrantes != null && linearLayout != null) {
            // Recorrer el mapa de integrantes y crear un TextView para cada integrante
            integrantes?.forEach { (rol, nombre) ->
                val textView = TextView(context).apply {
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    setPadding(8,10, 8, 10)
                    setTextColor(ContextCompat.getColor(context, R.color.purple_dark))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
                    gravity = Gravity.CENTER

                    val spannableRol = SpannableString("$rol: ")
                    spannableRol.setSpan(StyleSpan(Typeface.BOLD), 0, spannableRol.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                    val spannableNombre = SpannableString(nombre)
                    spannableNombre.setSpan(StyleSpan(Typeface.NORMAL), 0, spannableNombre.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                    text = TextUtils.concat(spannableRol, spannableNombre)
                    setTypeface(resources.getFont(R.font.underwood_champion))
                }

                linearLayout.addView(textView)
            }
        }

        return view
    }
}
