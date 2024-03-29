package com.sportportal.activity

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath.documentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sportportal.adapter.PesananAdapter
import com.sportportal.databinding.ActivityCustomerPesananBinding
import com.sportportal.extra.Transaction
import com.sportportal.extra.formatRupiah
import com.sportportal.extra.syncpaymentStatus
import com.xendit.model.Invoice
import java.io.Serializable

class CustomerPesanan : UserNavigationDrawerActivity() {

    private lateinit var binding : ActivityCustomerPesananBinding
    private val auth : FirebaseAuth get() = Login.user
    var db = FirebaseFirestore.getInstance()
    private var pesananList = ArrayList<Transaction>()
    private var success = 0
    private var expired = 0
    private var pending = 0
    private var spent = 0.0
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PesananAdapter
    private lateinit var invoices : Array<Invoice>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerPesananBinding.inflate(layoutInflater)
        object : Thread() {
            override fun run() {
                super.run()
                try {
                    syncpaymentStatus()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()

        invoiceData()
        getTransaction()
        setContentView(binding.root)
        supportActionBar?.title = "Pesanan"

        binding.refresh.setOnRefreshListener {
            val intent = intent
            startActivity(intent)
            finish()
        }
    }

    private fun invoiceData(){
        try {
            val email = auth.currentUser?.email
            val params: MutableMap<String, Any> = HashMap()
            params["limit"] = 1000
            invoices = Invoice.getAll(params)
            for (item in invoices){
                if (item.payerEmail != email){
                    invoices.dropWhile { it.id == item.id }
                }
            }
        }
        catch (e:Exception){
            Toast.makeText(
                this, "Gagal Mengambil Data Xendit Invoice",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getTransaction() {
        db.collection("transaksi")
            .orderBy(documentId(), Query.Direction.DESCENDING)
            .whereEqualTo("uid", auth.currentUser?.uid)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    pesananList.clear()
                    success = 0
                    expired = 0
                    pending = 0
                    spent = 0.0
                    for (document in task.result!!) {
                        val idTransaction = document.id
                        val uid = document.data["uid"] as String
                        val amount = document.data["amount"] as String
                        val orderTime = document.data["orderTime"] as String
                        val lapName = document.data["lapName"] as String
                        val itemCount = document.data["item"] as String
                        val dp = document.data["dp"] as Boolean
                        for (item in invoices) {
                            if (item.id == idTransaction) {
                                when (item.status) {
                                    "PAID" -> {
                                        success += 1
                                        spent += amount.toDouble()
                                    }

                                    "SETTLED" -> {
                                        success += 1
                                        spent += amount.toDouble()
                                    }

                                    "EXPIRED" -> {
                                        expired += 1
                                    }

                                    "PENDING" -> {
                                        pending += 1
                                    }
                                }
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
                                invoices.dropWhile {
                                    it.id == idTransaction
                                }
                                break
                            }
                        }

                    }

                    binding.tvSuccess.text = success.toString()
                    binding.tvExpired.text = expired.toString()
                    binding.tvPending.text = pending.toString()
                    binding.totalSpent.text = formatRupiah(spent)

                    adapter = PesananAdapter(pesananList)
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
                    Log.w(ContentValues.TAG, "Error getting documents.", task.exception)
                    Toast.makeText(
                        this, "Error getting documents $task.exception",
                        Toast.LENGTH_SHORT
                    ).show()

                }


            }
    }

    private fun moveToDetails(bundle : Bundle){
        val intent = Intent(this,CustomerDetailPesanan::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}