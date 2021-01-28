package com.example.track4deals.ui.profile

import android.os.Bundle
import android.text.method.KeyListener
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.track4deals.R
import com.example.track4deals.data.database.entity.ProductEntity
import com.example.track4deals.data.models.ServerResponseUser
import com.example.track4deals.internal.ScopedFragment
import com.example.track4deals.internal.UserProvider
import com.example.track4deals.ui.login.LoginFragment
import com.example.track4deals.ui.offers.recyclerView.OnProductListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserInfo
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_offers.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class ProfileFragment : ScopedFragment(), KodeinAware {
    override val kodein by closestKodein()
    private val profileViewModelFactory: ProfileViewModelFactory by instance()
    private val userProvider: UserProvider by instance()


    companion object {
        fun newInstance() = ProfileFragment()
        const val TAG = "ProfileFragment"

    }

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(this, profileViewModelFactory).get(ProfileViewModel::class.java)
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val formFieldList: List<EditText> = listOf(name_profile, email_profile)
        val mapOfFields = createEditTextListenersMap(formFieldList)


        disableAllTextField(formFieldList)
        requestUser()

        modify_profile_btn.setOnClickListener {

            when (modify_profile_btn.text) {

                getString(R.string.edit_button_it) -> {
                    enableAllTextField(mapOfFields)
                    modify_profile_btn.text = getString(R.string.Save)
                }

                getString(R.string.Save) -> {
                    disableAllTextField(formFieldList)
                    if(name_profile.text.toString()!=userProvider.getUserName())
                         viewModel.modifyUsername(name_profile.text.toString())
                    if(email_profile.text.toString()!=userProvider.getEmail())
                        viewModel.modifyEmail(email_profile.text.toString())

                    modify_profile_btn.text = getString(R.string.edit_button_it)
                }

            }

        }

        change_password_btn.setOnClickListener {
            parentFragmentManager.apply {
                beginTransaction()
                    .replace(R.id.nav_host_fragment, ChangePasswordFragment.newInstance())
                    .addToBackStack(ChangePasswordFragment.TAG)
                    .commit()
            }
        }

        userProvider.loadingComplete.observe(viewLifecycleOwner, Observer {
            // Bind recycler view
            bindUI()
        })


        viewModel.updateUsernameRes.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            makeText(context, "Operazione:" + it, Toast.LENGTH_LONG).show()
            requestUser()

        })


    }

    private fun requestUser() = launch(Dispatchers.Main) {

        if (userProvider.isLoggedIn()) {

                val user = viewModel.user.await()
                user.observe(viewLifecycleOwner, Observer {
                    if (it == null) return@Observer
                    if (it.response != null)
                        makeText(context, "UserID:" + it.response.user_id, Toast.LENGTH_LONG).show()
                })

        }
    }


    private fun bindUI(){
        val navController = findNavController()

        if (userProvider.isLoggedIn()) {
            name_profile.setText(userProvider.getUserName())
            fullname_field.text = userProvider.getUserName()
            email_field.text = userProvider.getEmail()
            email_profile.setText(userProvider.getEmail())
            item_tracked_label.text = userProvider.getNumTracking().toString()
        } else {
            navController.navigate(R.id.navigation_login)
        }
    }



    //INPUT: Map of  EditText component and respective key listener to be enabled
    //OUTPUT: none
    //Makes text view focusable and editable
    private fun enableAllTextField(fieldsMap: MutableMap<EditText, KeyListener>) {

        fieldsMap.forEach {
            it.key.keyListener = it.value
            it.key.isFocusableInTouchMode = true
        }
    }

    //INPUT: EditText component to be disabled
    //OUTPUT: none
    //Makes text view not focusable and editable anymore
    private fun disableAllTextField(fields: List<EditText>) {

        fields.forEach {
            it.keyListener = null
            it.isFocusableInTouchMode = false
        }
    }

    //INPUT: List of EditText
    //OUTPUT: Map of EditText fields paired with respective KeyListener
    //Bound each EditText fields with respective key listeners
    private fun createEditTextListenersMap(fields: List<EditText>): MutableMap<EditText, KeyListener> {
        val listeners = mutableMapOf<EditText, KeyListener>()
        fields.forEach {
            listeners[it] = it.keyListener
        }

        return listeners
    }



}