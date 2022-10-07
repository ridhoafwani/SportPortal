package com.sportportal.adapter

import com.sportportal.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.sportportal.databinding.ItemLapanganLayoutBinding
import com.sportportal.extra.Lapangan


class LapanganAdapter(val data : ArrayList<Lapangan>) : RecyclerView.Adapter<LapanganAdapter.TransactionVH>() {

    private val storageRef = FirebaseStorage.getInstance().reference


    inner class TransactionVH(val binding: ItemLapanganLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionVH {
        val binding =
            ItemLapanganLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
