package com.sportportal.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sportportal.databinding.ItemSaldoDitarikLayoutBinding
import com.sportportal.extra.Penarikan
import com.sportportal.extra.formatRupiah
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class SaldoPenarikanAdapter(val data : ArrayList<Penarikan>) : RecyclerView.Adapter<SaldoPenarikanAdapter.TransactionVH>() {


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
            ("- " + formatRupiah(item.amount.toDouble())).also { penarikanAmount.text = it }

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
