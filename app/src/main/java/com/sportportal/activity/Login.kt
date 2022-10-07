package com.sportportal.activity

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.sportportal.databinding.ActivityLoginBinding

@Suppress("DEPRECATION")
class Login : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var email : String
    private lateinit var password : String
    private lateinit var googleSignInClient: GoogleSignInClient
    private var db = FirebaseFirestore.getInstance()

    companion object{
        private const val TAG = "___TEST___"
        private const val RC_SIGN_IN = 100
        lateinit var user : FirebaseAuth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        user = auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("114288704775-rcteduhnl1njc6rkphs3qoum6ociu4p4.apps.googleusercontent.com")
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnSignIn.setOnClickListener{
            email = binding.edtEmail.text.toString()
            password = binding.edtPassword.text.toString()
            signInDefault()
        }
        binding.btnSignInGoogle.setOnClickListener {
            Snackbar.make(
                binding.root,
                "Processing, Please Wait",
                Snackbar.LENGTH_SHORT
            )
                .apply {

                    show()
                }
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        binding.tvSignUp.setOnClickListener{
            moveToSignUp()
        }

        binding.tvReset.setOnClickListener {
            moveToResetPassword()
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(
                    baseContext, "Google sign in failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    user = auth
                    isSignedUp(auth)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext, "Failed",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in.
        if (auth.currentUser != null) {
            moveToNext()
        }
    }

    private fun signInDefault() {

        email = binding.edtEmail.text.toString()
        password = binding.edtPassword.text.toString()

        when {

            TextUtils.isEmpty(email) -> {
                binding.edtEmail.error = "Email harus diisi"
                binding.edtEmail.requestFocus()
            }

            !(Patterns.EMAIL_ADDRESS.matcher(email).matches()) ->{
                binding.edtEmail.error = "Email tidak valid"
                binding.edtEmail.requestFocus()
            }

            TextUtils.isEmpty(password) -> {
                binding.edtPassword.error = "Password harus diisi"
                binding.edtPassword.requestFocus()
            }
            else -> {
                Snackbar.make(
                    binding.root,
                    "Processing, Please Wait",
                    Snackbar.LENGTH_SHORT
                )
                    .apply {

                        show()
                    }
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(
                                baseContext, "Masuk",
                                Toast.LENGTH_SHORT
                            ).show()
                            user = auth
                            moveToNext()
                        } else {
                            // If sign in fails, display a message to the user.
                            Snackbar.make(
                                binding.root,
                                "Email atau Password Salah",
                                Snackbar.LENGTH_SHORT
                            )
                                .apply {

                                    show()
                                }
                        }
                    }

            }
        }
    }

    private fun isSignedUp(user : FirebaseAuth){
        val docRef = db.collection("users_extra").document(user.uid.toString())
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    moveToNext()
                } else {
                    Log.d(TAG, "No such document")
                    storeUserData(user)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
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
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully written!")
                moveToNext()
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error writing document", e) }
    }

    private fun moveToSignUp() {
        startActivity(Intent(baseContext, SignUp::class.java))
    }

    private fun moveToResetPassword() {
        startActivity(Intent(baseContext, Password::class.java))
    }

    private fun moveToNext() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        //Cek apakah sudah verif email atau belum
        if(!auth.currentUser!!.isEmailVerified){
            startActivity(Intent(baseContext, EmailVerification::class.java))
        }
        else{
            val docRef = db.collection("users_extra").document(auth.uid.toString())
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        when (document.data?.get("role") as String){
                            "customer" -> {
                                startActivity(Intent(baseContext, CustomerBeranda::class.java))
                            }

                            "penyedia" -> {
                                startActivity(Intent(baseContext, PenyediaLapanganList::class.java))
                            }

                            "admin" -> {
                                startActivity(Intent(baseContext, AdminLapanganList::class.java))
                            }
                        }
                    } else {
                        //Snackbar login gagal
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }
        }
    }

}