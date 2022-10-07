package com.sportportal.activity

import android.content.Intent
import android.os.Bundle
import com.bumptech.glide.Glide
import com.sportportal.R
import com.sportportal.databinding.ActivityAdminProfilePenggunaBinding
import com.sportportal.extra.Pengguna
import java.io.Serializable

class AdminProfilePengguna : AdminNavigationDrawer() {

    private lateinit var binding : ActivityAdminProfilePenggunaBinding
    private lateinit var user : Pengguna

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminProfilePenggunaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Profile Pengguna"

        user = intent.getSerializableExtra("pengguna") as Pengguna
        initView()

        binding.btnEditRole.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("pengguna", user as Serializable)
            }
            moveToEditRole(bundle)
        }

    }

    private fun initView(){
        binding.name.text = user.name
        binding.email.text = user.email
        binding.role.text = user.role

        Glide.with(binding.image.context)
            .load(user.image)
            .fitCenter()
            .placeholder(R.drawable.profile_man)
            .into(binding.image)
    }

    private fun moveToEditRole(bundle: Bundle){
        val intent = Intent(baseContext,AdminEditRole::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}