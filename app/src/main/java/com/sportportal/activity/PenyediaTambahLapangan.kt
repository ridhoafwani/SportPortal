package com.sportportal.activity

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.ArrayAdapter
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
import com.sportportal.databinding.ActivityPenyediaTambahLapanganBinding
import com.sportportal.databinding.DialogInfoBinding
import com.sportportal.extra.Constant
import com.sportportal.extra.Unavailable

@Suppress("DEPRECATION")
class PenyediaTambahLapangan : AppCompatActivity() {

    private lateinit var binding: ActivityPenyediaTambahLapanganBinding
    private lateinit var adapter: UnavailableAdapter
    private lateinit var recyclerView: RecyclerView
    private var GALLERY_REQUEST_CODE = 71
    private var fiileUrl = ""
    private var db = FirebaseFirestore.getInstance()
    private val auth : FirebaseAuth get() = Login.user
    private lateinit var progressDialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPenyediaTambahLapanganBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Tambah Lapangan"

        adapter = UnavailableAdapter(initUntime())
        recyclerView = binding.scheduleList
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.layoutManager = GridLayoutManager(this,4)
        recyclerView.adapter = adapter

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
                    addData()
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

    private fun addData(){

        val lapangan: MutableMap<String, Any> = HashMap()
        lapangan["penyedia"] = auth.currentUser?.uid.toString()
        lapangan["title"] = binding.addLapanganFormLayout.etNamaLapangan.text.toString()
        lapangan["address"] = binding.addLapanganFormLayout.etLokasi.text.toString()
        lapangan["map"] = binding.addLapanganFormLayout.etPeta.text.toString()
        lapangan["category"] = binding.addLapanganFormLayout.etKategori.text.toString()
        lapangan["lowest_price"] = "belum tersedia"

        val docref = db.collection("lapangan").document()
        docref
            .set(lapangan)
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

        dialogInfoBinding.title.text = "Lapangan Ditambahkan"
        dialogInfoBinding.content.text = "Data Lapangan anda berhasil ditambahakn, silahkan cek pada menu Daftar Lapangan"
        dialogInfoBinding.btClose.text = "Lapangan"

        dialogInfoBinding.btClose.setOnClickListener {
            goToLapangan()
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.attributes = lp

        Handler().postDelayed({
            dialog.dismiss()
            goToLapangan()
        }, 3000)
    }

    private fun goToLapangan(){
        val intent = Intent(baseContext,PenyediaLapanganList::class.java)
        progressDialog.dismiss()
        startActivity(intent)
        finish()
    }

    private fun showprogressdialog(){
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Menambahkan Lapangan")
        progressDialog.setMessage("Loading ... ")
        progressDialog.show()
    }


}