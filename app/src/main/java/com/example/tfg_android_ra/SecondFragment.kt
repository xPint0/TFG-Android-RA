package com.example.tfg_android_ra

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.tfg_android_ra.databinding.FragmentSecondBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File
import java.util.Locale


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

    public var audioManager2: MediaPlayer? = null
    private var tiempoTotalCancion : Int = 0
    private val handler = Handler(Looper.getMainLooper())

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val qrValue = arguments?.getString("QRvalue")

        if (!qrValue.isNullOrEmpty()){
            val ruta = qrValue.substringBefore("/") //Separo el texto recibido del QR por la / ya que manda la ruta de la BD y tras la barra el Id del album
            val numero = qrValue.substringAfterLast("/")
            Log.d("Valor ruta", ruta)

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
            Log.d("ERROR CADENA", "No se ha recibido ninguna cadena")
        }

        binding.btInformacion.setOnClickListener {
            val dialogFragment = DialogFragment1()
            dialogFragment.configurarTexto(album)
            dialogFragment.show(childFragmentManager, "Album")
        }

        binding.btHistoria.setOnClickListener {
            val dialogFragment = DialogFragment1()
            dialogFragment.configurarTexto(historia)
            dialogFragment.show(childFragmentManager, "Historia")
        }

        binding.btIntegrantes.setOnClickListener {
            val dialogFragment = DialogFragment2()
            dialogFragment.configurarIntegrantes(integrantes, localFile)
            dialogFragment.show(childFragmentManager, "Integrantes")
            //TODO mejorar el estilo del dialogfragment
        }

        binding.btCuriosidades.setOnClickListener {
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

        binding.tvTiempo.text = timeFormat(tiempoTotalCancion)

        binding.tbPlayPause.setOnCheckedChangeListener{ _, isChecked ->
            if(isChecked){
                audioManager2?.start()
                actualizarSeekBar()
            }else{
                audioManager2?.pause()
            }
        }

        binding.seekRecorrido.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    // Actualizar la posición de reproducción por la accion del usuario (mover la barra)
                    audioManager2?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // No se necesita implementar para este caso
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // No se necesita implementar para este caso
            }
        })

        audioManager2?.setOnCompletionListener {
            // Reiniciar la posición de la reproducción del audio
            audioManager2?.seekTo(0)
            binding.tbPlayPause.isChecked = false // Desmarcar el toggle button
            binding.seekRecorrido.progress = 0 // Reiniciar la posición de la seek bar
            binding.tvTiempo.text = timeFormat(0) // Reiniciar el texto del tiempo transcurrido
        }


        binding.btPrincipal.setOnClickListener{
            if(binding.menuBotones.visibility == View.GONE) openMenu() else closeMenu()
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

    private fun openMenu() {
        binding.btPrincipal.text = "Cerrar menu"
        binding.btPrincipal.setIconResource(R.drawable.uparrow)
        binding.menuBotones.visibility = View.VISIBLE
    }

    private fun closeMenu() {
        binding.btPrincipal.text = "Desplegar menu"
        binding.btPrincipal.setIconResource(R.drawable.downarrow)
        binding.menuBotones.visibility = View.GONE
    }


    private fun actualizarSeekBar() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                val mp3player = audioManager2
                if (mp3player != null && audioManager2?.isPlaying == true) {
                    val currentPosition = audioManager2!!.currentPosition
                    binding.seekRecorrido.progress = currentPosition
                    binding.tvTiempo.text = timeFormat(currentPosition)
                }
                handler.postDelayed(this, 0)
            }
        }, 0)
    }

    private fun timeFormat(tiempoEnMilisegundos: Int): String? {
        val segundos = tiempoEnMilisegundos / 1000
        val minutos = segundos / 60
        val segundosRestantes = segundos % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutos, segundosRestantes)
    }

    private fun cargarArchivosStorage(nAlbum: String){
        progressDialog = ProgressDialog(requireContext())
        progressDialog?.setMessage("Cargando archivos...")
        progressDialog?.setCancelable(false)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)progressDialog?.window?.setBackgroundBlurRadius(Int.MAX_VALUE)
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
            audioManager2 = MediaPlayer.create(requireContext(), Uri.fromFile(localFile2))
            audioManager2?.setOnPreparedListener{
                tiempoTotalCancion = it.duration
                binding.seekRecorrido.max = tiempoTotalCancion
                binding.tvTiempo.text = timeFormat(0)
                checkAndDismissProgressDialog()
            }
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
            .setPositiveButton("Aceptar"){dialog, _ ->
                dialog.dismiss()
                findNavController().navigateUp()
            }
            .show()
    }

    private fun checkAndDismissProgressDialog() {
        if (progressDialog != null && progressDialog?.isShowing!! && localFile != null && localFile2 != null ) {
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                progressDialog?.dismiss()
            }, 620)
        }
    }

    override fun onPause() {
        super.onPause()
        if(audioManager2?.isPlaying == true){
            audioManager2?.pause()
            binding.tbPlayPause.isChecked = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        audioManager2?.release()
        borrarArchivosLocales()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        audioManager2?.release()
        borrarArchivosLocales()
        _binding = null
    }


}