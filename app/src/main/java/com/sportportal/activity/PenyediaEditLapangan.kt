package com.sportportal.activity

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sportportal.R
import com.sportportal.adapter.UnavailableAdapter
import com.sportportal.databinding.ActivityPenyediaEditLapanganBinding
import com.sportportal.databinding.DialogInfoBinding
import com.sportportal.extra.Constant
import com.sportportal.extra.Lapangan
import com.sportportal.extra.Unavailable

@Suppress("DEPRECATION")
class PenyediaEditLapangan : AppCompatActivity() {

    private lateinit var binding : ActivityPenyediaEditLapanganBinding
    private lateinit var adapter: UnavailableAdapter
    private lateinit var recyclerView: RecyclerView
    private var GALLERY_REQUEST_CODE = 71
    private lateinit var imageUrl : Uri
    private var fiileUrl = ""
    private var db = FirebaseFirestore.getInstance()
    private val auth : FirebaseAuth get() = Login.user
    private lateinit var data_lapangan: Lapangan
    private val storageRef = FirebaseStorage.getInstance().reference
    private var unavailable = ArrayList<Unavailable>()
    private var untimeBefore = ArrayList<String>()
    private var untimeAfter = ArrayList<String>()
    private lateinit var progressDialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPenyediaEditLapanganBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Edit Lapangan"

        data_lapangan = intent.getSerializableExtra("lapangan") as Lapangan
        initform()
        getUnavailableTime(initUntime())



        val categoryAdapter =
            ArrayAdapter(
                this,
                R.layout.item_autocomplete_layout,
                Constant.category
            )

        binding.addLapanganFormLayout.etKategori.setAdapter(categoryAdapter)

        binding.btnImage.setOnClickListener {
            selectImageFromGallery()
        }

