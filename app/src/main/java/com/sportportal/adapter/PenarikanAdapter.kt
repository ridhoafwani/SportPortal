package com.sportportal.adapter

import android.graphics.Color
import android.os.Build
import com.sportportal.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sportportal.databinding.ItemSaldoDitarikLayoutBinding
import com.sportportal.extra.Penarikan
import com.sportportal.extra.formatRupiah
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class PenarikanAdapter(val data : ArrayList<Penarikan>) : RecyclerView.Adapter<PenarikanAdapter.TransactionVH>() {


    inner class TransactionVH(val binding: ItemSaldoDitarikLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionVH {
        val binding =
            ItemSaldoDitarikLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

        val item = data[position]
        holder.binding.apply {

            penarik.text = item.namaPenarik
            val orderTime = LocalDateTime.parse(item.time).format(formater)
            penarikanDate.text = orderTime.toString()
            penarikanAmount.text = formatRupiah(item.amount.toDouble())

            if(item.isClaimed){
                transactionIconView.setBackgroundResource(R.drawable.icon_bg_success)
                transactionIconView.setImageResource(R.drawable.ic_baseline_check_24)
                penarikanAmount.setTextColor(Color.parseColor("#3BE9E5"))
            }
            else if (item.isCanceled){
                transactionIconView.setBackgroundResource(R.drawable.icon_bg_failed)
                penarikanAmount.setTextColor(Color.parseColor("#D6B9FE"))
                transactionIconView.setImageResource(R.drawable.ic_baseline_warning_24)
            }
            else{
                transactionIconView.setBackgroundResource(R.drawable.icon_bg_warning)
                penarikanAmount.setTextColor(Color.parseColor("#EDC9AA"))
                transactionIconView.setImageResource(R.drawable.ic_baseline_timelapse_24)

            }



            // on item click
            holder.itemView.setOnClickListener {
                onItemClickListener?.let { it(item) }
            }
        }
    }

    // on item click listener
    private var onItemClickListener: ((Penarikan) -> Unit)? = null

    fun setOnItemClickListener(listener: (Penarikan) -> Unit) {
        onItemClickListener = listener
    }
}
