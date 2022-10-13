package com.sportportal.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sportportal.R
import com.sportportal.adapter.SaldoPenarikanAdapter
import com.sportportal.adapter.SaldoPesananAdapter
import com.sportportal.databinding.ActivityPenyediaSaldoBinding
import com.sportportal.extra.*
import com.xendit.model.Invoice
import java.io.Serializable

class PenyediaSaldo : PenyediaNavigationDrawer() {

    private lateinit var binding : ActivityPenyediaSaldoBinding
    private var db = FirebaseFirestore.getInstance()
    private val auth : FirebaseAuth get() = Login.user
    private var pesananList = ArrayList<Transaction>()
    private var penarikanList = ArrayList<Penarikan>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SaldoPesananAdapter
    private lateinit var recyclerViewPenarikan: RecyclerView
    private lateinit var adapterPenarikan: SaldoPenarikanAdapter
    private lateinit var invoices : Array<Invoice>
    private var idLapanganList = listOf<String>()
    private var pendapatan = 0.0
    private var ditarik = 0.0
    private var saldo =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPenyediaSaldoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Saldo"
        object : Thread() {
            override fun run() {
                super.run()
                try {
                    syncPayoutStatus()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()

        inibaseMode()

        object : Thread() {
            override fun run() {
                super.run()
                try {
                    getSaldo()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()

        object : Thread() {
            override fun run() {
                super.run()
                try {
                    invoiceData()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()

        binding.refresh.setOnRefreshListener {
            val intent = intent
            startActivity(intent)
            finish()
        }

        binding.btnPendapatan.setOnClickListener {
            binding.btnPendapatan.setBackgroundColor(getColor(R.color.first))
            binding.btnDitarik.setBackgroundColor(getColor(R.color.white))
            binding.penarikanList.visibility = View.GONE
            binding.pesananList.visibility = View.VISIBLE
        }

        binding.btnDitarik.setOnClickListener {
            binding.btnPendapatan.setBackgroundColor(getColor(R.color.white))
            binding.btnDitarik.setBackgroundColor(getColor(R.color.second))
            binding.penarikanList.visibility = View.VISIBLE
            binding.pesananList.visibility = View.GONE
        }

        binding.btnWithdraw.setOnClickListener {
            moveToPenarikan()
        }
    }

    private fun inibaseMode(){
        binding.btnPendapatan.setBackgroundColor(getColor(R.color.first))
        binding.btnDitarik.setBackgroundColor(getColor(R.color.white))
    }

    private fun getSaldo(){
        db.collection("users_extra").document(auth.uid.toString())
            .get()
            .addOnSuccessListener { document ->
                if(document!=null){
                    Log.d(TAG, "${document.id} => ${document.data}")
                    saldo = document.data?.get("saldo") as String
                    binding.totalSaldo.text = formatRupiah(saldo.toDouble())
                    getPenarikan()
                }
                else{
                    Log.d(TAG, "document is null")
                }

            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }


    private fun getLapangan(){
        db.collection("lapangan")
            .whereEqualTo("penyedia", auth.uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                    val id = document.id
                    idLapanganList = idLapanganList + id
                }
                if( documents.size() > 0){
                    getTransaction()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun invoiceData(){
        val params: MutableMap<String, Any> = HashMap()
        params["limit"] = 1000
        invoices = Invoice.getAll(params)
        getLapangan()
    }


    private fun getTransaction() {
        println(idLapanganList)
        db.collection("transaksi")
            .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
            .whereIn("idLapangan", idLapanganList)
            .whereEqualTo("paid", true)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    pesananList.clear()
                    for (document in task.result!!) {
                        println(document.id)
                        val idTransaction = document.id
                        val uid = document.data["uid"] as String
                        val amount = document.data["amount"] as String
                        val orderTime = document.data["orderTime"] as String
                        val lapName = document.data["lapName"] as String
                        val itemCount = document.data["item"] as String
                        val dp = document.data["dp"] as Boolean
                        for (item in invoices) {
                            if (item.id == idTransaction) {
                                pesananList.add(
                                    Transaction(
                                        idTransaction,
                                        uid,
                                        amount,
                                        orderTime,
                                        lapName,
                                        itemCount,
                                        dp,
                                        item
                                    )
                                )
                                pendapatan += amount.toDouble()
                                invoices.dropWhile { invoice ->
                                    invoice.id == idTransaction
                                }
                                break
                            }
                        }

                    }

                    binding.tvPendapatan.text = formatRupiah(pendapatan)

                    adapter = SaldoPesananAdapter(pesananList)
                    recyclerView = binding.pesananList
                    recyclerView.layoutManager = LinearLayoutManager(this)
                    recyclerView.adapter = adapter

                    adapter.setOnItemClickListener {
                        val bundle = Bundle().apply {
                            putSerializable("transaction", it as Serializable)
                        }

                        moveToDetails(bundle)
                    }

                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                    Toast.makeText(
                        this, "Error getting documents $task.exception",
                        Toast.LENGTH_SHORT
                    ).show()

                }


            }
    }

    private fun moveToDetails(bundle : Bundle){
        val intent = Intent(this,PenyediaDetailPesanan::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun moveToDetailPenarikan(bundle : Bundle){
        val intent = Intent(this,PenyediaDetailPenarikan::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun moveToPenarikan(){
        val intent = Intent(this,PenyediaPenarikanSaldo::class.java)
        intent.putExtra("saldo", saldo)
        startActivity(intent)
    }

    private fun getPenarikan() {
        db.collection("penarikan")
            .whereEqualTo("idPenarik", auth.uid)
            .whereEqualTo("isCanceled", false)
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
                    ditarik += amount.toDouble()

                }
                binding.tvDitarik.text = formatRupiah(ditarik)
                adapterPenarikan = SaldoPenarikanAdapter(penarikanList)
                recyclerViewPenarikan = binding.penarikanList
                recyclerViewPenarikan.layoutManager = LinearLayoutManager(this)
                recyclerViewPenarikan.adapter = adapterPenarikan

                adapterPenarikan.setOnItemClickListener {
                    val bundle = Bundle().apply {
                        putSerializable("penarikan", it as Serializable)
                    }

                    moveToDetailPenarikan(bundle)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }
}