package com.example.tfg_android_ra

import android.app.AlertDialog
import android.app.AlertDialog.Builder
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tfg_android_ra.databinding.FragmentSecondBinding
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import kotlin.Exception


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference

    private lateinit var album : String
    private lateinit var historia : String
    private lateinit var otro : String
    private lateinit var web : String
    private lateinit var integrantes : Map<String, String>

    private lateinit var localFile : File
    private lateinit var localFile2 : File

    private var progressDialog: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //referencia a las bases de datos de firebase
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance().reference

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val qrValue = arguments?.getString("QRvalue")
        binding.tvPrueba.text = qrValue

        if (!qrValue.isNullOrEmpty()){
            val ruta = qrValue.substringBefore("/")
            val numero = qrValue.substringAfterLast("/")
            Log.d("valor ruta", ruta)

            cargarArchivosStorage(numero)
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

                AlertDialog.Builder(requireContext()).apply {
                    setTitle("Alerta")
                    setMessage("Vas a viajar a la pagina oficial de la banda, estas seguro?")
                    //Viajar a la web oficial
                    setPositiveButton("Aceptar") { _, _ ->
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(web))
                        try {
                            it.context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(
                                requireContext(),
                                "No se pudo abrir la página web",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    //No viajar
                    setNegativeButton("Cancelar") { _, _ ->
                        Toast.makeText(requireContext(), "Operacion cancelada", Toast.LENGTH_SHORT)
                            .show()
                    }
                    show()
                }
            } else {
                // Caso en el que la URL sea nula o vacía
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

    @RequiresApi(Build.VERSION_CODES.S)
    private fun cargarArchivosStorage(nAlbum: String){
        progressDialog = ProgressDialog(requireContext())
        progressDialog?.setMessage("Cargando archivos...")
        progressDialog?.setCancelable(false)
        progressDialog?.window?.setBackgroundBlurRadius(Int.MAX_VALUE)
        progressDialog?.show()

        val referenceAlbum = storage.child("${nAlbum}-Album.jpg")
        val referencePreview = storage.child("${nAlbum}-Preview.mp3")

        localFile = File.createTempFile("albumCover", "jpg")
        localFile2 = File.createTempFile("albumPreview", "mp3")

        referenceAlbum.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.ivAlbumCover.setImageBitmap(bitmap)
            Log.d("AlbumCover", "Descarga OK")
            checkAndDismissProgressDialog()
        }.addOnFailureListener{
            Log.e("AlbumCoverError", "Descarga ERROR")
            checkAndDismissProgressDialog()
        }

        referencePreview.getFile(localFile2).addOnSuccessListener {
            Log.d("AlbumPreview", "Descarga OK")
            checkAndDismissProgressDialog()
        }.addOnFailureListener{
            Log.e("AlbumPreviewError", "Descarga ERROR")
            checkAndDismissProgressDialog()
        }
    }

    fun borrarArchivosLocales() {
        try{
            localFile.delete()
            localFile2.delete()
            Log.d("ArchivosDelete", "Delete OK")
        }catch (e : Exception){
            Log.e("DeleteError", "ERROR al borrar los archivos de firebase del dispositivo")
        }
    }

    private fun showErrorDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("ERROR")
            .setMessage("Se ha detectado un problema al escanear el leer los datos de Firebase, por favor salga y vuela a intenterlo mas tarde")
            .setPositiveButton("Aceptar", null)
            .show()
    }

    private fun checkAndDismissProgressDialog() {
        if (progressDialog != null && progressDialog?.isShowing!! && localFile != null && localFile2 != null ) {
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                progressDialog?.dismiss()
            }, 500)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        borrarArchivosLocales()
        _binding = null
    }

}