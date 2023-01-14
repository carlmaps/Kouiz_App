package com.example.kouizapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import androidx.navigation.ui.NavigationUI
import androidx.room.Room
import com.example.kouizapp.R
import com.example.kouizapp.database.KouizDB
import com.example.kouizapp.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    // Declare Navigation Controller Object
    lateinit var mnavController: NavController
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        val appBarConfiguration = AppBarConfiguration.Builder(
            R.id.homeFragment, R.id.questionsFragment, R.id.resultFragment, R.id.resultAnalysisFragment
        ).build()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        mnavController = navHostFragment.navController
        // Code to link the navigation controller to the app bar
        NavigationUI.setupActionBarWithNavController(this,mnavController, appBarConfiguration)
    }
}