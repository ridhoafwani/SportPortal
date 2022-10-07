package com.sportportal.adapter

import com.sportportal.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.sportportal.databinding.ItemCheckoutLayoutBinding
import com.sportportal.extra.Court
import com.sportportal.extra.Harga
import com.sportportal.extra.Lapangan
import com.sportportal.extra.formatRupiah


class CheckoutAdapter(val data : ArrayList<Harga>, val lapangan: Lapangan, val court: Court) : RecyclerView.Adapter<CheckoutAdapter.TransactionVH>() {

    private val storageRef = FirebaseStorage.getInstance().reference


    inner class TransactionVH(val binding: ItemCheckoutLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionVH {
        val binding =
            ItemCheckoutLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionVH(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: TransactionVH, position: Int) {

        val item = data[position]
        holder.binding.apply {

            courtName.text = court.nama
            schedule.text = item.date + " Jam " + item.jam + ".00"
            price.text = formatRupiah(item.harga.toDouble())

           storageRef.child("Lapangan/"+lapangan.image).downloadUrl.addOnCompleteListener {
               if (it.isSuccessful){
                   val imageUrl = it.result!!
                   Glide.with(image.context)
                       .load(imageUrl)
                       .placeholder(R.drawable.sportal)
                       .into(image)
               }
           }



            // on item click
            holder.itemView.setOnClickListener {
                onItemClickListener?.let { it(item) }
            }
        }
    }

    // on item click listener
    private var onItemClickListener: ((Harga) -> Unit)? = null

    fun setOnItemClickListener(listener: (Harga) -> Unit) {
        onItemClickListener = listener
    }
}
