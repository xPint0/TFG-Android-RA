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
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.tfg_android_ra.databinding.FragmentFirstBinding
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    private var scanner: GmsBarcodeScanner? = null
    private var audioManager: AudioManager? = null
    private var navController: NavController? = null


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

        navController = findNavController()
        scanner = GmsBarcodeScanning.getClient(requireContext(), options)
        audioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager

        //llamo al metodo de girar al vinilo
        binding.ivVinyl.startAnimation(AnimationUtils.loadAnimation(requireContext(), com.example.tfg_android_ra.R.anim.rotate_animation))

        binding.fabQrscan.setOnClickListener { view ->
            startScan()
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
    private fun startScan() {
        //Escaner QR
        scanner?.startScan()?.addOnSuccessListener { barcode ->
            val values = barcode.rawValue
            Log.d("barcode", "OK! value: $values")

            val bundle = Bundle()
            bundle.putString("QRvalue", values)

            //TODO:arreglar el tema de navegacion al segundo fragment, da error al estar en SavedInstance = true
            navController?.navigate(com.example.tfg_android_ra.R.id.action_FirstFragment_to_SecondFragment, bundle)
        }
            ?.addOnCanceledListener() {
                Log.d("barcode", "Canceled!")
            }
            ?.addOnFailureListener { e ->
                Log.d("barcode", "Failed read!")
            }
    }




}