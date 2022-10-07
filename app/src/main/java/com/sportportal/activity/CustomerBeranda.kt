package com.sportportal.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sportportal.R
import com.sportportal.adapter.CustomerLapanganAdapter
import com.sportportal.adapter.LapanganAdapter
import com.sportportal.databinding.ActivityCustomerBerandaBinding
import com.sportportal.extra.Lapangan
import com.sportportal.extra.syncpaymentStatus
import java.io.Serializable

class CustomerBeranda : UserNavigationDrawerActivity() {

    private lateinit var binding : ActivityCustomerBerandaBinding
    private val auth : FirebaseAuth get() = Login.user
    private var db = FirebaseFirestore.getInstance()
    private var data = ArrayList<Lapangan>()
    private var futsall = ArrayList<Lapangan>()
    private var badminton = ArrayList<Lapangan>()
    private var basket = ArrayList<Lapangan>()
    private var voli = ArrayList<Lapangan>()
    private var tenis = ArrayList<Lapangan>()
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: CustomerLapanganAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerBerandaBinding.inflate(layoutInflater)
        verifiedCheck()
        this.getDataFromFirestore()
        setContentView(binding.root)

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

        supportActionBar?.title = "Lapangan"

        binding.filterAll.setOnClickListener {
            filter("All")
            backgroundCategory(all = true)
        }

        binding.filterFutsall.setOnClickListener {
            filter("Futsall")
            backgroundCategory(futsall = true)
        }

        binding.filterBadminton.setOnClickListener {
            filter("Badminton")
            backgroundCategory(badminton = true)
        }

        binding.filterBasket.setOnClickListener {
            filter("Basket")
            backgroundCategory(basket = true)
        }

        binding.filterTenis.setOnClickListener {
            filter("Tenis")
            backgroundCategory(tenis = true)
        }

