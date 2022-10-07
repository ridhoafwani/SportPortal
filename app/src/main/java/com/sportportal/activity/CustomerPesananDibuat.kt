package com.sportportal.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sportportal.databinding.ActivityCustomerPesananDibuatBinding

class CustomerPesananDibuat : AppCompatActivity() {

    private lateinit var binding : ActivityCustomerPesananDibuatBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerPesananDibuatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val invUrl = intent.getStringExtra("invUrl") as String

        intent = Intent(Intent.ACTION_VIEW, Uri.parse(invUrl))
        startActivity(Intent.createChooser(intent, "Browse with"))

        binding.btnPesanan.setOnClickListener {
            goToPesanan()
        }
    }

    private fun goToPesanan(){
        val intent = Intent(baseContext,CustomerPesanan::class.java)
        startActivity(intent)
        finish()
    }
}