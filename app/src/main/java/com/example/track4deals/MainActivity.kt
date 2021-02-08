package com.example.track4deals

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseApp
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein

class MainActivity : AppCompatActivity(), KodeinAware {
    override val kodein by closestKodein()

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)
        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(this)
        setSavedDarkMode(sharedPreference)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_offers,
                R.id.navigation_track,
                R.id.navigation_settings,
                R.id.navigation_profile,
                R.id.navigation_login
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


    }

    /**
     * Check if the User specified what theme is preferred otherwise use the default theme of the system
     * @param sharedPref SharedPreference instance for retrieving saved state
     */
    private fun setSavedDarkMode(sharedPref : SharedPreferences) {
        val isDark = sharedPref.getBoolean(
            getString(R.string.darkTheme_preference), false
        )

        //if is the first time use the user current theme settings
        if (isFirstBoot(sharedPref)) {
            if (this.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
            ) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                with(sharedPref.edit()) {
                    putBoolean(getString(R.string.darkTheme_preference), true)
                    apply()
                }
            } else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else { //if not check the user saved preference

            if (isDark)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    /**
     * Check if the App is statded for the first time
     * @param sharedPref SharedPreference instance for retrieving saved state
     * @return true if the app is started for the first time, false otherwise
     */
    private fun isFirstBoot(sharedPref : SharedPreferences): Boolean {
        val isFirstBoot = sharedPref.getBoolean(getString(R.string.first_boot), true)
        return if (isFirstBoot) {
            with(sharedPref.edit()) {
                putBoolean(getString(R.string.first_boot), false)
                apply()
            }

            true
        } else false

    }


}