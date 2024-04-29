package com.example.tfg_android_ra

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tfg_android_ra.databinding.FragmentSecondBinding
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var arFragment: ArFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val qrValue = ""
        binding.tvPrueba.text = qrValue

        //val arFragment = childFragmentManager.findFragmentById(R.id.ux_element) as ArFragment

        // Cargar el modelo 3D
        /*ModelRenderable.builder()
            .setSource(requireContext(), R.raw.bee)
            .build()
            .thenAccept { renderable ->
                // Crear un nodo de anclaje para el modelo y agregarlo al escenario
                val anchorNode = AnchorNode()
                anchorNode.renderable = renderable
                arFragment.arSceneView.scene.addChild(anchorNode)
            }
            .exceptionally { throwable ->
                // Manejar cualquier error al cargar el modelo
                throwable.printStackTrace()
                null
            }*/

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}