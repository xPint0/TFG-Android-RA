package com.example.tfg_android_ra

import android.app.AlertDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tfg_android_ra.databinding.FragmentFirstBinding
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import kotlinx.coroutines.processNextEventInCurrentThread


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    private var scanner: GmsBarcodeScanner? = null
    private var audioManager: MediaPlayer? = null

    private val VOL_ON = 0.5f
    private val VOL_OFF = 0f

    private val song1 = R.raw.paranoid
    private val song2 = R.raw.byob


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        scanner = GmsBarcodeScanning.getClient(requireContext(), options)
        Log.d("savedInstanceState", savedInstanceState.toString())

        //llamo al metodo de girar al vinilo y comienzo la musica
        binding.ivVinyl.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_animation))
        startAudio(song1)

        binding.fabQrscan.setOnClickListener { view ->
            startScan()
        }

        binding.cbVolumeSelector.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                audioManager?.setVolume(VOL_ON, VOL_ON)
                Snackbar.make(view, "Has restablezido el volumen", Snackbar.LENGTH_LONG).show()
                Log.d("volumen", "on")

            }else{
                audioManager?.setVolume(VOL_OFF, VOL_OFF)
                Snackbar.make(view, "Has muteado el volumen", Snackbar.LENGTH_LONG).show()
                Log.d("volumen", "off")

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        audioManager?.release()
    }

    private fun startScan() {
        // Escanear QR
        scanner?.startScan()?.addOnSuccessListener { barcode ->
            val values = barcode.rawValue
            Log.d("barcode", "OK! value: $values")

            if (values?.startsWith("TFG-Android-RA") == true) {
                val bundle = Bundle().apply {
                    putString("QRvalue", values)
                }
                // Navegar al segundo fragmento con el bundle
                //TODO arreglar la navegacion
                findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment, bundle)
            } else {
                Log.d("barcode", "QR no valido de la app (No contiene el texto requerido)")
                showErrorDialog()
            }
        }
            ?.addOnCanceledListener {
                Log.d("barcode", "Canceled!")
            }
            ?.addOnFailureListener { e ->
                Log.d("barcode", "Failed read!")
                showErrorDialog()
            }
    }

    private fun startAudio(uri: Int) {

        val estado = binding.cbVolumeSelector.isChecked

        audioManager?.release() // Liberar el recurso del MediaPlayer si ya estaba en uso
        audioManager = MediaPlayer.create(requireContext(), uri)
        audioManager?.setVolume(if(estado) VOL_ON else VOL_OFF, if(estado) VOL_ON else VOL_OFF) //comprobacion del estado para que la siguiente cancion
                                                                                                // siga con el mismo estado q la anterior
        audioManager?.setOnCompletionListener {
            if (uri == song1) {
                startAudio(song2) // Si se completó la reproducción de la primera canción, iniciar la segunda
            } else {
                startAudio(song1) // Si se completó la reproducción de la segunda canción, iniciar la primera
            }
        }
        audioManager?.start()
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("ERROR")
            .setMessage("Se ha detectado un problema al escanear el QR, vuelva a intentarlo o pruebe con otro QR")
            .setPositiveButton("Aceptar", null)
            .show()
    }


}