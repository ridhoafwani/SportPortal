package com.sportportal.activity

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.util.Patterns.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.sportportal.databinding.ActivityPasswordBinding

@Suppress("DEPRECATION")
class Password : AppCompatActivity() {

    private lateinit var binding : ActivityPasswordBinding
    private val auth : FirebaseAuth get() = Login.user
    private lateinit var email : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnReset.setOnClickListener {
            email = binding.edtEmail.text.toString()

            when {
                TextUtils.isEmpty(email) -> {
                    binding.edtEmail.error = "Email harus diisi"
                    binding.edtEmail.requestFocus()
                }

                !(EMAIL_ADDRESS.matcher(email).matches()) ->{
                    binding.edtEmail.error = "Email tidak valid"
                    binding.edtEmail.requestFocus()
                }

                else ->{
                    sendResetLink(email)
                }
            }
        }

        binding.tvLogin.setOnClickListener {
            moveToLogin()
        }
    }

    private fun sendResetLink(emailAddress : String){
        auth.setLanguageCode("id")
        auth.sendPasswordResetEmail(emailAddress)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")
                    Snackbar.make(
                        binding.root,
                        "Email Terkirim",
                        Snackbar.LENGTH_SHORT
                    )
                        .apply {

                            show()
                        }

                    Handler().postDelayed({
                        moveToLogin()
                    }, 3000)
                }
            }
    }

    fun moveToLogin(){
        startActivity(Intent(baseContext, Login::class.java))
    }
}