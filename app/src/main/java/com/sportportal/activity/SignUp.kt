package com.sportportal.activity

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.sportportal.databinding.ActivitySignUpBinding

class SignUp : AppCompatActivity(), View.OnClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding : ActivitySignUpBinding
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        binding.btnSignUp.setOnClickListener(this)
        binding.tvSignIn.setOnClickListener(this)
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            Login.user = auth
            moveToLogin()
        }
    }

    override fun onClick(v: View?) {
        when(v){
            binding.btnSignUp -> register()
            binding.tvSignIn -> moveToLogin()
        }
    }

    fun register(){
        val name = binding.edtName.text.toString()
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        when{
            TextUtils.isEmpty(name) -> {
                binding.edtName.setError("Name cannot empty")
                binding.edtName.requestFocus()
            }

            TextUtils.isEmpty(email) -> {
                binding.edtEmail.setError("Name cannot empty")
                binding.edtEmail.requestFocus()
            }

            !(Patterns.EMAIL_ADDRESS.matcher(email).matches()) ->{
                binding.edtEmail.error = "Email tidak valid"
                binding.edtEmail.requestFocus()
            }

            TextUtils.isEmpty(password) -> {
                binding.edtPassword.setError("Name cannot empty")
                binding.edtPassword.requestFocus()
            }
            else -> {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(baseContext, "Registrasi Berhasil",
                                Toast.LENGTH_SHORT).show()
                            val user = auth.currentUser
                            val profileUpdates = userProfileChangeRequest {
                                displayName = name
                            }

                            user!!.updateProfile(profileUpdates)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d(TAG, "User profile updated.")
                                        storeUserData(auth)
                                    }
                                }


                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(
                                this, "Registrasi Gagal",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }

    }

    private fun storeUserData(user : FirebaseAuth){
        val userData = hashMapOf(
            "name"  to  user.currentUser?.displayName,
            "email" to  user.currentUser?.email,
            "saldo" to  "0",
            "role"  to  "customer",
            "image" to  user.currentUser?.photoUrl
        )

        db.collection("users_extra").document(user.uid.toString())
            .set(userData)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully written!")
                moveToLogin()
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }
    }

    fun moveToLogin(){
        startActivity(Intent(baseContext, Login::class.java))
    }

}


