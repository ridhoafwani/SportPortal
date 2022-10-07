package com.sportportal.activity

import android.content.Intent
import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.sportportal.R
import com.sportportal.databinding.ActivityProfileBinding
import java.text.SimpleDateFormat
import java.util.*

class Profile : UserNavigationDrawerActivity() {

    private lateinit var binding : ActivityProfileBinding
    private val auth : FirebaseAuth get() = Login.user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Profile"

        binding.name.text = auth.currentUser?.displayName
        val time = auth.currentUser?.metadata?.creationTimestamp
        binding.createdAt.text = time?.let { getDateTime(it) }
        binding.email.text = auth.currentUser?.email

        val imageUrl = auth.currentUser?.photoUrl

        Glide.with(binding.imgProfilePhoto)
            .load(imageUrl)
            .placeholder(R.drawable.profile_man)
            .into(binding.imgProfilePhoto)

        binding.btnEdtProfile.setOnClickListener {
            goToEdtProfile()
        }
    }

    private fun getDateTime(s: Long): String? {
        return try {
            val sdf = SimpleDateFormat("d MMM yyy")
            val netDate = Date(s)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }
    }

    private fun goToEdtProfile(){
        val intent = Intent(baseContext,EditProfile::class.java)
        startActivity(intent)
    }


}