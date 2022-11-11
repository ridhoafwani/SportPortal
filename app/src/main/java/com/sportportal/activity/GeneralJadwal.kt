package com.sportportal.activity

import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.sportportal.adapter.JadwalAdapter
import com.sportportal.databinding.ActivityCustomerJadwalBinding
import com.sportportal.extra.*
import java.io.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class GeneralJadwal : GeneralNavigationDrawer(), JadwalListener{

    private lateinit var binding : ActivityCustomerJadwalBinding
    private lateinit var court: Court
    private lateinit var lapangan : Lapangan
    private var itemSelected = ArrayList<Harga>()
    private var unavailable = ArrayList<String>()
    private var booked = ArrayList<String>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JadwalAdapter
    private var db = FirebaseFirestore.getInstance()
    private val today = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    } else {
        TODO("VERSION.SDK_INT < O")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerJadwalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Pilih Jadwal"

        object : Thread() {
            override fun run() {
                super.run()
                try {
                    syncpaymentStatus()
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()

        val list = ArrayList<LocalDate>()
        var date = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }

        for (i in 1..30) {
            list.add(date)
            date = date.plusDays(1)
        }

        court = intent.getSerializableExtra("court") as Court
        lapangan = intent.getSerializableExtra("lapangan") as Lapangan

        binding.etWhen.DatePicker(
            this,
            "dd/MM/yyyy",
            Date()
        )
        binding.etWhen.setText(today)



        binding.etWhen.doAfterTextChanged {
            val selected = adapter.getSelected()
            for(item in selected){
                itemSelected.add(item)
            }
            booked.clear()
            getBookedTime(binding.etWhen.text.toString())
        }


        getUnavailableTime()

        //Isi default harga


        initschedule()


        binding.btnPesan.setOnClickListener {
            val selected = adapter.getSelected()
            for(item in selected){
                itemSelected.add(item)
            }


            val bundle = Bundle().apply {
                putSerializable("court", court as Serializable)
                putSerializable("lapangan", lapangan as Serializable)
                putSerializable("harga", itemSelected as Serializable)
                putString("bookDate", binding.etWhen.text.toString())
            }

            moveToCheckput(bundle)
        }

        binding.btnReset.setOnClickListener {
            itemSelected.clear()
            initschedule()
        }

        binding.refresh.setOnRefreshListener {
            val intent = intent
            finish()
            startActivity(intent)
        }
    }

    override fun onScheduleAction(isSelected : Boolean){
        if(itemSelected.isNotEmpty()){
            binding.btnPesan.visibility = View.VISIBLE
        }
        else if (isSelected) {
            binding.btnPesan.visibility = View.VISIBLE
        }
        else{
            binding.btnPesan.visibility = View.GONE
        }
    }

    private fun moveToCheckput(bundle: Bundle) {
        val intent = Intent(baseContext,Login::class.java)
        intent.putExtras(bundle)
        startActivity(intent)

    }


    private fun getUnavailableTime() {
        db.collection("unavailable")
            .whereEqualTo("idLapangan", lapangan.id)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val time = document.data["unavailableTime"] as String
                        unavailable.add(time)
                    }
                    getBookedTime(binding.etWhen.text.toString())


                } else {
                    Log.w(ContentValues.TAG, "Error getting documents.", task.exception)
                    Toast.makeText(
                        this, "Error getting documents $task.exception",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

    }

    //Belum Where
    private fun getBookedTime(date: String) {
        db.collection("pesan")
            .whereEqualTo("courtId", court.id)
            .whereEqualTo("scheduleDate", date)
            .whereEqualTo("paid", true)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val time = document.data["scheduleTime"] as String
                        booked.add(time)
                    }
                    getOfflineBookedTime(date)


                } else {
                    Log.w(ContentValues.TAG, "Error getting documents.", task.exception)
                    Toast.makeText(
                        this, "Error getting documents $task.exception",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

    }

    private fun getOfflineBookedTime(date: String) {
        db.collection("pesanan_offline")
            .whereEqualTo("courtId", court.id)
            .whereEqualTo("scheduleDate", date)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val time = document.data["scheduleTime"] as String
                        booked.add(time)
                    }
                    initschedule()


                } else {
                    Log.w(ContentValues.TAG, "Error getting documents.", task.exception)
                    Toast.makeText(
                        this, "Error getting documents $task.exception",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

    }

    private fun initschedule(){

        adapter = JadwalAdapter(initHarga(), this, unavailable, booked, itemSelected, binding.etWhen.text.toString())
        recyclerView = binding.scheduleList
        recyclerView.layoutManager = GridLayoutManager(this,4)
        recyclerView.adapter = adapter
    }

    private fun initHarga() : ArrayList<Harga> {
        val harga = ArrayList<Harga>()
        harga.clear()
        harga.add(Harga("00",court.jam_0))
        harga.add(Harga("01",court.jam_1))
        harga.add(Harga("02",court.jam_2))
        harga.add(Harga("03",court.jam_3))
        harga.add(Harga("04",court.jam_4))
        harga.add(Harga("05",court.jam_5))
        harga.add(Harga("06",court.jam_6))
        harga.add(Harga("07",court.jam_7))
        harga.add(Harga("08",court.jam_8))
        harga.add(Harga("09",court.jam_9))
        harga.add(Harga("10",court.jam_10))
        harga.add(Harga("11",court.jam_11))
        harga.add(Harga("12",court.jam_12))
        harga.add(Harga("13",court.jam_13))
        harga.add(Harga("14",court.jam_14))
        harga.add(Harga("15",court.jam_15))
        harga.add(Harga("16",court.jam_16))
        harga.add(Harga("17",court.jam_17))
        harga.add(Harga("18",court.jam_18))
        harga.add(Harga("19",court.jam_19))
        harga.add(Harga("20",court.jam_20))
        harga.add(Harga("21",court.jam_21))
        harga.add(Harga("22",court.jam_22))
        harga.add(Harga("23",court.jam_23))

        return harga
    }

}