        binding.btnAdd.setOnClickListener {
            when{
                binding.addLapanganFormLayout.etNamaLapangan.text!!.isEmpty() -> {
                    binding.addLapanganFormLayout.etNamaLapangan.error = "Masukkan Nama Lapangan Anda"
                }
                binding.addLapanganFormLayout.etLokasi.text!!.isEmpty() -> {
                    binding.addLapanganFormLayout.etLokasi.error = "Masukkan Alamat Lapangan Anda"
                }
                binding.addLapanganFormLayout.etKategori.text!!.isEmpty() -> {
                    binding.addLapanganFormLayout.etKategori.error = "Masukkan Kategori Lapangan Anda"
                }
                binding.addLapanganFormLayout.etPeta.text!!.isEmpty() -> {
                    binding.addLapanganFormLayout.etPeta.error = "Masukkan Link Peta Menuju Lapangan Anda"
                }

                else ->{
                    showprogressdialog()
                    updateData()
                }
            }
        }
    }

    private fun initUntime() : ArrayList<Unavailable> {
        val unTimeList = ArrayList<Unavailable>()
        unTimeList.clear()
        unTimeList.add(Unavailable("00"))
        unTimeList.add(Unavailable("01"))
        unTimeList.add(Unavailable("02"))
        unTimeList.add(Unavailable("03"))
        unTimeList.add(Unavailable("04"))
        unTimeList.add(Unavailable("05"))
        unTimeList.add(Unavailable("06"))
        unTimeList.add(Unavailable("07"))
        unTimeList.add(Unavailable("08"))
        unTimeList.add(Unavailable("09"))
        unTimeList.add(Unavailable("10"))
        unTimeList.add(Unavailable("11"))
        unTimeList.add(Unavailable("12"))
        unTimeList.add(Unavailable("13"))
        unTimeList.add(Unavailable("14"))
        unTimeList.add(Unavailable("15"))
        unTimeList.add(Unavailable("16"))
        unTimeList.add(Unavailable("17"))
        unTimeList.add(Unavailable("18"))
        unTimeList.add(Unavailable("19"))
        unTimeList.add(Unavailable("20"))
        unTimeList.add(Unavailable("21"))
        unTimeList.add(Unavailable("22"))
        unTimeList.add(Unavailable("23"))

        return unTimeList
    }

    private fun initform(){
        binding.addLapanganFormLayout.etNamaLapangan.setText(data_lapangan.title)
        binding.addLapanganFormLayout.etLokasi.setText(data_lapangan.address)
        binding.addLapanganFormLayout.etKategori.setText(data_lapangan.category)
        binding.addLapanganFormLayout.etPeta.setText(data_lapangan.map)

        //Image

        storageRef.child("Lapangan/"+data_lapangan.image).downloadUrl.addOnCompleteListener {
            if (it.isSuccessful){
                imageUrl = it.result!!
                Glide.with(binding.lapanganImg)
                    .load(imageUrl)
                    .placeholder(R.drawable.sportal)
                    .into(binding.lapanganImg)
            }
        }


    }


    private fun getUnavailableTime(untime : ArrayList<Unavailable>) {
        db.collection("unavailable")
            .whereEqualTo("idLapangan", data_lapangan.id)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val time = document.data["unavailableTime"] as String
                        val id = document.id
                        unavailable.add(Unavailable(time, id))
                        untimeBefore.add(time)
                    }
                    for (item in untime) {
                        for (unavailibleList in unavailable) {
                            if (item.jam == unavailibleList.jam) {
                                item.isSelected = true
                            }
                        }
                    }

                    adapter = UnavailableAdapter(untime)
                    recyclerView = binding.scheduleList
                    recyclerView.isNestedScrollingEnabled = false
                    recyclerView.layoutManager = GridLayoutManager(this, 4)
                    recyclerView.adapter = adapter


                } else {
                    Log.w(TAG, "Error getting documents.", task.exception)
                    Toast.makeText(
                        this, "Error getting documents $task.exception",
                        Toast.LENGTH_SHORT,
                    ).show()

                }
            }

    }

    private fun updateData(){

        val lapangan: MutableMap<String, Any> = HashMap()
        lapangan["penyedia"] = auth.currentUser?.uid.toString()
        lapangan["title"] = binding.addLapanganFormLayout.etNamaLapangan.text.toString()
        lapangan["address"] = binding.addLapanganFormLayout.etLokasi.text.toString()
        lapangan["map"] = binding.addLapanganFormLayout.etPeta.text.toString()
        lapangan["category"] = binding.addLapanganFormLayout.etKategori.text.toString()

        val docref = db.collection("lapangan").document(data_lapangan.id)
        docref
            .update(lapangan)
            .addOnSuccessListener {
                uploadImageToFirebase(fiileUrl,docref.id)
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e)
            }

    }

    private fun selectImageFromGallery() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Please select..."
            ),
            GALLERY_REQUEST_CODE
        )
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {

        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (requestCode == GALLERY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK
            && data != null
            && data.data != null
        ) {

            // Get the Uri of data
            val file_uri = data.data
            if (file_uri != null) {
                fiileUrl = file_uri.toString()
                Glide.with(binding.lapanganImg)
                    .load(file_uri)
                    .placeholder(R.drawable.sportal)
                    .into(binding.lapanganImg)
            }
        }
    }

    private fun uploadImageToFirebase(fileUrl: String, docRefId : String) {
        if (fileUrl != "") {
            val fileUri = fileUrl.toUri()
            val fileName = "$docRefId.jpg"

            val refStorage = FirebaseStorage.getInstance().reference.child("Lapangan/$fileName")

            refStorage.putFile(fileUri)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                        addUnavailableTime(docRefId)

                    }
                }

                .addOnFailureListener { e ->
                    print(e.message)
                }
        }
        else{
            addUnavailableTime(docRefId)
        }
    }

    private fun addUnavailableTime(docRefId: String){

        val selected = adapter.getSelected()
        for (item in selected){
            untimeAfter.add(item.jam)
        }
        untimeBefore.sort()
        untimeAfter.sort()
        println(untimeBefore)
        println(untimeAfter)

        if(untimeBefore != untimeAfter){

            for(item in unavailable){
                db.collection("unavailable").document(item.id)
                    .delete()
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
            }

            for(item in selected){
                val unavailable: MutableMap<String, Any> = HashMap()
                unavailable["idLapangan"] = docRefId
                unavailable["unavailableTime"] = item.jam

                db.collection("unavailable")
                    .add(unavailable)
                    .addOnSuccessListener { documentReference ->
                        Log.d(
                            TAG,
                            "DocumentSnapshot added with ID: " + documentReference.id
                        )
                        showCustomDialog()
                    }
                    .addOnFailureListener { e -> Log.w(TAG, "Error adding document", e)
                    }
            }

        }

        else{
            showCustomDialog()
        }




    }

    private fun moveToLapanganList() {
        val intent = Intent(baseContext,PenyediaLapanganList::class.java)
        progressDialog.dismiss()
        startActivity(intent)
        finish()
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

        dialogInfoBinding.title.text = "Lapangan Diupdate"
        dialogInfoBinding.content.text = "Data Lapangan anda berhasil diperbarui, silahkan cek pada menu Detail Lapangan"
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

    private fun showprogressdialog(){
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Memperbarui Lapangan")
        progressDialog.setMessage("Loading ... ")
        progressDialog.show()
    }
}