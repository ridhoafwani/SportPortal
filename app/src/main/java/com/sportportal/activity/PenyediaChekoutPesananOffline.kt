package com.sportportal.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.sportportal.adapter.CheckoutAdapter
import com.sportportal.databinding.ActivityPenyediaChekoutPesananOfflineBinding
import com.sportportal.databinding.DialogInfoBinding
import com.sportportal.extra.*
import java.time.LocalDateTime

@Suppress("DEPRECATION")
class PenyediaChekoutPesananOffline : AppCompatActivity() {

    private lateinit var binding: ActivityPenyediaChekoutPesananOfflineBinding
    private lateinit var adapter : CheckoutAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var court : Court
    private lateinit var lapangan : Lapangan
    private var harga = ArrayList<Harga>()
    private var db = FirebaseFirestore.getInstance()
    @SuppressLint("NewApi")
    private val currentDateTime = LocalDateTime.now()
    private lateinit var bookDate : String
    private var amount = 0
    private var totalAmount = 0
    var pesananList = ArrayList<Pesanan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPenyediaChekoutPesananOfflineBinding.inflate(layoutInflater)
        setContentView(binding.root)


        court = intent.getSerializableExtra("court") as Court
        lapangan = intent.getSerializableExtra("lapangan") as Lapangan
        harga = intent.getSerializableExtra("harga") as ArrayList<Harga>
        bookDate = intent.getStringExtra("bookDate") as String

        for(item in harga){
            totalAmount += item.harga.toInt()
        }
        amount=totalAmount
        binding.amount.text = formatRupiah(amount.toDouble())


        adapter = CheckoutAdapter(harga, lapangan, court)
        recyclerView = binding.checkoutList
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter



        binding.btnChekcout.setOnClickListener {
            createPesanan()
        }
    }

    private fun createPesanan(){
        for (index in 0 until harga.size ){
            val dataPesanan: MutableMap<String, Any> = HashMap()
            dataPesanan["courtId"] = court.id
            dataPesanan["amount"] = harga[index].harga
            dataPesanan["scheduleDate"] = harga[index].date
            dataPesanan["scheduleTime"] = harga[index].jam
            dataPesanan["orderTime"] = currentDateTime.toString()
            dataPesanan["lapName"] = lapangan.title
            dataPesanan["courtName"] = court.nama
            dataPesanan["orderBy"] = binding.edtName.text.toString()

            db.collection("pesanan_offline").add(dataPesanan)
                .addOnSuccessListener {
                    Log.w(ContentValues.TAG, "Succses adding transaksi")
                    showCustomDialog()
                }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding document", e)
                }
        }

    }

    private fun goToOffline() {
        val intent = Intent(baseContext, PenyediaPesananOfflineLapanganList::class.java)
        startActivity(intent)
    }

    private fun showCustomDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        val dialogInfoBinding = DialogInfoBinding.inflate(layoutInflater)
        dialog.setContentView(dialogInfoBinding.root)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        dialogInfoBinding.title.text = "Pesanan Offline Ditambahkan"
        dialogInfoBinding.content.text = "Data Pesanan Offline anda berhasil ditambahkan, silahkan cek pada menu Daftar Pesanan Offline"
        dialogInfoBinding.btClose.text = "Pesanan Offline"

        dialogInfoBinding.btClose.setOnClickListener {
            goToOffline()
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.attributes = lp
        Handler().postDelayed({
            dialog.dismiss()
            goToOffline()
        }, 3000)
    }

}