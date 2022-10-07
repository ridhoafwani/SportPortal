package com.sportportal.activity


import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sportportal.R
import com.sportportal.adapter.PenyediaCourtAdapter
import com.sportportal.adapter.UnavailableAdapter
import com.sportportal.databinding.ActivityAdminDetailLapanganBinding
import com.sportportal.databinding.DialogInfoBinding
import com.sportportal.extra.Court
import com.sportportal.extra.Lapangan
import com.sportportal.extra.Unavailable
import com.sportportal.utils.Tools
import com.sportportal.utils.ViewAnimation

@Suppress("DEPRECATION")
class AdminDetailLapangan : AdminNavigationDrawer() {

    private lateinit var binding : ActivityAdminDetailLapanganBinding
    private val storageRef = FirebaseStorage.getInstance().reference
    val db = FirebaseFirestore.getInstance()

    private lateinit var parent_view: View
    private var unavailable = ArrayList<Unavailable>()

    private lateinit var nested_scroll_view : NestedScrollView

    private lateinit var data_lapangan: Lapangan
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PenyediaCourtAdapter
    private lateinit var unavailableRecyclerView: RecyclerView
    private lateinit var unavailableAdapter: UnavailableAdapter
    var court = ArrayList<Court>()

    private lateinit var price : MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDetailLapanganBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Detail Lapangan"

        data_lapangan = intent.getSerializableExtra("lapangan") as Lapangan

        parent_view = binding.parentView
        initToolbar()
        initComponent()
        initData()
        getCourtList()
        getUnavailableTime()
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
        supportActionBar!!.title = "Lapangan"
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

        // section unavailable
        binding.btToggleUnavailable.setOnClickListener { view ->
            toggleSection(
                view,
                binding.lytExpandUnavailable
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

        binding.btnDelete.setOnClickListener {
            showConfirmDialog()
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
                            val price_in_hour = document.data[i.toString()] as String
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


                    if (court.isEmpty()) {
                        adapter = PenyediaCourtAdapter(court, false)
                        recyclerView = binding.courtList
                        recyclerView.layoutManager = LinearLayoutManager(this)
                        recyclerView.adapter = adapter

                    } else {
                        adapter = PenyediaCourtAdapter(court,false)
                        recyclerView = binding.courtList
                        recyclerView.layoutManager = LinearLayoutManager(this)
                        recyclerView.adapter = adapter

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

    private fun getUnavailableTime() {
        db.collection("unavailable")
            .orderBy("unavailableTime")
            .whereEqualTo("idLapangan", data_lapangan.id)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val time = document.data["unavailableTime"] as String
                        unavailable.add(Unavailable(time))
                    }

                    unavailableAdapter = UnavailableAdapter(unavailable, false)
                    unavailableRecyclerView = binding.unavailableList
                    unavailableRecyclerView.isNestedScrollingEnabled = false
                    unavailableRecyclerView.layoutManager = GridLayoutManager(this, 4)
                    unavailableRecyclerView.adapter = unavailableAdapter


                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                    Toast.makeText(
                        this, "Error getting documents $task.exception",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

    }

    private fun deleteLapangan(){
        db.collection("lapangan").document(data_lapangan.id)
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }

    private fun deleteRelatedCourt(){

        for (item in court){
            db.collection("court").document(item.id)
                .delete()
                .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
        }
    }

//    private fun moveToJadwal(bundle: Bundle) {
//        val intent = Intent(baseContext,CustomerJadwal::class.java)
//        intent.putExtras(bundle)
//        startActivity(intent)
//
//    }

    private fun moveToLapanganList() {
        val intent = Intent(baseContext,PenyediaLapanganList::class.java)
        startActivity(intent)
    }

    private fun showCustomDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        val dialogInfoBinding = DialogInfoBinding.inflate(layoutInflater)
        dialog.setContentView(dialogInfoBinding.root)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        dialogInfoBinding.title.text = "Lapangan Dihapus"
        dialogInfoBinding.content.text = "Data Lapangan anda berhasil dihapus, silahkan cek pada menu Lapangan"
        dialogInfoBinding.btClose.text = "Lapangan"

        dialogInfoBinding.btClose.setOnClickListener {
            moveToLapanganList()
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.attributes = lp
        Handler().postDelayed({
            dialog.dismiss()
            moveToLapanganList()
        }, 3000)
    }

    private fun showConfirmDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Yakin ingin menghapus Lapangan ?")
        builder.setMessage("Lapangan yang anda hapus tidak dapat dikembalikan secara otomatis. Menghapus data lapangan juga akan menghapus data lainnya yang terkait dengan lapanga. Aksi ini tidak akan mempengaruhi saldo dan Pesanan yang sebelumnya sudah dibuat. Jika hanya ingin merubah beberapa data, silahkan gunakan fitur Edit Lapangan")
        builder.setPositiveButton("Hapus"
        ) { _, _ ->
            deleteLapangan()
            deleteRelatedCourt()

            showCustomDialog()
        }
        builder.setNegativeButton("Batal", null)
        builder.show()
    }

}