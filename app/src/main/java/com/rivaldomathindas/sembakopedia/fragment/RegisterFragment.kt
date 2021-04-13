package com.rivaldomathindas.sembakopedia.fragment

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.rivaldomathindas.sembakopedia.utils.AppUtils
import com.rivaldomathindas.sembakopedia.utils.AppUtils.drawableToBitmap
import com.rivaldomathindas.sembakopedia.utils.AppUtils.getColor
import com.rivaldomathindas.sembakopedia.utils.AppUtils.setDrawable
import com.rivaldomathindas.sembakopedia.utils.K
import com.mikepenz.ionicons_typeface_library.Ionicons
import kotlinx.android.synthetic.main.fragment_register.*
import org.jetbrains.anko.toast
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import com.rivaldomathindas.sembakopedia.R
import com.rivaldomathindas.sembakopedia.activity.MainActivity
import com.rivaldomathindas.sembakopedia.model.User
import com.rivaldomathindas.sembakopedia.base.BaseFragment
import com.rivaldomathindas.sembakopedia.utils.*
import com.rivaldomathindas.sembakopedia.utils.PreferenceHelper.set
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class
RegisterFragment : BaseFragment() {

    private lateinit var registerSuccessful: Bitmap
    private var imageUri: Uri? = null
    private var imageSelected = false
    private var isCreatingAccount = false
    private lateinit var prefs: SharedPreferences

    companion object {
        private const val AVATAR_REQUEST = 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val successfulIcon = setDrawable(requireActivity(), Ionicons.Icon.ion_checkmark_round, R.color.white, 25)
        registerSuccessful = drawableToBitmap(successfulIcon)
        prefs = PreferenceHelper.defaultPrefs(requireActivity())

        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerFirstname.setDrawable(setDrawable(requireActivity(), Ionicons.Icon.ion_person, R.color.secondaryText, 18))
        registerLastname.setDrawable(setDrawable(requireActivity(), Ionicons.Icon.ion_person, R.color.secondaryText, 18))
        registerPhone.setDrawable(setDrawable(requireActivity(), Ionicons.Icon.ion_android_call, R.color.secondaryText, 18))
        registerEmail.setDrawable(setDrawable(requireActivity(), Ionicons.Icon.ion_ios_email, R.color.secondaryText, 18))
        registerPassword.setDrawable(setDrawable(requireActivity(), Ionicons.Icon.ion_android_lock, R.color.secondaryText, 18))
        registerConfirmPassword.setDrawable(setDrawable(requireActivity(), Ionicons.Icon.ion_android_lock, R.color.secondaryText, 18))

        registerAvatar.setOnClickListener {
            if (!isCreatingAccount) {
                if (storagePermissionGranted()) {
                    val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(galleryIntent, AVATAR_REQUEST)
                } else {
                    requestStoragePermission()
                }
            }
        }

        registerLogin.setOnClickListener {
            if (!isCreatingAccount) {
                if (requireActivity().supportFragmentManager.backStackEntryCount > 0)
                    requireActivity().supportFragmentManager.popBackStackImmediate()
                else
                    (activity as AppCompatActivity).replaceFragment(LoginFragment(), R.id.authFrameLayout)
            } else requireActivity().toast(getString(R.string.please_wait))
        }

        registerTerms.setOnClickListener {
            if (!isCreatingAccount) {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse("https://sites.google.com/view/dankmemesapp/terms-and-conditions")
                startActivity(i)
            } else requireActivity().toast(getString(R.string.please_wait))
        }

        registerButton.setOnClickListener { signUp() }
    }

    //cek data dan membuat user baru
    private fun signUp() {
        if (!AppUtils.validated(registerFirstname, registerLastname, registerEmail, registerPassword, registerConfirmPassword)) return

        val name = "${registerFirstname.text.toString().trim()} ${registerLastname.text.toString().trim()}"
        val email = registerEmail.text.toString().trim()
        val pw = registerPassword.text.toString().trim()
        val confirmPw = registerConfirmPassword.text.toString().trim()

        //cek data
        if (pw != confirmPw) {
            registerConfirmPassword.error = getString(R.string.does_not_match_password)
            return
        }
        if (!registerCheckBox.isChecked) {
            activity?.toast(getString(R.string.please_check_the_terms_and_conditions))
            return
        }
        if (!imageSelected) {
            activity?.toast(getString(R.string.please_select_a_profile_picture))
            return
        }

        //membuat user baru
        isCreatingAccount = true
        registerButton.startAnimation()
        getFirebaseAuth().createUserWithEmailAndPassword(email, pw)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        registerButton.doneLoadingAnimation(getColor(requireActivity(), R.color.primary), registerSuccessful)
                        val user = task.result!!.user
                        updateUI(user!!)

                        prefs[K.NAME] = name
                        prefs[K.EMAIL] = email
                        prefs[K.PHONE] = registerPhone.text.toString().trim()

                    } else {
                        try {
                            throw task.exception!!
                        } catch (weakPassword: FirebaseAuthWeakPasswordException){
                            isCreatingAccount = false
                            registerButton.revertAnimation()
                            registerPassword.error = getString(R.string.please_enter_a_stronger_password)
                        } catch (userExists: FirebaseAuthUserCollisionException) {
                            isCreatingAccount = false
                            registerButton.revertAnimation()
                            activity?.toast(getString(R.string.account_already_exists))
                        } catch (malformedEmail: FirebaseAuthInvalidCredentialsException) {
                            isCreatingAccount = false
                            registerButton.revertAnimation()
                            registerEmail.error = getString(R.string.incorrect_email_format)
                        } catch (e: Exception) {
                            isCreatingAccount = false
                            registerButton.revertAnimation()
                            activity?.toast(getString(R.string.error_signing_up))
                        }
                    }
                }
    }

    //menyimpan data ke firebase
    private fun updateUI(user: FirebaseUser) {
        val id = user.uid

        val newUser = User()
        newUser.name = "${registerFirstname.text.toString().trim()} ${registerLastname.text.toString().trim()}"
        newUser.email = user.email
        newUser.dateCreated = TimeFormatter().getNormalYear(System.currentTimeMillis())
        newUser.id = id
        newUser.phone = registerPhone.text.toString().trim()

        val ref = getStorageReference().child("avatars").child(id)
        val uploadTask = ref.putFile(imageUri!!)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                throw task.exception!!
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                newUser.avatar =  task.result.toString()

                user.sendEmailVerification()
                FirebaseMessaging.getInstance().subscribeToTopic(K.TOPIC_GLOBAL)

                getFirestore().collection(K.USERS).document(id).set(newUser).addOnSuccessListener {
                    registerButton.doneLoadingAnimation(getColor(requireActivity(), R.color.primary), registerSuccessful)

                    requireActivity().toast(getString(R.string.welcome) + " ${registerFirstname.text.toString().trim()} ${registerLastname.text.toString().trim()}")
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    AppUtils.animateEnterRight(requireActivity())
                    requireActivity().finish()
                }

            } else {
                registerButton.revertAnimation()
                activity?.toast(getString(R.string.error_signing_up))
            }
        }
    }

    //ambil gambar
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AVATAR_REQUEST && resultCode == RESULT_OK) {
            data.let { imageUri = it!!.data }

            startCropActivity(imageUri!!)
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                imageSelected = true
                val resultUri = result.uri

                registerAvatar?.loadUrl(resultUri.toString())
                imageUri = resultUri

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
               activity?.toast(getString(R.string.cropping_error))
            }
        }

    }

    //crop pada gambar
    private fun startCropActivity(imageUri: Uri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(requireContext(), this)
    }

    //menon-aktifkan tombol back ketika sedang membuat akun
    fun backPressOkay(): Boolean = !isCreatingAccount

    //memodifikasi fungsi dari onDestroy
    override fun onDestroy() {
        if (registerButton != null) registerButton.dispose()
        super.onDestroy()
    }

}
