package com.sportportal.activity

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.sportportal.databinding.ActivityEmailVerificationBinding

@Suppress("DEPRECATION")
class EmailVerification : AppCompatActivity() {

    private lateinit var binding : ActivityEmailVerificationBinding
    private val auth : FirebaseAuth get() = Login.user

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEmailVerificationBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        verifiedChek()
        setContentView(binding.root)

        binding.btnSend.setOnClickListener {
            sendverification()
        }

    }

    private fun sendverification(){
        auth.setLanguageCode("id")
        auth.currentUser!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Email sent.")
                    Snackbar.make(
                        binding.root,
                        "Email Verifikasi Terkirim",
                        Snackbar.LENGTH_LONG
                    )
                        .apply {
                            show()
                        }

                    Handler().postDelayed({
                        signOut()
                    }, 3000)
                }
            }
    }

    private fun verifiedChek() {
        if (auth.currentUser!!.isEmailVerified){
            moveToNext()
        }

    }

    private fun moveToNext(){
        startActivity(Intent(baseContext, CustomerBeranda::class.java))
    }

    private fun signOut() {
        auth.signOut()
        startActivity(Intent(baseContext, Login::class.java))
    }
}