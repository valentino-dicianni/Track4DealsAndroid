package com.example.track4deals.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.track4deals.R
import com.example.track4deals.data.repository.ProductRepository
import com.example.track4deals.internal.UserProvider
import com.google.firebase.auth.FirebaseAuth
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class SettingsFragment() : PreferenceFragmentCompat(), KodeinAware {
    override val kodein by closestKodein()
    private val userProvider by instance<UserProvider>()
    private val settingsViewModelFactory: SettingsViewModelFactory by instance()
    private lateinit var settingsViewModel: SettingsViewModel
    private val onSettingChangeListener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences: SharedPreferences, key: String ->

            when (key) {
                getString(R.string.darkTheme_preference) -> {
                    if (sharedPreferences.getBoolean(
                            getString(R.string.darkTheme_preference),
                            false
                        )
                    ) {

                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                        Toast.makeText(
                            context,
                            getString(R.string.enableDarkTheme),
                            Toast.LENGTH_LONG
                        ).show()

                    } else {

                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                        Toast.makeText(
                            context,
                            getString(R.string.enableLightTheme),
                            Toast.LENGTH_LONG
                        ).show()

                    }
                }

                getString(R.string.notification_preference) -> {
                    if (sharedPreferences.getBoolean(
                            getString(R.string.notification_preference),
                            false
                        )
                    ) {
                        Toast.makeText(
                            context,
                            getString(R.string.notificationsON),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            getString(R.string.notificationsOFF),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }

            }


        }


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsViewModel =
            ViewModelProvider(this, settingsViewModelFactory).get(SettingsViewModel::class.java)
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(
            onSettingChangeListener
        )

        val logOutPref =  preferenceManager.findPreference<Preference>(getString(R.string.logoutDesc))

        logOutPref?.isEnabled = userProvider.isLoggedIn()


    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {

        if (preference != null) {
            if (preference.key == context?.getString(R.string.logoutDesc)) {
                FirebaseAuth.getInstance().signOut()
                userProvider.flush()
                settingsViewModel.resetTracking()

                preference.isEnabled = false

                Toast.makeText(context, getString(R.string.logoutExecuted), Toast.LENGTH_LONG)
                    .show()
                return true
            }
        }

        return false
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(
            onSettingChangeListener
        )
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(
            onSettingChangeListener
        )
    }


}