package com.example.track4deals.ui.profile

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.track4deals.R
import com.example.track4deals.ui.login.LoginViewModelFactory
import kotlinx.android.synthetic.main.change_password_fragment.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class ChangePasswordFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private val profileViewModelFactory: ProfileViewModelFactory by instance()

    companion object {
        fun newInstance() = ChangePasswordFragment()
        const val TAG = "ChangePasswordFragment"
    }

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.change_password_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, profileViewModelFactory).get(ProfileViewModel::class.java)
        // TODO: Use the ViewModel
    }

}