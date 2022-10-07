package com.sportportal.activity

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.sportportal.adapter.LapanganAdapter
import com.sportportal.databinding.ActivityPenyediaLapanganListBinding
import com.sportportal.extra.Lapangan
import com.sportportal.extra.syncPayoutStatus
import com.sportportal.extra.syncpaymentStatus
import java.io.Serializable

class AdminLapanganList : AdminNavigationDrawer() {

    private lateinit var binding : ActivityPenyediaLapanganListBinding
    private var db = FirebaseFirestore.getInstance()
    private var data = ArrayList<Lapangan>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LapanganAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPenyediaLapanganListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Lapangan"

        object : Thread() {
            override fun run() {
                super.run()
                try {
                    syncpaymentStatus()
                    syncPayoutStatus()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()

        binding.title.text = "Lapangan yang Tersedia"
        binding.floatingAddLapangan.visibility = View.GONE

        getDataFromFirestore()

        binding.refresh.setOnRefreshListener {
            val intent = intent
            finish()
            startActivity(intent)
        }

    }

    private fun getDataFromFirestore() {
        db.collection("lapangan")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    data.clear()
                    for (document in task.result!!) {
                        val id = document.id
                        val title = document.data["title"] as String
                        val address = document.data["address"] as String
                        val category = document.data["category"] as String
                        val image = (document.id + ".jpg")
                        val map = document.data["map"] as String
                        val penyedia = document.data["penyedia"] as String
                        val lowest_price = document.data["lowest_price"] as String
                        data.add(Lapangan(id, title, address, map, category, image, penyedia, lowest_price))

                    }

                    if (data.isEmpty()) {
                        showEmptyData()
                    } else {
                        adapter = LapanganAdapter(data)
                        recyclerView = binding.lapanganList
                        recyclerView.layoutManager = LinearLayoutManager(this)
                        recyclerView.adapter = adapter


                        adapter.setOnItemClickListener {
                            val bundle = Bundle().apply {
                                putSerializable("lapangan", it as Serializable)
                            }

                            moveToDetails(bundle)

                        }
                    }


                } else {
                    Log.w(ContentValues.TAG, "Error getting documents.", task.exception)
                    Toast.makeText(
                        this, "Error getting documents $task.exception",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
    }


    private fun showEmptyData(){
        binding.emptyStateLayout.visibility = View.VISIBLE
    }

    private fun moveToDetails(bundle : Bundle){
        val intent = Intent(baseContext,AdminDetailLapangan::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

}