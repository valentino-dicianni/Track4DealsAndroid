package com.example.track4deals.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.track4deals.R
import com.example.track4deals.internal.UserProvider
import com.google.firebase.auth.FirebaseAuth
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


class SettingsFragment : PreferenceFragmentCompat(), KodeinAware {
    override val kodein by closestKodein()
    private val userProvider by instance<UserProvider>()
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var manager: PreferenceManager

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        if (preference != null) {
            if( preference.key == context?.getString(R.string.logoutDesc)) {
                FirebaseAuth.getInstance().signOut()
                userProvider.flush()
                Toast.makeText(context, getString(R.string.logoutExecuted), Toast.LENGTH_LONG).show()
                return true
            }
            if( preference.key == context?.getString(R.string.GetToken)) {
                Log.d("TEST: ", "onPreferenceTreeClick: ${userProvider.getToken()}")
            }
        }
        return false
    }
}