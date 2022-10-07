package com.sportportal.adapter

import com.sportportal.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.sportportal.databinding.ItemCustomerLapanganLayoutBinding
import com.sportportal.databinding.ItemLapanganLayoutBinding
import com.sportportal.extra.Lapangan
import com.sportportal.extra.formatRupiah


class CustomerLapanganAdapter(val data : ArrayList<Lapangan>) : RecyclerView.Adapter<CustomerLapanganAdapter.TransactionVH>() {

    private val storageRef = FirebaseStorage.getInstance().reference


    inner class TransactionVH(val binding: ItemCustomerLapanganLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionVH {
        val binding =
            ItemCustomerLapanganLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionVH(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: TransactionVH, position: Int) {

        val item = data[position]
        holder.binding.apply {

            lapanganTitle.text = item.title
            lapanganAddress.text = item.address
            if (item.lowest_price != "belum tersedia"){
                lowest.text = formatRupiah(item.lowest_price.toDouble())
            }
            else{
                lowest.text = "(Belum Tersedia)"
            }

           storageRef.child("Lapangan/"+item.image).downloadUrl.addOnCompleteListener {
               if (it.isSuccessful){
                   val imageUrl = it.result!!
                   Glide.with(lapanganImage.context)
                       .load(imageUrl)
                       .fitCenter()
//                       .apply( RequestOptions().override(300, 300))
                       .placeholder(R.drawable.sportal)
                       .into(lapanganImage)
               }
           }




            // on item click
            holder.itemView.setOnClickListener {
                onItemClickListener?.let { it(item) }
            }
        }
    }

    // on item click listener
    private var onItemClickListener: ((Lapangan) -> Unit)? = null

    fun setOnItemClickListener(listener: (Lapangan) -> Unit) {
        onItemClickListener = listener
    }
}
