package com.sportportal.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sportportal.R
import com.sportportal.databinding.ItemUnavailableLayoutBinding
import com.sportportal.extra.*


class UnavailableAdapter(val data : ArrayList<Unavailable>, private var clikable: Boolean = true) : RecyclerView.Adapter<UnavailableAdapter.JadwalVH>() {

    inner class JadwalVH(val binding: ItemUnavailableLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JadwalVH {
        val binding =
            ItemUnavailableLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JadwalVH(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: JadwalVH, position: Int) {


        val item = data[position]
        holder.binding.apply {

            jam.text = item.jam + ".00"
            if(item.isSelected){
                layout.setBackgroundResource(R.color.colorPrimary)
                jam.setTextColor(Color.WHITE)
            }

            // on item click
            holder.itemView.setOnClickListener {

                if(clikable){
                    if(item.isSelected){
                        layout.setBackgroundResource(R.color.white)
                        jam.setTextColor(Color.BLACK)
                        item.isSelected = false

                    } else{
                        layout.setBackgroundResource(R.color.colorPrimary)
                        jam.setTextColor(Color.WHITE)
                        layout.isEnabled = false
                        item.isSelected = true
                    }
                }

            }
        }
    }

    // on item click listener
    private var onItemClickListener: ((Unavailable) -> Unit)? = null

    fun setOnItemClickListener(listener: (Unavailable) -> Unit) {
        onItemClickListener = listener
    }

    fun getSelected (): ArrayList<Unavailable>{
        val selected = ArrayList<Unavailable>()
        for (data in data){
            if (data.isSelected){
                selected.add(data)
            }
        }
        return selected
    }
}
