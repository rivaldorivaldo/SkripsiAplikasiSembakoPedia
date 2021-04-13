package com.rivaldomathindas.sembakopedia.base

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.rivaldomathindas.sembakopedia.utils.Connectivity
import org.jetbrains.anko.toast

open class BaseFragment : Fragment() {

    fun isConnected(): Boolean = Connectivity.isConnected(requireActivity())

    // User hasn't requested storage permission; request them to allow
    fun requestStoragePermission() {
        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        activity?.toast("Storage permission is required!")
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) {
                        token.continuePermissionRequest()
                    }
                }).check()
    }

    // Check if user has granted storage permission
    fun storagePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    // Get root database reference
    fun getDatabaseReference(): DatabaseReference = FirebaseDatabase.getInstance().reference

    // Get root firestore reference
    fun getFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    // Get FirebaseAuth instance
    fun getFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    // Get Firebase Storage reference
    fun getStorageReference(): StorageReference = FirebaseStorage.getInstance().reference

    // Get user ID
    fun getUid(): String {
        val user = FirebaseAuth.getInstance().currentUser

        return user!!.uid
    }

    // Get user token
    fun getToken(): String {
        var token:String? = null

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { p0 -> token = p0!!.token }

        return token!!
    }

}
