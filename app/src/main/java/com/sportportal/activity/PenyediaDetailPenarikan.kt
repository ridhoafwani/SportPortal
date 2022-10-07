package com.sportportal.activity

import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.sportportal.R
import com.sportportal.databinding.ActivityPenyediaDetailPenarikanBinding
import com.sportportal.databinding.DialogInfoBinding
import com.sportportal.extra.Penarikan
import com.sportportal.extra.formatRupiah
import com.xendit.model.Payout
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


@Suppress("DEPRECATION")
class PenyediaDetailPenarikan : AppCompatActivity() {

    private lateinit var binding : ActivityPenyediaDetailPenarikanBinding
    private lateinit var penarikan : Penarikan
    private lateinit var payout : Payout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPenyediaDetailPenarikanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        penarikan = intent.getSerializableExtra("penarikan") as Penarikan
        initXenditElements()
        initView()

        binding.btnClaim.setOnClickListener {
            val intent = Intent(baseContext,PenyediaPenarikanDibuat::class.java)
            val bundle = Bundle().apply {
                putString("invUrl", payout.payoutUrl)
                putString("message", "CLAIM DANA PENARIKANMU")
            }
            intent.putExtras(bundle)

            startActivity(intent)
        }

        binding.btnExpire.setOnClickListener {
            try {
                Payout.voidPayout(payout.id)
                showCustomDialog()


            }
            catch (e:Exception){
                println(e)
            }
        }

    }

    private fun initXenditElements(){
        try{
            payout = Payout.getPayout(penarikan.xenditPayoutId)
        }
        catch (e:Exception){
            println(e)
        }

    }

    private fun initView(){
        binding.tvNamaPenarik.text = penarikan.namaPenarik
        binding.tvAmount.text = formatRupiah(penarikan.amount.toDouble())
        binding.tvEmail.text = payout.email
        val penarikanTime = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.parse(penarikan.time).format(DateTimeFormatter.ofPattern("EEE, d MMM yyy h:mm a", Locale("id", "ID")))
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        binding.tvTime.text = penarikanTime

        if(penarikan.isClaimed){
            binding.tvBank.text = payout.bankCode
            binding.tvNorek.text = payout.accountNumber
            binding.tvAn.text = payout.accountHolderName
        }
        else if(penarikan.isCanceled){
            binding.tvStatus.setBackgroundResource(R.color.second)
            binding.tvStatus.text = "Dibatalkan"
            binding.tvKetStatus.text = "Penarikan telah dibatalkan"
            binding.tvBank.text = "-"
            binding.tvNorek.text = "-"
            binding.tvAn.text = "-"
        }
        else{
            binding.tvStatus.setBackgroundResource(R.color.third)
            binding.tvStatus.text = "Dalam Proses"
            if(payout.bankCode == "" || payout.bankCode == null){
                binding.tvKetStatus.text = "Penarikan belum diclaim"
                binding.tvBank.text = "-"
                binding.tvNorek.text = "-"
                binding.tvAn.text = "-"
                binding.lytBtn.visibility = View.VISIBLE
            }
            else{
                binding.tvBank.text = payout.bankCode
                binding.tvNorek.text = payout.accountNumber
                binding.tvAn.text = payout.accountHolderName
                binding.tvKetStatus.text = "Penarikan sedang diproses"
            }


        }
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

        dialogInfoBinding.title.text = "Penarikan Dibatalkan"
        dialogInfoBinding.content.text = "Penarikan anda berhasil diabatalkan, dana penarikan anda akan dikembaliakn ke saldo aktif"
        dialogInfoBinding.btClose.text = "Penarikan"

        dialogInfoBinding.btClose.setOnClickListener { _ ->
            moveToPenarikanList()
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.attributes = lp

        Handler().postDelayed({
            dialog.dismiss()
            moveToPenarikanList()
        }, 3000)
    }

    private fun moveToPenarikanList(){
        val intent = Intent(baseContext,PenyediaPenarikanList::class.java)
        startActivity(intent)
    }


}