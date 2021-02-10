package com.example.track4deals.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.KeyListener
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.track4deals.R
import com.example.track4deals.data.constants.AppConstants.Companion.RC_UPDATE_IMG
import com.example.track4deals.internal.UserProvider
import com.example.track4deals.ui.login.LoginFragment
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.element_row_rv.view.*
import kotlinx.android.synthetic.main.fragment_profile.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance

class ProfileFragment : Fragment(), KodeinAware {
    override val kodein by closestKodein()
    private val profileViewModelFactory: ProfileViewModelFactory by instance()
    private val userProvider: UserProvider by instance()
    private var imageUri: Uri? = null
    private var storageRef: StorageReference? = null

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
        storageRef = FirebaseStorage.getInstance().reference.child("UserPictures")
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val formFieldList: List<EditText> = listOf(name_profile, email_profile)
        val mapOfFields = createEditTextListenersMap(formFieldList)

        disableAllTextField(formFieldList)

        //UI LISTENERS
        modify_profile_btn.setOnClickListener {
            when (modify_profile_btn.text) {

                getString(R.string.edit_button_it) -> {
                    enableAllTextField(mapOfFields)
                    modify_profile_btn.text = getString(R.string.Save)
                }

                getString(R.string.Save) -> {
                    disableAllTextField(formFieldList)
                    if (name_profile.text.toString() != userProvider.getUserName())
                        viewModel.updateUsername(name_profile.text.toString())
                    if (email_profile.text.toString() != userProvider.getEmail()) {
                        viewModel.emailDialogNeeded()
                        PasswordConfirmationDialogFragment().show(
                            parentFragmentManager,
                            PasswordConfirmationDialogFragment.TAG
                        )
                        viewModel.modifyEmail(email_profile.text.toString())
                    }
                    modify_profile_btn.text = getString(R.string.edit_button_it)
                }
            }
        }

        change_password_btn.setOnClickListener {
            ChangePasswordDialogFragment().show(
                parentFragmentManager,
                ChangePasswordDialogFragment.TAG
            )
        }


        delete_btn.setOnClickListener {
            viewModel.deleteDialogNeeded()
            PasswordConfirmationDialogFragment().show(
                parentFragmentManager,
                PasswordConfirmationDialogFragment.TAG
            )
            viewModel.delete(true)
        }

        edit_image_icon.setOnClickListener {
            onClickImage()
        }


        //VIEWMODEL RESULT LISTENER

        userProvider.loadingComplete.observe(viewLifecycleOwner, {
            bindUI()
        })


