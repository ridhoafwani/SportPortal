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
import com.sportportal.adapter.CourtAdapter
import com.sportportal.databinding.ActivityPenyediaPesananOfflineCourtListBinding
import com.sportportal.extra.Court
import com.sportportal.extra.Lapangan
import java.io.Serializable

class PenyediaPesananOfflineCourtList : PenyediaNavigationDrawer() {

    private lateinit var binding : ActivityPenyediaPesananOfflineCourtListBinding
    private var db = FirebaseFirestore.getInstance()
    private lateinit var lapangan : Lapangan
    private var court = ArrayList<Court>()
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: CourtAdapter
    private lateinit var price : MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPenyediaPesananOfflineCourtListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Pesanan Offline"

        lapangan = intent.getSerializableExtra("lapangan") as Lapangan
        getCourtList()

    }

    private fun getCourtList() {
        db.collection("court")
            .whereEqualTo("idLapangan", lapangan.id)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    court.clear()
                    for (document in task.result!!) {
                        val id = document.id
                        val idLapangan = document.data["idLapangan"] as String
                        val nama = document.data["nama"] as String
                        val desc = document.data["desc"] as String
                        val jam: MutableList<String> = mutableListOf()
                        for (i in 0 until 24) {
                            var price_in_hour = document.data[i.toString()] as String
                            if(price_in_hour == ""){
                                price_in_hour = "0"
                            }
                            jam.add(price_in_hour)
                        }
                        price = jam
                        court.add(
                            Court(
                                id,
                                idLapangan,
                                nama,
                                lapangan.image,
                                desc,
                                price[0],
                                price[1],
                                price[2],
                                price[3],
                                price[4],
                                price[5],
                                price[6],
                                price[7],
                                price[8],
                                price[9],
                                price[10],
                                price[11],
                                price[12],
                                price[13],
                                price[14],
                                price[15],
                                price[16],
                                price[17],
                                price[18],
                                price[19],
                                price[20],
                                price[21],
                                price[22],
                                price[23]
                            )
                        )

                    }

                    if (court.isEmpty()) {
                        showEmptyData()
                        adapter = CourtAdapter(court)
                        recyclerView = binding.courtList
                        recyclerView.layoutManager = LinearLayoutManager(this)
                        recyclerView.adapter = adapter

                    } else {
                        adapter = CourtAdapter(court)
                        recyclerView = binding.courtList
                        recyclerView.layoutManager = LinearLayoutManager(this)
                        recyclerView.adapter = adapter

                    }

                    adapter.setOnItemClickListener {
                        val bundle = Bundle().apply {
                            putSerializable("court", it as Serializable)
                            putSerializable("lapangan", lapangan as Serializable)
                        }

                        moveToPesananOfflineList(bundle)

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

    private fun moveToPesananOfflineList(bundle: Bundle) {
        val intent = Intent(baseContext,PenyediaPesananOffline::class.java)
        intent.putExtras(bundle)
        startActivity(intent)

    }

    private fun showEmptyData() {
        binding.emptyStateLayout.visibility = View.VISIBLE
    }
}