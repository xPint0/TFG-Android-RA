package com.example.tfg_android_ra

import android.R
import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.tfg_android_ra.databinding.FragmentFirstBinding
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE).build()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val scanner = GmsBarcodeScanning.getClient(requireContext(), options)
        val audioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager

        //llamo al metodo de girar al vinilo
        //rotateImageView(binding.imageView)

        binding.fabQrscan.setOnClickListener { view ->

            //Escaner QR
            scanner.startScan().addOnSuccessListener { barcode ->
                val values = barcode.rawValue
                Log.d("barcode", "OK! value: $values")
            }
                .addOnCanceledListener() {
                    Log.d("barcode", "Canceled!")
                }
                .addOnFailureListener { e ->
                    Log.d("barcode", "Failed read!")
                }

        }



        binding.cbVolumeSelector.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                audioManager?.adjustVolume(AudioManager.ADJUST_UNMUTE, AudioManager.FLAG_PLAY_SOUND)
                Snackbar.make(view, "Has restablezido el volumen", Snackbar.LENGTH_LONG).show()
                Log.d("volumen", "on")
            }else{
                audioManager?.adjustVolume(AudioManager.ADJUST_MUTE, AudioManager.FLAG_PLAY_SOUND)
                Snackbar.make(view, "Has muteado el volumen", Snackbar.LENGTH_LONG).show()
                Log.d("volumen", "off")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun rotateImageView(imageView: ImageView) {
        // Definir la animación de rotación en el eje X
        val rotateAnimation = RotateAnimation(0f, 0f)

        // Establecer la duración de la animación
        rotateAnimation.duration = 1000 // Duración en milisegundos (1 segundo)

        // Establecer la interpolación de la animación (opcional)
        rotateAnimation.interpolator = LinearInterpolator()

        // Establecer la repetición de la animación (opcional)
        rotateAnimation.repeatCount = Animation.INFINITE // Repetir la animación infinitamente

        // Establecer el tipo de transformación en 3D para la animación
        rotateAnimation.fillAfter = true

        // Iniciar la animación en la ImageView
        imageView.startAnimation(rotateAnimation)
    }



}