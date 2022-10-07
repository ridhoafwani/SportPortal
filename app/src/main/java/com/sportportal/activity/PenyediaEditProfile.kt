package com.sportportal.activity

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sportportal.R
import com.sportportal.databinding.ActivityEditProfileBinding
import com.sportportal.databinding.DialogInfoBinding

@Suppress("DEPRECATION")
class PenyediaEditProfile : AppCompatActivity() {

    private lateinit var binding : ActivityEditProfileBinding
    private val auth : FirebaseAuth get() = Login.user
    private var GALLERY_REQUEST_CODE = 71
    private var imageUrl = ""
    private var fiileUrl = ""
    private lateinit var progressDialog : ProgressDialog
    private val db = FirebaseFirestore.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Edit Profile"

        val defaultImageUrl = auth.currentUser?.photoUrl

        Glide.with(binding.imgProfilePhoto)
            .load(defaultImageUrl)
            .placeholder(R.drawable.profile_man)
            .into(binding.imgProfilePhoto)

        binding.edtEmail.setText(auth.currentUser?.email)
        binding.edtName.setText(auth.currentUser?.displayName)
        binding.btnChangeImage.setOnClickListener {
            selectImageFromGallery()
        }

        binding.btnSave.setOnClickListener {

            progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Memperbarui Profile")
            progressDialog.setMessage("Loading ... ")
            progressDialog.show()

            if(fiileUrl!=""){
                uploadImageToFirebase(fiileUrl.toUri())
            }else{
                updateFirebaseUser()
            }
        }
    }

    private fun selectImageFromGallery() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Please select..."
            ),
            GALLERY_REQUEST_CODE
        )
    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (requestCode == GALLERY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK
            && data != null
            && data.data != null
        ) {

            // Get the Uri of data
            val file_uri = data.data
            if (file_uri != null) {
                fiileUrl = file_uri.toString()
                Glide.with(binding.imgProfilePhoto)
                    .load(file_uri)
                    .placeholder(R.drawable.profile_man)
                    .into(binding.imgProfilePhoto)
            }
        }
    }

    private fun uploadImageToFirebase(fileUri: Uri) {
        val fileName = auth.currentUser?.uid.toString() +".jpg"

        val refStorage = FirebaseStorage.getInstance().reference.child("profilepict/$fileName")

        refStorage.putFile(fileUri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                    imageUrl = it.toString()
                    updateFirebaseUser()
                }
            }

            .addOnFailureListener { e ->
                print(e.message)
            }
    }

    private fun updateFirebaseUser(){

        if(imageUrl == "" && binding.edtName.text.toString() == auth.currentUser?.displayName){
            Snackbar.make(
                binding.root,
                "Sepertinya Data Tidak Berubah",
                Snackbar.LENGTH_LONG
            )
                .apply {
                    progressDialog.dismiss()
                    show()
                }

        }
        else if (imageUrl != "" && binding.edtName.text.toString() != auth.currentUser?.displayName){
            val profileUpdates = userProfileChangeRequest {
                displayName = binding.edtName.text.toString()
                photoUri = Uri.parse(imageUrl)
            }
            auth.currentUser!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User profile updated.")
                        progressDialog.dismiss()
                        updateUserExtra()
                        getPenarikan()
                    }
                }
        }
        else if(binding.edtName.text.toString() == auth.currentUser?.displayName){
            val profileUpdates = userProfileChangeRequest {
                photoUri = Uri.parse(imageUrl)
            }
            auth.currentUser!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User profile updated.")
                        progressDialog.dismiss()
                        updateUserExtra()
                    }
                }
        }
        else{
            val profileUpdates = userProfileChangeRequest {
                displayName = binding.edtName.text.toString()
            }
            auth.currentUser!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User profile updated.")
                        progressDialog.dismiss()
                        updateUserExtra()
                        getPenarikan()
                    }
                }
        }

    }

    private fun updateUserExtra(){
        val data = hashMapOf(
            "name" to auth.currentUser?.displayName,
            "image" to auth.currentUser?.photoUrl
        )
        val docRef = db.collection("users_extra").document(auth.uid.toString())
        docRef
            .update(data as Map<String, Any>)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
                showCustomDialog()
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
    }

    private fun goToProfile(){
        val intent = Intent(baseContext,PenyediaProfile::class.java)
        startActivity(intent)
        finish()
    }

    private fun showCustomDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        val dialogInfoBinding = DialogInfoBinding.inflate(layoutInflater)
        dialog.setContentView(dialogInfoBinding.root)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        dialogInfoBinding.title.text = "Profile Diupdate"
        dialogInfoBinding.content.text = "Data profile anda berhasil diperbarui, silahkan cek pada menu profile"
        dialogInfoBinding.btClose.text = "Profile"

        dialogInfoBinding.btClose.setOnClickListener {
            goToProfile()
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.attributes = lp

        Handler().postDelayed({
            dialog.dismiss()
            goToProfile()
        }, 3000)
    }

    private fun getPenarikan(){
        db.collection("penarikan")
            .whereEqualTo("idPenarik", auth.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    updateNamaPenarik(document.id)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun updateNamaPenarik(idPenarikan : String){
        val docRef = db.collection("penarikan").document(idPenarikan)
        docRef
            .update("namaPenarik", auth.currentUser?.displayName)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")

            }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
    }
}