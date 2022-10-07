package com.sportportal.adapter

import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sportportal.R
import com.sportportal.databinding.ItemScheduleLayoutBinding
import com.sportportal.extra.Harga
import com.sportportal.extra.JadwalListener
import com.sportportal.extra.prettyCount
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class JadwalAdapter(val data : ArrayList<Harga>, private val picked : JadwalListener, private val unavailable : ArrayList<String>, private val booked : ArrayList<String>, private val itemSelected : ArrayList<Harga>, val date : String) : RecyclerView.Adapter<JadwalAdapter.JadwalVH>() {



    inner class JadwalVH(val binding: ItemScheduleLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JadwalVH {
        val binding =
            ItemScheduleLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JadwalVH(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: JadwalVH, position: Int) {

        val currentHour = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH"))
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        val today = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        var dateInSelected = false
        for(itemSelected in itemSelected){
            if(date == itemSelected.date){
                dateInSelected = true
                println("dateInSelectednya true")
                break
            }
        }

        val item = data[position]
        holder.binding.apply {

            "${item.jam}.00".also { jam.text = it }
            if(item.harga == ""){
                price.text = prettyCount(0)
            }
            else{
                price.text = prettyCount(item.harga.toDouble())
            }


            //Jika Waktu lewat
            if( date == today && currentHour.toInt() >= item.jam.toInt()){
                layout.setBackgroundResource(R.color.background_jadwal)
                foreground.visibility = View.VISIBLE
                item.isUnTime = true
            }else{
                item.isUnTime = false
            }


            //Jika Unavailable
            for(unTime in unavailable){
                if (item.jam == unTime){
                    layout.setBackgroundResource(R.color.background_jadwal)
                    foreground.visibility = View.VISIBLE
                    item.isUnTime = true
                    break
                }
            }

            //Jika Booked
            for(unTime in booked){
                if (item.jam == unTime){
                    layout.setBackgroundResource(R.color.background_jadwal)
                    foreground.visibility = View.VISIBLE
                    item.isUnTime = true
                    break
                }
            }

            //Jika tanggal dalam itemSelected
            if(dateInSelected){
                println("masuk if dateInSelected")
                for(itemSelected in itemSelected){
                    if(date == itemSelected.date && item.jam == itemSelected.jam){
                        item.shouldClear = true
                        println("Jadi should clear")
                    }
                }
            }

            if(item.shouldClear){
                layout.setBackgroundResource(R.color.colorPrimary)
                jam.setTextColor(Color.WHITE)
                price.setTextColor(Color.WHITE)
            }



            // on item click
            holder.itemView.setOnClickListener {
                if(item.shouldClear){
                    for(isiItemSelected in itemSelected){
                        if(isiItemSelected.date == date && isiItemSelected.jam == item.jam){
                            val index = itemSelected.indexOf(isiItemSelected)
                            itemSelected.removeAt(index)
                            item.shouldClear = false
                            layout.setBackgroundResource(R.color.white)
                            jam.setTextColor(Color.BLACK)
                            price.setTextColor(Color.BLACK)
                            println("Kehapus")
                            break
                        }

                    }
//                    Snackbar.make(
//                        this.root,
//                        "Gunakan Tombol Clear",
//                        Snackbar.LENGTH_LONG
//                    )
//                        .apply {
//                            show()
//                        }
                }
                else if(!item.isUnTime){
                    if(item.isSelected){
                        layout.setBackgroundResource(R.color.white)
                        jam.setTextColor(Color.BLACK)
                        price.setTextColor(Color.BLACK)
                        item.isSelected = false
                        if(getSelected().size == 0){
                            picked.onScheduleAction(false)

                        }
                    } else{
                        println("else if yang else")
                        layout.setBackgroundResource(R.color.colorPrimary)
                        jam.setTextColor(Color.WHITE)
                        price.setTextColor(Color.WHITE)
                        layout.isEnabled = false
                        item.isSelected = true
                        item.date = date
                        picked.onScheduleAction(true)
                    }
                }

            }
        }
    }

    // on item click listener
    private var onItemClickListener: ((Harga) -> Unit)? = null

    fun setOnItemClickListener(listener: (Harga) -> Unit) {
        onItemClickListener = listener
    }

    fun getSelected (): ArrayList<Harga>{
        val selected = ArrayList<Harga>()
        for (data in data){
            if (data.isSelected){
                selected.add(data)
            }
        }
        return selected
    }
}
