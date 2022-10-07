package com.sportportal.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sportportal.R
import com.sportportal.databinding.ItemPenggunaBinding
import com.sportportal.extra.Pengguna


class PenggunaAdapter(val data : ArrayList<Pengguna>) : RecyclerView.Adapter<PenggunaAdapter.TransactionVH>() {


    inner class TransactionVH(val binding: ItemPenggunaBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionVH {
        val binding =
            ItemPenggunaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionVH(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: TransactionVH, position: Int) {

        val item = data[position]
        holder.binding.apply {

            name.text = item.name
            email.text = item.email

            Glide.with(image.context)
                .load(item.image)
                .fitCenter()
//              .apply( RequestOptions().override(300, 300))
                .placeholder(R.drawable.profile_man)
                .into(image)


            // on item click
            holder.itemView.setOnClickListener {
                onItemClickListener?.let { it(item) }
            }
        }
    }

    // on item click listener
    private var onItemClickListener: ((Pengguna) -> Unit)? = null

    fun setOnItemClickListener(listener: (Pengguna) -> Unit) {
        onItemClickListener = listener
    }
}
