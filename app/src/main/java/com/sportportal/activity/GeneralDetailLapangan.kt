package com.sportportal.activity


import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sportportal.R
import com.sportportal.adapter.CourtAdapter
import com.sportportal.databinding.ActivityCustomerDetailLapanganBinding
import com.sportportal.extra.Court
import com.sportportal.extra.Lapangan
import com.sportportal.extra.formatRupiah
import com.sportportal.utils.Tools
import com.sportportal.utils.ViewAnimation
import java.io.Serializable


class GeneralDetailLapangan : GeneralNavigationDrawer() {

    private val storageRef = FirebaseStorage.getInstance().reference
    private val db = FirebaseFirestore.getInstance()

    private lateinit var binding : ActivityCustomerDetailLapanganBinding
    private lateinit var parent_view: View
    private lateinit var nested_scroll_view : NestedScrollView

    private lateinit var data_lapangan: Lapangan
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CourtAdapter
    var court = ArrayList<Court>()

    private lateinit var price : MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerDetailLapanganBinding.inflate(layoutInflater)

        setContentView(binding.root)
        supportActionBar?.title = "Detail Lapangan"

        data_lapangan = intent.getSerializableExtra("lapangan") as Lapangan

        parent_view = binding.parentView
        initToolbar()
        initComponent()
        initData()
        getCourtList()

    }

    private fun initData(){
        binding.title.text = data_lapangan.title
        binding.category.text = data_lapangan.category

        storageRef.child("Lapangan/"+data_lapangan.image).downloadUrl.addOnCompleteListener {
            if (it.isSuccessful){
                val imageUrl = it.result!!
                Glide.with(binding.image.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.sportal)
                    .into(binding.image)
            }
        }

        binding.tvLokasi.text = data_lapangan.address
    }

    private fun initToolbar() {
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        Tools.setSystemBarColor(this)
    }

    private fun initComponent() {
        // nested scrollview
        nested_scroll_view = binding.nestedScrollView

        // section lokasi
        binding.btToggleLokasi.setOnClickListener { view ->
            toggleSection(
                view,
                binding.lytExpandLokasi
            )
        }

        // section court
        binding.btToggleCourt.setOnClickListener { view ->
            toggleSection(
                view,
                binding.lytExpandCourt
            )
        }

        // expand first court
        toggleArrow(binding.btToggleCourt)
        binding.lytExpandCourt.visibility = View.VISIBLE

        //map button
        binding.btnMap.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(data_lapangan.map))
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")
            startActivity(intent)
        }

    }

    private fun toggleSection(bt: View, lyt: View) {
        val show = toggleArrow(bt)
        if (show) {
            ViewAnimation.expand(lyt) {
                fun onFinish() {
                    Tools.nestedScrollTo(nested_scroll_view, lyt)
                }
            }
        } else {
            ViewAnimation.collapse(lyt)
        }
    }

    private fun toggleArrow(view: View): Boolean {
        return if (view.rotation == 0f) {
            view.animate().setDuration(200).rotation(180f)
            true
        } else {
            view.animate().setDuration(200).rotation(0f)
            false
        }
    }

    private fun getCourtList() {
        db.collection("court")
            .whereEqualTo("idLapangan", data_lapangan.id)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    court.clear()
                    for (document in task.result!!) {
                        val id = document.id
                        val idLapangan = document.data["idLapangan"] as String
                        val nama = document.data["nama"] as String
                        val desc = document.data["desc"] as String
                        val jam: MutableList<String> = mutableListOf()
                        for (i in 0 until 24) {
                            var price_in_hour = document.data[i.toString()] as String
                            if(price_in_hour == ""){
                                price_in_hour = "0"
                            }
                            jam.add(price_in_hour)
                        }
                        price = jam
                        court.add(
                            Court(
                                id,
                                idLapangan,
                                nama,
                                data_lapangan.image,
                                desc,
                                price[0],
                                price[1],
                                price[2],
                                price[3],
                                price[4],
                                price[5],
                                price[6],
                                price[7],
                                price[8],
                                price[9],
                                price[10],
                                price[11],
                                price[12],
                                price[13],
                                price[14],
                                price[15],
                                price[16],
                                price[17],
                                price[18],
                                price[19],
                                price[20],
                                price[21],
                                price[22],
                                price[23]
                            )
                        )

                    }

                    try{
                        val price = "Mulai dari " + formatRupiah(data_lapangan.lowest_price.toDouble())
                        binding.price.text = price
                    }
                    catch (e:Exception){
                        binding.price.text = "Belum Tersedia"
                    }

                    if (court.isEmpty()) {
                        adapter = CourtAdapter(court)
                        recyclerView = binding.courtList
                        recyclerView.layoutManager = LinearLayoutManager(this)
                        recyclerView.adapter = adapter

                    } else {
                        adapter = CourtAdapter(court)
                        recyclerView = binding.courtList
                        recyclerView.layoutManager = LinearLayoutManager(this)
                        recyclerView.adapter = adapter

                    }

                    adapter.setOnItemClickListener {
                        val bundle = Bundle().apply {
                            putSerializable("court", it as Serializable)
                            putSerializable("lapangan", data_lapangan as Serializable)
                        }

                        moveToJadwal(bundle)

                    }

                } else {
                    Log.w(ContentValues.TAG, "Error getting documents.", task.exception)
                    Toast.makeText(
                        this, "Error getting documents $task.exception",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
    }

    private fun moveToJadwal(bundle: Bundle) {
        val intent = Intent(baseContext,GeneralJadwal::class.java)
        intent.putExtras(bundle)
        startActivity(intent)

    }

}