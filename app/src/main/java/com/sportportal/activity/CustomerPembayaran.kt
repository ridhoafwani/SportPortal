@file:Suppress("DEPRECATION")

package com.sportportal.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.sportportal.adapter.CheckoutAdapter
import com.sportportal.databinding.ActivityCustomerPembayaranBinding
import com.sportportal.extra.*
import com.xendit.Xendit
import com.xendit.model.Invoice
import java.time.LocalDateTime


class CustomerPembayaran : AppCompatActivity() {

    private lateinit var binding : ActivityCustomerPembayaranBinding
    private lateinit var adapter : CheckoutAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var court : Court
    private lateinit var lapangan : Lapangan
    private lateinit var invoiceURL : String
    private var harga = ArrayList<Harga>()
    private val auth : FirebaseAuth get() = Login.user
    private var db = FirebaseFirestore.getInstance()
    @SuppressLint("NewApi")
    private val currentDateTime = LocalDateTime.now()
    private lateinit var xenditPaymentId : String
    private lateinit var bookDate : String
    private var amount = 0
    private var totalAmount = 0
    private lateinit var progressDialog : ProgressDialog
    var pesananList = ArrayList<Pesanan>()
    private var dp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerPembayaranBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Checkout"

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        Xendit.Opt.apiKey = com.sportportal.BuildConfig.XENDIT_API_KEY


        court = intent.getSerializableExtra("court") as Court
        lapangan = intent.getSerializableExtra("lapangan") as Lapangan
        harga = intent.getSerializableExtra("harga") as ArrayList<Harga>
        bookDate = intent.getStringExtra("bookDate") as String

        for(item in harga){
            totalAmount += item.harga.toInt()
        }
        amount=totalAmount
        binding.amount.text = formatRupiah(amount.toDouble())

        binding.rdGroup.setOnCheckedChangeListener { _, id ->
            when(id){
                binding.rdBtnLunas.id -> {
                    amount = totalAmount
                    dp = false
                }

                binding.rdBtnDp.id -> {
                    amount = totalAmount/2
                    dp = true
                }
            }

            binding.amount.text = formatRupiah(amount.toDouble())
        }



        adapter = CheckoutAdapter(harga, lapangan, court)
        recyclerView = binding.checkoutList
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter



        binding.btnChekcout.setOnClickListener {

            progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Membuat Pesanan")
            progressDialog.setMessage("Loading ... ")
            progressDialog.progress = 10
            progressDialog.show()

            object : Thread() {
                override fun run() {
                    super.run()
                    try {
                        sleep(1000)
//                        if (progressDialog.isShowing) progressDialog.dismiss()
                        createPesanan()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }.start()

        }
    }

    private fun createPesanan(){
        for (index in 0 until harga.size ){

            val docref = db.collection("pesan").document()

            if(index == 0){
                xenditPaymentId = docref.id
            }

            val idPesanan = docref.id
            val uid = auth.currentUser?.uid.toString()
            val courtId = court.id
            val amount= harga[index].harga
            val scheduleDate = harga[index].date
            val scheduleTime = harga[index].jam
            val xenditInvoiceId = ""
            val paid = false
            val courtName = court.nama

            pesananList.add(Pesanan(idPesanan,uid,courtId,amount,scheduleDate,scheduleTime,xenditInvoiceId,xenditPaymentId,paid,courtName))

            if (index == harga.size - 1){
                createInvoice(xenditPaymentId)
            }


        }
    }

    private fun createInvoice(externalid : String){

        val payeremail = auth.currentUser?.email
        val description = lapangan.penyedia

        try{

            val invoice = Invoice.create(
                externalid,
                amount,
                payeremail,
                description
            )

            for(item in pesananList){
                item.xenditInvoiceid = invoice.id

                db.collection("pesan").document(item.idPesanan).set(item)
                    .addOnSuccessListener {
                        Log.w(TAG, "Succses adding document")
                    }
                    .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e)
                    }
            }

            val transactionData: MutableMap<String, Any> = HashMap()
            transactionData["uid"] = auth.currentUser?.uid.toString()
            transactionData["amount"] = totalAmount.toString()
            transactionData["dp"] = dp
            transactionData["orderTime"] = currentDateTime.toString()
            transactionData["lapName"] = lapangan.title
            transactionData["item"] = pesananList.size.toString()
            transactionData["idLapangan"] = lapangan.id
            transactionData["paid"] = false

            db.collection("transaksi").document(invoice.id).set(transactionData)
                .addOnSuccessListener {
                    Log.w(TAG, "Succses adding transaksi")
                }
                .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e)
                }


            invoiceURL = invoice.invoiceUrl


            val intent = Intent(baseContext,CustomerPesananDibuat::class.java)
            val bundle = Bundle().apply {
                putString("invUrl", invoiceURL)
            }
            intent.putExtras(bundle)

            progressDialog.dismiss()

            startActivity(intent)
        }catch(e: Exception){
            println("error $e")
        }
    }
}