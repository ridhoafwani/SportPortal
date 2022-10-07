package com.sportportal.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sportportal.databinding.ItemSaldoPendapatanLayoutBinding
import com.sportportal.extra.Transaction
import com.sportportal.extra.formatRupiah
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class SaldoPesananAdapter(val data : ArrayList<Transaction>) : RecyclerView.Adapter<SaldoPesananAdapter.TransactionVH>() {


    inner class TransactionVH(val binding: ItemSaldoPendapatanLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionVH {
        val binding =
            ItemSaldoPendapatanLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

            lapanganName.text = item.namaLapangan
            val orderTime = LocalDateTime.parse(item.orderTime).format(formater)
            pesananDate.text = orderTime.toString()
            (item.itemCount + " Item").also { pesananTime.text = it }
            ("+ " + formatRupiah(item.amount.toDouble())).also { pesananAmount.text = it }

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
