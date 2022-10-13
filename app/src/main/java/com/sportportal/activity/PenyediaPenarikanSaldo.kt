package com.sportportal.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sportportal.databinding.ActivityPenyediaPenarikanSaldoBinding
import com.sportportal.extra.MoneyTextWatcher
import com.sportportal.extra.cutSaldo
import com.sportportal.extra.formatRupiah
import com.xendit.Xendit
import com.xendit.model.Disbursement
import com.xendit.model.Payout
import java.math.BigDecimal
import java.time.LocalDateTime


@Suppress("DEPRECATION")
class PenyediaPenarikanSaldo : AppCompatActivity() {

    private lateinit var binding : ActivityPenyediaPenarikanSaldoBinding
    private var tersedia = ""
    private var db = FirebaseFirestore.getInstance()
    private val auth : FirebaseAuth get() = Login.user
    @SuppressLint("NewApi")
    private val currentDateTime = LocalDateTime.now()
    private var payoutLink = ""
    private lateinit var progressDialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPenyediaPenarikanSaldoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Xendit.Opt.apiKey = "xnd_development_5PGeUn6WAWZQYHvSUeOEPtiwfhLdJNEgd59z8m7AXq7PNKHcMReOtdgsaqeSI"

        tersedia = intent.getStringExtra("saldo") as String
        binding.tvTersedia.text = formatRupiah(tersedia.toDouble())
        binding.amount.text = formatRupiah(tersedia.toDouble())

        binding.withdraw.addTextChangedListener(MoneyTextWatcher(binding.withdraw))

        binding.withdraw.doAfterTextChanged {
            updateSaldo()
        }

        binding.btnWithdraw.setOnClickListener {
            when{
                doGetValue().toInt() < 10000 -> {
                    binding.withdraw.error = "Penarikan minimal adalah Rp 10.000"
                }

                doGetValue().toInt() > tersedia.toInt() -> {
                    binding.withdraw.error = "Saldo tidak cukup"
                }

                else ->{
                    progressDialog = ProgressDialog(this)
                    progressDialog.setTitle("Membuat Penarikan")
                    progressDialog.setMessage("Loading ... ")
                    progressDialog.progress = 10
                    progressDialog.show()

                    object : Thread() {
                        override fun run() {
                            super.run()
                            try {
                                sleep(1500)
                                createPenarikan()
                            } catch (e: InterruptedException) {
                                e.printStackTrace()
                            }
                        }
                    }.start()

                }
            }
        }


    }

    private fun doGetValue(): BigDecimal {
        return MoneyTextWatcher.parseCurrencyValue(binding.withdraw.text.toString())
    }

    private fun updateSaldo(){
        var sisa = (tersedia.toDouble() - doGetValue().toDouble())
        if (sisa < 0){
            sisa = 0.0
        }
        binding.amount.text = formatRupiah(sisa)
    }

    private fun createXenditPayOut(id : String){
        val params: MutableMap<String, Any> = HashMap()
        params["external_id"] = id
        params["amount"] = doGetValue()
        params["email"] = auth.currentUser?.email.toString()

        val payout = Payout.createPayout(params)
        payoutLink = payout.payoutUrl
        updatePenarikan(id,payout.id)

    }

    private fun updatePenarikan(docId : String, payoutId : String){
        db.collection("penarikan").document(docId)
            .update("xenditPayoutId", payoutId)
            .addOnSuccessListener {

                cutSaldo(doGetValue().toString(), auth.uid.toString())
                val intent = Intent(baseContext,PenyediaPenarikanDibuat::class.java)
                val bundle = Bundle().apply {
                    putString("invUrl", payoutLink)
                    putString("message", "PENARIKANMU BERHASIL DIBUAT")
                }
                intent.putExtras(bundle)

                if (progressDialog.isShowing) progressDialog.dismiss()
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

    }

    private fun createPenarikan(){
        val dataPenarikan: MutableMap<String, Any> = HashMap()
        dataPenarikan["idPenarik"] = auth.uid.toString()
        dataPenarikan["namaPenarik"] = auth.currentUser?.displayName.toString()
        dataPenarikan["amount"] = doGetValue().toString()
        dataPenarikan["isClaimed"] = false
        dataPenarikan["isCanceled"] = false
        dataPenarikan["time"] = currentDateTime.toString()
        dataPenarikan["xenditPayoutId"] = ""

        db.collection("penarikan")
            .add(dataPenarikan)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot written with ID: ${documentReference.id}")
                createXenditPayOut(documentReference.id)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
    }
}