package com.example.tfg_android_ra

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tfg_android_ra.databinding.FragmentSecondBinding
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference

    private lateinit var album : String
    private lateinit var historia : String
    private lateinit var otro : String
    private lateinit var web : String
    private lateinit var integrantes : Map<String, String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        database = FirebaseDatabase.getInstance().reference
        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val qrValue = arguments?.getString("QRvalue")
        binding.tvPrueba.text = qrValue

        if (!qrValue.isNullOrEmpty()){
            val ruta = qrValue.substringBefore("/")
            val numero = qrValue.substringAfterLast("/")
            Log.d("valor ruta", ruta)
            database.child(ruta).addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        album = snapshot.child("${numero}-Album").value.toString()
                        historia = snapshot.child("${numero}-Historia").value.toString()
                        integrantes = snapshot.child("${numero}-Integrantes").value as Map<String, String>
                        otro = snapshot.child("${numero}-Otro").value.toString()
                        web = snapshot.child("${numero}-Web").value.toString()
                    }else{
                        showErrorDialog()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showErrorDialog()
                }

            })
        }else{
            Log.d("ERROR CADENA", "no se ha recibido ninguna cadena")
        }

        binding.fabAlbum.setOnClickListener {
            val dialogFragment = DialogFragment1()
            dialogFragment.configurarTexto(album)
            dialogFragment.show(childFragmentManager, "Album")
        }

        binding.fabHistoria.setOnClickListener {
            val dialogFragment = DialogFragment1()
            dialogFragment.configurarTexto(historia)
            dialogFragment.show(childFragmentManager, "Historia")
        }

        binding.fabIntegrantes.setOnClickListener {
            val dialogFragment = DialogFragment2()
            dialogFragment.configurarIntegrantes(integrantes)
            dialogFragment.show(childFragmentManager, "Integrantes")
            //TODO mejorar el estilo del dialogfragment
        }

        binding.fabOtro.setOnClickListener {
            val dialogFragment = DialogFragment1()
            dialogFragment.configurarTexto(otro)
            dialogFragment.show(childFragmentManager, "Otro")
        }

        binding.ivAlbumCover.setOnClickListener {
            // Verificar que la URL no sea nula o vacía
            if (!web.isNullOrEmpty()) {

                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(web))
                // Verificar que haya una actividad que pueda manejar el Intent
                if (intent.resolveActivity(requireActivity().packageManager) != null) {
                    // Iniciar la actividad
                    startActivity(intent)
                } else {
                    // Manejar el caso en el que no haya actividad que pueda manejar el Intent
                    Toast.makeText(requireContext(), "No se pudo abrir la página web", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Manejar el caso en el que la URL sea nula o vacía
                Toast.makeText(requireContext(), "La URL de la página web está vacía", Toast.LENGTH_SHORT).show()
            }
        }



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

    private fun showErrorDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("ERROR")
            .setMessage("Se ha detectado un problema al escanear el leer los datos de Firebase, por favor salga y vuela a intenterlo mas tarde")
            .setPositiveButton("Aceptar", null)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}