package com.sportportal.activity

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sportportal.adapter.PesananOfflineAdapter
import com.sportportal.databinding.ActivityPenyediaPesananOfflineBinding
import com.sportportal.extra.Court
import com.sportportal.extra.Lapangan
import com.sportportal.extra.PesananOffline
import java.io.Serializable

class PenyediaPesananOffline : PenyediaNavigationDrawer() {

    private lateinit var binding : ActivityPenyediaPesananOfflineBinding
    private lateinit var lapangan : Lapangan
    private lateinit var  court : Court
    private var db = FirebaseFirestore.getInstance()
    private var pesananOfflineList = ArrayList<PesananOffline>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PesananOfflineAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPenyediaPesananOfflineBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Pesanan Offline"

        lapangan = intent.getSerializableExtra("lapangan") as Lapangan
        court = intent.getSerializableExtra("court") as Court

        print(court.id)
        getPesananOffline()


        binding.btnAddOffline.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable("court", court as Serializable)
                putSerializable("lapangan", lapangan as Serializable)
            }
            moveToTambahPesananOffline(bundle)
        }
    }

    private fun moveToTambahPesananOffline(bundle: Bundle) {
        val intent = Intent(baseContext,PenyediaTambahJadwalPesananOffline::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun getPesananOffline() {
        db.collection("pesanan_offline")
            .whereEqualTo("courtId", court.id)
            .orderBy("orderTime", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val id = document.id
                        val courtId = document.data["courtId"] as String
                        val courtName = document.data["courtName"] as String
                        val amount = document.data["amount"] as String
                        val scheduleDate = document.data["scheduleDate"] as String
                        val scheduleTime = document.data["scheduleTime"] as String
                        val lapName = document.data["lapName"] as String
                        val orderBy = document.data["orderBy"] as String
                        pesananOfflineList.add(PesananOffline(id,courtId,courtName,amount,scheduleDate,scheduleTime,lapName,orderBy))
                    }

                    //init recyclerview
                    adapter = PesananOfflineAdapter(pesananOfflineList)
                    recyclerView = binding.pesananList
                    recyclerView.layoutManager = LinearLayoutManager(this)
                    recyclerView.adapter = adapter

                    (pesananOfflineList.size.toString() + " pesanan").also { binding.totalSpent.text = it }


                } else {
                    Log.w(ContentValues.TAG, "Error getting documents.", task.exception)
                    Toast.makeText(
                        this, "Error getting documents $task.exception",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

    }


}