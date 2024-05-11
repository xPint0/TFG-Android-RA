package com.example.tfg_android_ra

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.example.tfg_android_ra.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        val fragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
        if (fragment is SecondFragment){
            fragment.borrarArchivosLocales()
            fragment.audioManager2?.release()
        }
        if (fragment is FirstFragment){
            fragment.audioManager?.release()
            fragment.view?.findViewById<ImageView>(R.id.iv_vinyl)?.clearAnimation()
        }

    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        val fragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
        if (fragment is FirstFragment){
            fragment.audioManager?.pause()
            fragment.view?.findViewById<ImageView>(R.id.iv_vinyl)?.clearAnimation()
        }
        if (fragment is SecondFragment) fragment.audioManager2?.pause()
    }

    override fun onResume() {
        super.onResume()
        val fragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main)
        if (fragment is FirstFragment){
            fragment.audioManager?.start()
            fragment.view?.findViewById<ImageView>(R.id.iv_vinyl)?.startAnimation(AnimationUtils.loadAnimation(fragment.context, R.anim.rotate_animation))
        }
        if (fragment is SecondFragment) fragment.audioManager2?.start()
    }

}