        binding.filterVoli.setOnClickListener {
            filter("Voli")
            backgroundCategory(voli = true)
        }

    }

    private fun getDataFromFirestore() {
        db.collection("lapangan")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    data.clear()
                    futsall.clear()
                    badminton.clear()
                    basket.clear()
                    voli.clear()
                    tenis.clear()
                    for (document in task.result!!) {
                        val id = document.id
                        val title = document.data["title"] as String
                        val address = document.data["address"] as String
                        val category = document.data["category"] as String
                        val image = (document.id + ".jpg")
                        val map = document.data["map"] as String
                        val penyedia = document.data["penyedia"] as String
                        val lowest_price = document.data["lowest_price"] as String
                        data.add(Lapangan(id, title, address, map, category, image, penyedia,lowest_price))
                        when (category) {
                            "Futsall" -> {
                                futsall.add(
                                    Lapangan(
                                        id,
                                        title,
                                        address,
                                        map,
                                        category,
                                        image,
                                        penyedia,
                                        lowest_price
                                    )
                                )
                            }
                            "Badminton" -> {
                                badminton.add(
                                    Lapangan(
                                        id,
                                        title,
                                        address,
                                        map,
                                        category,
                                        image,
                                        penyedia,
                                        lowest_price
                                    )
                                )
                            }
                            "Basket" -> {
                                basket.add(
                                    Lapangan(
                                        id,
                                        title,
                                        address,
                                        map,
                                        category,
                                        image,
                                        penyedia,
                                        lowest_price
                                    )
                                )
                            }
                            "Voli" -> {
                                voli.add(
                                    Lapangan(
                                        id,
                                        title,
                                        address,
                                        map,
                                        category,
                                        image,
                                        penyedia,
                                        lowest_price
                                    )
                                )
                            }
                            "Tenis" -> {
                                tenis.add(
                                    Lapangan(
                                        id,
                                        title,
                                        address,
                                        map,
                                        category,
                                        image,
                                        penyedia,
                                        lowest_price
                                    )
                                )
                            }
                        }
                    }

                    if (data.isEmpty()) {
                        showEmptyData()
                    } else {
                        filter("All")
                        backgroundCategory(all = true)
                    }


                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                    Toast.makeText(
                        this, "Error getting documents $task.exception",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
    }

    private fun filter (filter:String){
        when (filter) {
            "All" -> {
                hideEmptyData()
                adapter = CustomerLapanganAdapter(data)
            }

            "Futsall" -> {
                if(futsall.isEmpty()){
                    adapter = CustomerLapanganAdapter(futsall)
                    showEmptyData()
                }
                else{
                    hideEmptyData()
                    adapter = CustomerLapanganAdapter(futsall)
                }
            }

            "Badminton" -> {
                if(futsall.isEmpty()){
                    adapter = CustomerLapanganAdapter(badminton)
                    showEmptyData()
                }
                else{
                    hideEmptyData()
                    adapter = CustomerLapanganAdapter(badminton)
                }
            }

            "Basket" -> {
                if(basket.isEmpty()){
                    adapter = CustomerLapanganAdapter(basket)
                    showEmptyData()
                }
                else{
                    hideEmptyData()
                    adapter = CustomerLapanganAdapter(basket)
                }
            }

            "Voli" -> {
                if(voli.isEmpty()){
                    adapter = CustomerLapanganAdapter(voli)
                    showEmptyData()
                }
                else{
                    hideEmptyData()
                    adapter = CustomerLapanganAdapter(voli)
                }
            }

            "Tenis" -> {
                if(tenis.isEmpty()){
                    adapter = CustomerLapanganAdapter(tenis)
                    showEmptyData()
                }
                else{
                    hideEmptyData()
                    adapter = CustomerLapanganAdapter(tenis)
                }
            }
        }

        recyclerView = binding.lapanganList
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        adapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("lapangan", it as Serializable)
            }

            moveToDetails(bundle)

        }

    }

    private fun backgroundCategory(all : Boolean = false,
                                   futsall : Boolean = false,
                                   badminton : Boolean = false,
                                   voli : Boolean = false,
                                   basket : Boolean = false,
                                   tenis : Boolean = false){
        if (all){
            binding.filterAll.setBackgroundResource(R.drawable.btn_rounded_white)
        }
        else{
            binding.filterAll.setBackgroundResource(R.drawable.btn_rounded_green_300)
        }

        if (futsall){
            binding.filterFutsall.setBackgroundResource(R.drawable.btn_rounded_white)
        }
        else{
            binding.filterFutsall.setBackgroundResource(R.drawable.btn_rounded_green_300)
        }

        if (badminton){
            binding.filterBadminton.setBackgroundResource(R.drawable.btn_rounded_white)
        }
        else{
            binding.filterBadminton.setBackgroundResource(R.drawable.btn_rounded_green_300)
        }

        if (voli){
            binding.filterVoli.setBackgroundResource(R.drawable.btn_rounded_white)
        }
        else{
            binding.filterVoli.setBackgroundResource(R.drawable.btn_rounded_green_300)
        }

        if (basket){
            binding.filterBasket.setBackgroundResource(R.drawable.btn_rounded_white)
        }
        else{
            binding.filterBasket.setBackgroundResource(R.drawable.btn_rounded_green_300)
        }

        if (tenis){
            binding.filterTenis.setBackgroundResource(R.drawable.btn_rounded_white)
        }
        else{
            binding.filterTenis.setBackgroundResource(R.drawable.btn_rounded_green_300)
        }
    }

    private fun showEmptyData(){
        binding.emptyStateLayout.visibility = View.VISIBLE
    }

    private fun hideEmptyData(){
        binding.emptyStateLayout.visibility = View.GONE
    }

    private fun moveToDetails(bundle : Bundle){
        val intent = Intent(baseContext,CustomerDetailLapangan::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun verifiedCheck(){
        if(!(auth.currentUser!!.isEmailVerified)){
            startActivity(Intent(baseContext, EmailVerification::class.java))
        }
    }
}