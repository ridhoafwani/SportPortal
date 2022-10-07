package com.sportportal.activity

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sportportal.adapter.PenarikanAdapter
import com.sportportal.databinding.ActivityPenyediaPenarikanListBinding
import com.sportportal.extra.Penarikan
import com.sportportal.extra.formatRupiah
import com.sportportal.extra.syncPayoutStatus
import java.io.Serializable

class PenyediaPenarikanList : PenyediaNavigationDrawer() {

    private lateinit var binding: ActivityPenyediaPenarikanListBinding
    private val auth : FirebaseAuth get() = Login.user
    var db = FirebaseFirestore.getInstance()
    private var penarikanList = ArrayList<Penarikan>()
    private var success = 0
    private var canceled = 0
    private var pending = 0
    private var ditarik = 0.0
    private var saldo = ""
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PenarikanAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPenyediaPenarikanListBinding.inflate(layoutInflater)


        object : Thread() {
            override fun run() {
                super.run()
                try {
                    syncPayoutStatus()
                    getSaldo()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()

        getPenarikan()
        setContentView(binding.root)
        supportActionBar?.title = "Penarikan"

        binding.refresh.setOnRefreshListener {
            val intent = intent
            startActivity(intent)
            finish()
        }

        binding.btnAddPenarikan.setOnClickListener{
            moveToPenarikan()
        }
    }


    private fun getPenarikan() {
        db.collection("penarikan")
            .whereEqualTo("idPenarik", auth.uid)
            .orderBy("time", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val idPenarik = document.data["idPenarik"] as String
                    val namaPenarik = document.data["namaPenarik"] as String
                    val amount = document.data["amount"] as String
                    val isClaimed = document.data["isClaimed"] as Boolean
                    val isCanceled = document.data["isCanceled"] as Boolean
                    val time = document.data["time"] as String
                    val xenditPayoutId = document.data["xenditPayoutId"] as String
                    penarikanList.add(Penarikan(idPenarik, namaPenarik, amount, isClaimed, isCanceled, time, xenditPayoutId))

                    if(isClaimed){
                        success += 1
                        ditarik += amount.toDouble()
                    }
                    else if (isCanceled){
                        canceled +=1

                    }
                    else{
                        pending += 1
                        ditarik += amount.toDouble()

                    }

                }

                binding.tvSuccess.text = success.toString()
                binding.tvExpired.text = canceled.toString()
                binding.tvPending.text = pending.toString()
                binding.totalSpent.text = formatRupiah(ditarik)

                adapter = PenarikanAdapter(penarikanList)
                recyclerView = binding.penarikanList
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = adapter

                adapter.setOnItemClickListener {
                    val bundle = Bundle().apply {
                        putSerializable("penarikan", it as Serializable)
                    }

                    moveToDetails(bundle)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    private fun moveToDetails(bundle : Bundle){
        val intent = Intent(this,PenyediaDetailPenarikan::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun moveToPenarikan(){
        val intent = Intent(this,PenyediaPenarikanSaldo::class.java)
        intent.putExtra("saldo", saldo)
        startActivity(intent)
    }

    private fun getSaldo(){
        db.collection("users_extra").document(auth.uid.toString())
            .get()
            .addOnSuccessListener { document ->
                if(document!=null){
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    saldo = document.data?.get("saldo") as String

                }
                else{
                    Log.d(ContentValues.TAG, "document is null")
                }

            }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }
}