        viewModel.usernameChangeRes.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            if (it.status) {
                bindUI()
                makeText(
                    context,
                    getString(R.string.username_change_success),
                    Toast.LENGTH_LONG
                ).show()
            } else
                makeText(context, getString(R.string.generic_error), Toast.LENGTH_LONG).show()
        })



        viewModel.updateEmailResult.observe(viewLifecycleOwner, Observer { res ->
            if (res == null) return@Observer
            res.observe(viewLifecycleOwner, {
                if (it != null) {

                    if (it.status) {
                        bindUI()
                        makeText(
                            context,
                            getString(R.string.email_change_success),
                            Toast.LENGTH_LONG
                        ).show()
                    } else
                        makeText(
                            context,
                            getString(R.string.generic_error) + " ${it.message}",
                            Toast.LENGTH_LONG
                        ).show()
                }
            })
        })

        viewModel.deleteResult.observe(viewLifecycleOwner, Observer { res ->
            if (res == null) return@Observer
            res.observe(viewLifecycleOwner, {
                if (it != null) {

                    if (it.status) {
                        makeText(
                            context,
                            getString(R.string.user_delete_success),
                            Toast.LENGTH_LONG
                        ).show()
                        FirebaseAuth.getInstance().signOut()
                        userProvider.flush()
                        navigateLogin()
                    } else
                        makeText(
                            context,
                            getString(R.string.generic_error) + " ${it.message}",
                            Toast.LENGTH_LONG
                        ).show()
                }
            })
        })

        viewModel.passwordChangeRes.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer

            if (it.status) {
                makeText(
                    context,
                    getString(R.string.password_change_success),
                    Toast.LENGTH_LONG
                ).show()
            } else
                makeText(
                    context,
                    getString(R.string.generic_error) + " ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
        })

        viewModel.pictureChangeRes.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer

            if (it.status) {
                makeText(
                    context,
                    getString(R.string.picture_change_success),
                    Toast.LENGTH_LONG
                ).show()
            } else
                makeText(
                    context,
                    getString(R.string.generic_error) + " ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
        })
    }


    private fun bindUI() {
        val navController = findNavController()
        if (userProvider.isLoggedIn()) {
            setProfileImage(userProvider.getProfilePic().toString())
            name_profile.setText(userProvider.getUserName())
            fullname_field.text = userProvider.getUserName()
            email_field.text = userProvider.getEmail()
            email_profile.setText(userProvider.getEmail())
            item_tracked_label.text = userProvider.getNumTracking().toString()
            group_loading.visibility = View.GONE
            group_uploading.visibility = View.GONE
            groupProfile.visibility = View.VISIBLE
        } else {
            navController.navigate(R.id.navigation_login)
        }
    }

    private fun onClickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RC_UPDATE_IMG)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_UPDATE_IMG && resultCode == Activity.RESULT_OK && data!!.data != null) {
            imageUri = data.data
            uploadImageToDatabase()
        } else {
            makeText(context, getString(R.string.image_uploading_error), Toast.LENGTH_LONG).show()
        }
    }

    private fun uploadImageToDatabase() {

        //Show the progressBar
        groupProfile.visibility = View.GONE
        group_uploading.visibility = View.VISIBLE

        if (imageUri != null) {
            val fileRef = storageRef!!.child(userProvider.getUserName() + ".jpg")

            val uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                //if the task is done change the picture url through viewModel
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    if (downloadUrl != null) {
                        viewModel.updatePicture(downloadUrl)
                    }
                    setProfileImage(downloadUrl.toString())
                }

                group_uploading.visibility = View.GONE
                groupProfile.visibility = View.VISIBLE
            }


            //Updating the percentage on the progressBar
            uploadTask.addOnProgressListener {
                progressBarHorizontal.max = it.totalByteCount.toInt()
                progressBarHorizontal.progress = it.bytesTransferred.toInt()
                textViewHorizontalProgress.text = getString(
                    R.string.upload_percentage,
                    (it.bytesTransferred / 1000).toInt(),
                    (it.totalByteCount / 1000).toInt()
                )
            }

        }
    }


    private fun setProfileImage(profilePhoto: String) {
        val requestOptions = RequestOptions()
            .placeholder(R.drawable.ic_user_profile)
            .error(R.drawable.ic_user_profile)

        Glide.with(this)
            .applyDefaultRequestOptions(requestOptions)
            .load(profilePhoto)
            .circleCrop()
            .into(profile_image)
    }


    /**
     * Makes text view focusable and editable
     * @param fieldsMap Map of EditText component to be enabled
     */
    private fun enableAllTextField(fieldsMap: MutableMap<EditText, KeyListener>) {
        fieldsMap.forEach {
            it.key.keyListener = it.value
            it.key.isFocusableInTouchMode = true
        }
    }


    /**
     * Makes text view not focusable and editable anymore
     * @param fields EditText component to be disabled
     */
    private fun disableAllTextField(fields: List<EditText>) {
        fields.forEach {
            it.keyListener = null
            it.isFocusableInTouchMode = false
        }
    }


    /**
     * Bound each EditText fields with respective key listeners
     * @param fields List of EditText
     * @return Map of EditText fields paired with respective KeyListener
     */
    private fun createEditTextListenersMap(fields: List<EditText>): MutableMap<EditText, KeyListener> {
        val listeners = mutableMapOf<EditText, KeyListener>()
        fields.forEach {
            listeners[it] = it.keyListener
        }
        return listeners
    }

    private fun navigateLogin() {
        parentFragmentManager.apply {
            beginTransaction()
                .replace(R.id.nav_host_fragment, LoginFragment())
                .addToBackStack(LoginFragment.TAG)
                .commit()
        }
    }


}