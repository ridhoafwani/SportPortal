package com.sportportal.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sportportal.databinding.ActivityPenyediaPenarikanDibuatBinding

class PenyediaPenarikanDibuat : AppCompatActivity() {
    private lateinit var binding: ActivityPenyediaPenarikanDibuatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPenyediaPenarikanDibuatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val invUrl = intent.getStringExtra("invUrl") as String
        val message = intent.getStringExtra("message") as String

        binding.tvMessage.text = message

        intent = Intent(Intent.ACTION_VIEW, Uri.parse(invUrl))
        startActivity(Intent.createChooser(intent, "Browse with"))

        binding.btnPesanan.setOnClickListener {
            goToPenarikan()
        }
    }

    private fun goToPenarikan() {
        val intent = Intent(baseContext, PenyediaPenarikanList::class.java)
        startActivity(intent)
        finish()
    }
}