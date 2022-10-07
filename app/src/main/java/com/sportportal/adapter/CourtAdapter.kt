package com.sportportal.adapter

import com.sportportal.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import com.sportportal.databinding.ItemCourtLayoutBinding
import com.sportportal.extra.Court


class CourtAdapter(val data : ArrayList<Court>) : RecyclerView.Adapter<CourtAdapter.TransactionVH>() {

    private val storageRef = FirebaseStorage.getInstance().reference


    inner class TransactionVH(val binding: ItemCourtLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionVH {
        val binding =
            ItemCourtLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionVH(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: TransactionVH, position: Int) {

        val item = data[position]
        holder.binding.apply {

            courtTitle.text = item.nama
            shortDesc.text = item.desc

           storageRef.child("Lapangan/"+item.image).downloadUrl.addOnCompleteListener {
               if (it.isSuccessful){
                   val imageUrl = it.result!!
                   Glide.with(lapanganImage.context)
                       .load(imageUrl).apply( RequestOptions().override(300, 300))
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
    private var onItemClickListener: ((Court) -> Unit)? = null

    fun setOnItemClickListener(listener: (Court) -> Unit) {
        onItemClickListener = listener
    }
}
