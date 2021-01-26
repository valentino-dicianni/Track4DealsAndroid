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
import com.example.track4deals.data.models.ServerResponseUser
import com.example.track4deals.internal.ScopedFragment
import com.example.track4deals.internal.UserProvider
import com.example.track4deals.ui.login.LoginFragment
import com.example.track4deals.ui.offers.recyclerView.OnProductListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
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
    private lateinit var groupAdapter: GroupAdapter<ViewHolder>


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
        val navController = findNavController()
        val formFieldList: List<EditText> = listOf(name_profile, email_profile, phone_profile)
        val mapOfFields = createEditTextListenersMap(formFieldList)




        disableAllTextField(formFieldList)

        if (userProvider.isLoggedIn()) {
            name_profile.setText(userProvider.getUserName())
            fullname_field.text = userProvider.getUserName()
            email_field.text = userProvider.getEmail()
            email_profile.setText(userProvider.getEmail())
            phone_profile.setText(userProvider.getPhoneNumber())
            item_tracked_label.text = userProvider.getNumTracking().toString()
        } else {
            navController.navigate(R.id.navigation_login)
        }

        modify_profile_btn.setOnClickListener {
            enableAllTextField(mapOfFields)

            //TODO API communication
            modify_profile_btn.text = getString(R.string.Save)
        }

        change_password_btn.setOnClickListener {
            parentFragmentManager.apply {
                beginTransaction()
                    .replace(R.id.nav_host_fragment, ChangePasswordFragment.newInstance())
                    .addToBackStack(ChangePasswordFragment.TAG)
                    .commit()
            }
        }

        bindUI(this)

    }

    private fun bindUI(frag: Fragment) = launch(Dispatchers.Main) {

        if (userProvider.isLoggedIn()) {
            // NavHostFragment.findNavController(frag).navigate(R.id.action_navigation_profile_to_navigation_login)


            if (frag.view != null) {

                val user = viewModel.user.await()
                user.observe(viewLifecycleOwner, Observer {
                    if (it == null) return@Observer // gestrire null
                    if (it.response != null)
                        makeText(context, "UserID:" + it.response.user_id, Toast.LENGTH_LONG).show()
                })
            }
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