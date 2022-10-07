package com.sportportal.activity

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sportportal.adapter.LapanganAdapter
import com.sportportal.databinding.ActivityPenyediaPesananOfflineLapanganListBinding
import com.sportportal.extra.Lapangan
import java.io.Serializable

class PenyediaPesananOfflineLapanganList : PenyediaNavigationDrawer() {

    private lateinit var binding : ActivityPenyediaPesananOfflineLapanganListBinding
    private val auth : FirebaseAuth get() = Login.user
    private var db = FirebaseFirestore.getInstance()
    private var data = ArrayList<Lapangan>()
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: LapanganAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPenyediaPesananOfflineLapanganListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Pesanan Offline"

        getDataFromFirestore()

    }

    private fun getDataFromFirestore() {
        db.collection("lapangan")
            .whereEqualTo("penyedia", auth.currentUser?.uid)
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

                            moveToCourt(bundle)

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

    private fun moveToCourt(bundle : Bundle){
        val intent = Intent(baseContext,PenyediaPesananOfflineCourtList::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}