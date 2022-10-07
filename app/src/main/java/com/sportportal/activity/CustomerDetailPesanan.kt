package com.sportportal.activity

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.sportportal.R
import com.sportportal.adapter.ItemPesananAdapter
import com.sportportal.databinding.ActivityCustomerDetailPesananBinding
import com.sportportal.extra.PesananRead
import com.sportportal.extra.Transaction
import com.sportportal.extra.formatRupiah
import com.xendit.model.Invoice
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import androidx.core.view.drawToBitmap
import com.sportportal.extra.saveBitmap

class CustomerDetailPesanan : AppCompatActivity() {

    private lateinit var binding : ActivityCustomerDetailPesananBinding
    private lateinit var transaction : Transaction
    private var db = FirebaseFirestore.getInstance()
    private var pesananList = ArrayList<PesananRead>()
    private lateinit var adapter : ItemPesananAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var invoice : Invoice
    private var status = ""
    private val today = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDate.now()
    } else {
        TODO("VERSION.SDK_INT < O")
    }
    private val hourNow = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("H"))
    } else {
        TODO("VERSION.SDK_INT < O")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerDetailPesananBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Detail Pesanan"

        transaction = intent.getSerializableExtra("transaction") as Transaction
        getPesanan(transaction.idTransaction)

        invoice = Invoice.getById(transaction.idTransaction)

        binding.fullDetail.setOnRefreshListener {
            val intent = intent
            finish()
            startActivity(intent)
        }

        binding.tvNamLap.text = transaction.namaLapangan
        val orderTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.parse(transaction.orderTime).format(DateTimeFormatter.ofPattern("EEE, d MMM yyy h:mm a", Locale("id", "ID")))
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        binding.tvOrderTime.text = orderTime
        binding.transactionDetails.amount.text = formatRupiah(transaction.amount.toDouble())
        binding.transactionDetails.amountToPay.text = formatRupiah(invoice.amount.toDouble())

        if (transaction.dp){
            binding.transactionDetails.type.text = "DP"
        }else{
            binding.transactionDetails.type.text = "Lunas"
        }

        status = invoice.status

        binding.btnShare.setOnClickListener {
            shareImage()
        }

        when(status){
            "PAID" ->{
                binding.btnPay.visibility = View.GONE
                binding.btnShare.visibility = View.VISIBLE
                binding.tvStatus.setBackgroundResource(R.color.first)
                binding.tvStatus.text = "Sukses"
                binding.transactionDetails.status.text = "Sudah Dibayar"

                binding.transactionDetails.paymentMethod.text = invoice.paymentMethod
                binding.transactionDetails.expiredAt.visibility = View.GONE
                binding.transactionDetails.cpExpiredAt.visibility = View.GONE
                binding.transactionDetails.cpPayment.visibility = View.VISIBLE
                binding.transactionDetails.paymentMethod.visibility = View.VISIBLE

            }

            "SETTLED" ->{
                binding.btnPay.visibility = View.GONE
                binding.btnShare.visibility = View.VISIBLE
                binding.tvStatus.setBackgroundResource(R.color.first)
                binding.tvStatus.text = "Sukses"
                binding.transactionDetails.status.text = "Sudah Dibayar"

                binding.transactionDetails.paymentMethod.text = invoice.paymentMethod
                binding.transactionDetails.expiredAt.visibility = View.GONE
                binding.transactionDetails.cpExpiredAt.visibility = View.GONE
                binding.transactionDetails.cpPayment.visibility = View.VISIBLE
                binding.transactionDetails.paymentMethod.visibility = View.VISIBLE
            }

            "EXPIRED" ->{
                binding.btnPay.visibility = View.GONE
                binding.btnShare.visibility = View.GONE
                binding.tvStatus.setBackgroundResource(R.color.second)
                binding.tvStatus.text = "Gagal"
                binding.transactionDetails.status.text = "Gagal"

                binding.transactionDetails.cpPayment.visibility = View.GONE
                binding.transactionDetails.paymentMethod.visibility = View.GONE
                binding.transactionDetails.expiredAt.visibility = View.GONE
                binding.transactionDetails.cpExpiredAt.visibility = View.GONE
            }

            "PENDING" ->{

                var firstdate = ""
                var firstTime = ""

                db.collection("pesan")
                    .whereEqualTo("xenditInvoiceid", transaction.idTransaction)
                    .get()
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            for (document in task.result!!) {
                                firstdate = document.data["scheduleDate"] as String
                                firstTime = document.data["scheduleTime"] as String
                                break
                            }

                            if (today > LocalDate.parse(
                                    firstdate,
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                )
                            ) {
                                status = "EXPIRED"
                            } else if (today > LocalDate.parse(
                                    firstdate,
                                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                                )
                            ) {
                                if (hourNow.toInt() > LocalDateTime.parse(
                                        firstTime,
                                        DateTimeFormatter.ofPattern("H")
                                    ).toString().toInt()
                                ) {
                                    status = "EXPIRED"
                                }

                            }

                            when (status) {

                                "EXPIRED" -> {

                                    binding.btnPay.visibility = View.GONE
                                    binding.btnShare.visibility = View.GONE
                                    binding.tvStatus.setBackgroundResource(R.color.second)
                                    binding.tvStatus.text = "Gagal"
                                    binding.transactionDetails.status.text = "Expired"

                                    binding.transactionDetails.cpPayment.visibility = View.GONE
                                    binding.transactionDetails.paymentMethod.visibility = View.GONE
                                    binding.transactionDetails.expiredAt.visibility = View.GONE
                                    binding.transactionDetails.cpExpiredAt.visibility = View.GONE

                                    invoice.status = status
                                    adapter = ItemPesananAdapter(pesananList, invoice)
                                    recyclerView = binding.pesananList
                                    recyclerView.layoutManager = LinearLayoutManager(this)
                                    recyclerView.adapter = adapter
                                }

                                "PENDING" -> {
                                    binding.btnPay.visibility = View.VISIBLE
                                    binding.btnShare.visibility = View.GONE
                                    binding.tvStatus.setBackgroundResource(R.color.third)
                                    binding.tvStatus.text = "Pending"
                                    binding.transactionDetails.status.text = "Belum Dibayar"

                                    binding.transactionDetails.cpPayment.visibility = View.GONE
                                    binding.transactionDetails.paymentMethod.visibility = View.GONE
                                    binding.transactionDetails.expiredAt.visibility = View.VISIBLE
                                    binding.transactionDetails.cpExpiredAt.visibility = View.VISIBLE

                                    var dateFormat = SimpleDateFormat(
                                        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                                        Locale.getDefault()
                                    )
                                    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                                    val expiry = dateFormat.parse(invoice.expiryDate)

                                    dateFormat = SimpleDateFormat(
                                        "d MMM yyy h:mm a",
                                        Locale.getDefault()
                                    )
                                    dateFormat.timeZone = TimeZone.getDefault()
                                    binding.transactionDetails.expiredAt.text =
                                        dateFormat.format(expiry)

                                    binding.btnPay.setOnClickListener {
                                        val intent =
                                            Intent(baseContext, CustomerPesananDibuat::class.java)
                                        val bundle = Bundle().apply {
                                            putString("invUrl", invoice.invoiceUrl)
                                        }
                                        intent.putExtras(bundle)
                                        startActivity(intent)
                                    }
                                }
                            }


                        } else {
                            Log.w(ContentValues.TAG, "Error getting documents.", task.exception)


                        }


                    }


            }
        }

    }

    private fun getPesanan(idTransaction : String) {
        db.collection("pesan")
            .whereEqualTo("xenditInvoiceid", idTransaction)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    pesananList.clear()
                    for (document in task.result!!) {
                        val idItemPesanan = document.id
                        val courtId = document.data["courtId"] as String
                        val amount = document.data["amount"] as String
                        val scheduleDate = document.data["scheduleDate"] as String
                        val scheduleTime = document.data["scheduleTime"] as String
                        val courtName = document.data["courtName"] as String
                        pesananList.add(
                            PesananRead(
                                idItemPesanan,
                                courtId,
                                amount,
                                scheduleDate,
                                scheduleTime,
                                courtName
                            )
                        )
                    }



                    adapter = ItemPesananAdapter(pesananList, invoice)
                    recyclerView = binding.pesananList
                    recyclerView.layoutManager = LinearLayoutManager(this)
                    recyclerView.adapter = adapter


                } else {
                    Log.w(ContentValues.TAG, "Error getting documents.", task.exception)


                }


            }
    }

    private val requestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) shareImage()

        }

    private fun isStoragePermissionGranted(): Boolean = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED


    private fun shareImage() {
        if (!isStoragePermissionGranted()) {
            requestLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return
        }

        val imageURI = binding.detail.drawToBitmap().let { bitmap ->
            saveBitmap(this, bitmap)
        } ?: run {
            Toast.makeText(this, "error",
                Toast.LENGTH_SHORT).show()
            return
        }

        val intent = ShareCompat.IntentBuilder(this)
            .setType("image/jpeg")
            .setStream(imageURI)
            .intent

        startActivity(Intent.createChooser(intent, null))
    }
}