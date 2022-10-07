package com.sportportal.adapter

import android.graphics.Color
import android.os.Build
import com.sportportal.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sportportal.databinding.ItemDetailPesananLayoutBinding
import com.sportportal.extra.PesananRead
import com.sportportal.extra.formatRupiah
import com.xendit.model.Invoice
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class ItemPesananAdapter(val data : ArrayList<PesananRead>, val transaction : Invoice) : RecyclerView.Adapter<ItemPesananAdapter.TransactionVH>() {


    inner class TransactionVH(val binding: ItemDetailPesananLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionVH {
        val binding =
            ItemDetailPesananLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionVH(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: TransactionVH, position: Int) {

        val formater = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern("EEE, d MMM yyy", Locale("id", "ID"))
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        val item = data[position]
        holder.binding.apply {

            courtName.text = item.courtName
            val schedule = LocalDate.parse(item.scheduleDate,DateTimeFormatter.ofPattern("dd/MM/yyyy")).format(formater)
            pesananDate.text = schedule
            "Jam ${item.scheduleTime}.00".also { pesananTime.text = it }
            pesananAmount.text = formatRupiah(item.amount.toDouble())

            when(transaction.status){
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
                    transactionIconView.setBackgroundResource(R.drawable.icon_bg_warning)
                    pesananAmount.setTextColor(Color.parseColor("#EDC9AA"))
                    transactionIconView.setImageResource(R.drawable.ic_baseline_timelapse_24)
                }
            }



            // on item click
            holder.itemView.setOnClickListener {
                onItemClickListener?.let { it(item) }
            }
        }
    }

    // on item click listener
    private var onItemClickListener: ((PesananRead) -> Unit)? = null

    fun setOnItemClickListener(listener: (PesananRead) -> Unit) {
        onItemClickListener = listener
    }
}
