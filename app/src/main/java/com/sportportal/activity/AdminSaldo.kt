package com.sportportal.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sportportal.R
import com.sportportal.adapter.SaldoPenarikanAdapter
import com.sportportal.adapter.SaldoPesananAdapter
import com.sportportal.databinding.ActivityPenyediaSaldoBinding
import com.sportportal.extra.*
import com.xendit.model.Invoice
import java.io.Serializable

class AdminSaldo : AdminNavigationDrawer() {

    private lateinit var binding : ActivityPenyediaSaldoBinding
    private var db = FirebaseFirestore.getInstance()
    private var pesananList = ArrayList<Transaction>()
    private var penarikanList = ArrayList<Penarikan>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SaldoPesananAdapter
    private lateinit var recyclerViewPenarikan: RecyclerView
    private lateinit var adapterPenarikan: SaldoPenarikanAdapter
    private lateinit var invoices : Array<Invoice>
    private var pendapatan = 0.0
    private var ditarik = 0.0
    private var saldo = 0.0

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

        binding.totalBalanceTitle.text = "Saldo Sistem"

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

        binding.btnWithdraw.visibility = View.GONE
    }

    private fun inibaseMode(){
        binding.btnPendapatan.setBackgroundColor(getColor(R.color.first))
        binding.btnDitarik.setBackgroundColor(getColor(R.color.white))
    }

    private fun getSaldo(){
        db.collection("users_extra")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val userSaldo = document.data["saldo"] as String
                    saldo += userSaldo.toDouble()
                }
                binding.totalSaldo.text = formatRupiah(saldo)
                getPenarikan()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

    }


    private fun invoiceData(){
        val params: MutableMap<String, Any> = HashMap()
        params["limit"] = 1000
        invoices = Invoice.getAll(params)
        getTransaction()
    }


    private fun getTransaction() {
        db.collection("transaksi")
            .orderBy("orderTime", Query.Direction.DESCENDING)
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

    private fun moveToDetailsPenarikan(bundle : Bundle){
        val intent = Intent(this, AdminDetailPenarikan::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun getPenarikan() {
        db.collection("penarikan")
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

                    moveToDetailsPenarikan(bundle)
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }
}