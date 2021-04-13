package com.rivaldomathindas.sembakopedia.fragment

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.rivaldomathindas.sembakopedia.utils.AppUtils
import com.rivaldomathindas.sembakopedia.utils.AppUtils.setDrawable
import com.rivaldomathindas.sembakopedia.utils.K
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import com.mikepenz.ionicons_typeface_library.Ionicons
import kotlinx.android.synthetic.main.fragment_login.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import com.rivaldomathindas.sembakopedia.utils.PreferenceHelper.set
import com.rivaldomathindas.sembakopedia.R
import com.rivaldomathindas.sembakopedia.activity.MainActivity
import com.rivaldomathindas.sembakopedia.model.User
import com.rivaldomathindas.sembakopedia.base.BaseFragment
import com.rivaldomathindas.sembakopedia.utils.PreferenceHelper
import com.rivaldomathindas.sembakopedia.utils.replaceFragment
import com.rivaldomathindas.sembakopedia.utils.setDrawable

class LoginFragment : BaseFragment() {

    private var isLoggingIn = false
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        prefs = PreferenceHelper.defaultPrefs(requireActivity())

        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginEmail.setDrawable(setDrawable(requireActivity(), Ionicons.Icon.ion_ios_email, R.color.secondaryText, 18))
        loginPassword.setDrawable(setDrawable(requireActivity(), Ionicons.Icon.ion_android_lock, R.color.secondaryText, 18))

        loginRegister.setOnClickListener {
            if (!isLoggingIn)
                (activity as AppCompatActivity).replaceFragment(RegisterFragment(), R.id.authFrameLayout)
            else
                requireActivity().toast(getString(R.string.please_wait))
        }

        loginButton.setOnClickListener { login() }
        loginForgotPassword.setOnClickListener { if (!isLoggingIn) forgotPassword() else requireActivity().toast(getString(R.string.please_wait))}
    }

    //Login
    private fun login() {
        if (!AppUtils.validated(loginEmail, loginPassword)) return

        val email = loginEmail.text.toString().trim()
        val pw = loginPassword.text.toString().trim()

        isLoggingIn = true
        loginButton.startAnimation()
        getFirebaseAuth().signInWithEmailAndPassword(email, pw)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        val user = task.result!!.user
                        updateUI(user!!)
                    } else {
                        try {
                            throw task.exception!!
                        } catch (wrongPassword: FirebaseAuthInvalidCredentialsException) {
                            isLoggingIn = false
                            loginButton.revertAnimation()
                            loginPassword.error = getString(R.string.password_incorrect)

                        } catch (userNull: FirebaseAuthInvalidUserException) {
                            isLoggingIn = false
                            loginButton.revertAnimation()
                            activity?.toast(getString(R.string.account_not_found))

                        } catch (e: Exception) {
                            isLoggingIn = false
                            loginButton.revertAnimation()
                            activity?.toast(getString(R.string.error_signing_in))
                        }
                    }
                }

    }

    //Lupa password
    private fun forgotPassword() {
        if (!AppUtils.validated(loginEmail)) return

        val email = loginEmail.text.toString().trim()

        activity?.alert(getString(R.string.alert_forgot_password) + " " + email) {
            title = getString(R.string.forgot_password)

            positiveButton(getString(R.string.send_email)) {

                getFirebaseAuth().sendPasswordResetEmail(email)
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                activity?.toast(getString(R.string.email_sent))
                            } else {
                                try {
                                    throw task.exception!!
                                } catch (malformedEmail: FirebaseAuthInvalidCredentialsException) {
                                    loginEmail.error = getString(R.string.incorrect_email_format)
                                    activity?.toast(getString(R.string.email_not_sent))
                                } catch (e: Exception) {
                                    activity?.toast(getString(R.string.email_not_sent))
                                }
                            }
                        }
            }

            negativeButton("CANCEL") {}
        }!!.show()
    }

    //Data user setelah login
    private fun updateUI(user: FirebaseUser) {
        FirebaseMessaging.getInstance().subscribeToTopic(K.TOPIC_GLOBAL)
        val ref = getFirestore().collection(K.USERS).document(user.uid)

        ref.get().addOnCompleteListener {
            if (it.isSuccessful) {
                val userObject = it.result!!.toObject(User::class.java)

                prefs[K.NAME] = userObject!!.name
                prefs[K.EMAIL] = userObject.email
                prefs[K.PHONE] = userObject.phone

                Handler().postDelayed({
                    requireActivity().toast(getString(R.string.welcome_back) + " " + userObject.name)

                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().overridePendingTransition(R.anim.enter_b, R.anim.exit_a)
                    requireActivity().finish()
                }, 400)

            } else {
                activity?.toast(getString(R.string.fetching_user_data_failed) + " " + it.exception)
            }
        }


    }

    //menon-aktifkan tombol back ketika sedang login
    fun backPressOkay(): Boolean = !isLoggingIn

    //memodifikasi fungsi dari onDestroy
    override fun onDestroy() {
        if (loginButton != null) loginButton.dispose()
        super.onDestroy()
    }

}
