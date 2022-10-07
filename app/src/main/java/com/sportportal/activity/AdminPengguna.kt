package com.sportportal.activity

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.sportportal.adapter.PenggunaAdapter
import com.sportportal.databinding.ActivityAdminPenggunaBinding
import com.sportportal.extra.Pengguna
import java.io.Serializable

class AdminPengguna : AdminNavigationDrawer() {

    private lateinit var binding : ActivityAdminPenggunaBinding
    private var db = FirebaseFirestore.getInstance()
    private var userlist = ArrayList<Pengguna>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PenggunaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminPenggunaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Pengguna"

        getUserList()
    }

    private fun getUserList(){
        db.collection("users_extra")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val id = document.id
                    val name = document.data["name"] as String
                    val email = document.data["email"] as String
                    var image = ""
                    if (document.data["image"] != null){
                        image = document.data["image"] as String
                    }
                    val role = document.data["role"] as String

                    userlist.add(Pengguna(id,name,email,image,role))
                }

                adapter = PenggunaAdapter(userlist)
                recyclerView = binding.recyclerView
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = adapter

                adapter.setOnItemClickListener {
                    val bundle = Bundle().apply {
                        putSerializable("pengguna", it as Serializable)
                    }

                    moveToProfilePengguna(bundle)

                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }

    private fun moveToProfilePengguna(bundle : Bundle){
        val intent = Intent(baseContext,AdminProfilePengguna::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}