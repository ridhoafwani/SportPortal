package com.sportportal.adapter

import android.content.ContentValues
import android.graphics.Color
import android.os.Build
import android.util.Log
import com.sportportal.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.sportportal.databinding.ItemPesananLayoutBinding
import com.sportportal.extra.Transaction
import com.sportportal.extra.formatRupiah
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class PesananAdapter(val data : ArrayList<Transaction>) : RecyclerView.Adapter<PesananAdapter.TransactionVH>() {


    inner class TransactionVH(val binding: ItemPesananLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionVH {
        val binding =
            ItemPesananLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionVH(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: TransactionVH, position: Int) {

        val formater = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern("d MMM yyy h:mm a", Locale("id", "ID"))
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        val today = LocalDate.now()
        val hourNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("H"))
        val db = FirebaseFirestore.getInstance()

        val item = data[position]
        holder.binding.apply {

            lapanganName.text = item.namaLapangan
            val orderTime = LocalDateTime.parse(item.orderTime).format(formater)
            pesananDate.text = orderTime.toString()
            pesananTime.text = item.itemCount + " Item"
            pesananAmount.text = formatRupiah(item.amount.toDouble())
            var status = item.xenditInvoice.status



            when(status){
                "PAID" ->{
                    transactionIconView.setBackgroundResource(R.drawable.icon_bg_success)
                    transactionIconView.setImageResource(R.drawable.ic_baseline_check_24)
                    pesananAmount.setTextColor(Color.parseColor("#3BE9E5"))
                }

                "SETTLED" ->{
                    transactionIconView.setBackgroundResource(R.drawable.icon_bg_success)
                    pesananAmount.setTextColor(Color.parseColor("#3BE9E5"))
                    transactionIconView.setImageResource(R.drawable.ic_baseline_check_24)
                }

                "EXPIRED" ->{
                    transactionIconView.setBackgroundResource(R.drawable.icon_bg_failed)
                    pesananAmount.setTextColor(Color.parseColor("#D6B9FE"))
                    transactionIconView.setImageResource(R.drawable.ic_baseline_warning_24)
                }

                "PENDING" ->{

                    var firstdate = ""
                    var firstTime = ""

                    db.collection("pesan")
                        .whereEqualTo("xenditInvoiceid", item.idTransaction)
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
                                        transactionIconView.setBackgroundResource(R.drawable.icon_bg_failed)
                                        pesananAmount.setTextColor(Color.parseColor("#D6B9FE"))
                                        transactionIconView.setImageResource(R.drawable.ic_baseline_warning_24)
                                    }

                                    "PENDING" -> {
                                        transactionIconView.setBackgroundResource(R.drawable.icon_bg_warning)
                                        pesananAmount.setTextColor(Color.parseColor("#EDC9AA"))
                                        transactionIconView.setImageResource(R.drawable.ic_baseline_timelapse_24)
                                    }
                                }


                            } else {
                                Log.w(ContentValues.TAG, "Error getting documents.", task.exception)


                            }


                        }

//                    transactionIconView.setBackgroundResource(R.drawable.icon_bg_warning)
//                    pesananAmount.setTextColor(Color.parseColor("#EDC9AA"))
//                    transactionIconView.setImageResource(R.drawable.ic_baseline_timelapse_24)
                }
            }



            // on item click
            holder.itemView.setOnClickListener {
                onItemClickListener?.let { it(item) }
            }
        }
    }

    // on item click listener
    private var onItemClickListener: ((Transaction) -> Unit)? = null

    fun setOnItemClickListener(listener: (Transaction) -> Unit) {
        onItemClickListener = listener
    }
}
