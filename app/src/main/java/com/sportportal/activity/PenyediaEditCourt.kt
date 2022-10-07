package com.sportportal.activity

import android.app.Dialog
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.google.firebase.firestore.FirebaseFirestore
import com.sportportal.databinding.ActivityPenyediaEditCourtBinding
import com.sportportal.databinding.DialogInfoBinding
import com.sportportal.extra.Court
import com.sportportal.extra.cekLowestPrice

@Suppress("DEPRECATION")
class PenyediaEditCourt : PenyediaNavigationDrawer() {

    private lateinit var binding : ActivityPenyediaEditCourtBinding
    private var db = FirebaseFirestore.getInstance()
    private lateinit var court : Court
    private lateinit var progressDialog : ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPenyediaEditCourtBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Edit Court"

        court = intent.getSerializableExtra("court") as Court

        initForm()

        binding.btnAdd.setOnClickListener {
            showprogressdialog()

            val court_data: MutableMap<String, Any> = HashMap()
            court_data["idLapangan"] = court.idLapangan
            court_data["nama"] = binding.edtCourtName.text.toString()
            court_data["desc"] = binding.edtDesc.text.toString()
            court_data["0"] = binding.price00.text.toString()
            court_data["1"] = binding.price01.text.toString()
            court_data["2"] = binding.price02.text.toString()
            court_data["3"] = binding.price03.text.toString()
            court_data["4"] = binding.price04.text.toString()
            court_data["5"] = binding.price05.text.toString()
            court_data["6"] = binding.price06.text.toString()
            court_data["7"] = binding.price07.text.toString()
            court_data["8"] = binding.price08.text.toString()
            court_data["9"] = binding.price09.text.toString()
            court_data["10"] = binding.price10.text.toString()
            court_data["11"] = binding.price11.text.toString()
            court_data["12"] = binding.price12.text.toString()
            court_data["13"] = binding.price13.text.toString()
            court_data["14"] = binding.price14.text.toString()
            court_data["15"] = binding.price15.text.toString()
            court_data["16"] = binding.price16.text.toString()
            court_data["17"] = binding.price17.text.toString()
            court_data["18"] = binding.price18.text.toString()
            court_data["19"] = binding.price19.text.toString()
            court_data["20"] = binding.price20.text.toString()
            court_data["21"] = binding.price21.text.toString()
            court_data["22"] = binding.price22.text.toString()
            court_data["23"] = binding.price23.text.toString()


            val docref = db.collection("court").document(court.id)
            docref
                .set(court_data)
                .addOnSuccessListener {
                    cekLowestPrice(court.idLapangan)
                    showCustomDialog()
                }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding document", e)
                }


        }
    }

    private fun initForm(){
        binding.edtCourtName.setText(court.nama)
        binding.edtDesc.setText(court.desc)
        binding.price00.setText(court.jam_0)
        binding.price01.setText(court.jam_1)
        binding.price02.setText(court.jam_2)
        binding.price03.setText(court.jam_3)
        binding.price04.setText(court.jam_4)
        binding.price05.setText(court.jam_5)
        binding.price06.setText(court.jam_6)
        binding.price07.setText(court.jam_7)
        binding.price08.setText(court.jam_8)
        binding.price09.setText(court.jam_9)
        binding.price10.setText(court.jam_10)
        binding.price11.setText(court.jam_11)
        binding.price12.setText(court.jam_12)
        binding.price13.setText(court.jam_13)
        binding.price14.setText(court.jam_14)
        binding.price15.setText(court.jam_15)
        binding.price16.setText(court.jam_16)
        binding.price17.setText(court.jam_17)
        binding.price18.setText(court.jam_18)
        binding.price19.setText(court.jam_19)
        binding.price20.setText(court.jam_20)
        binding.price21.setText(court.jam_21)
        binding.price22.setText(court.jam_22)
        binding.price23.setText(court.jam_23)
    }

    private fun moveToLapanganList() {
        val intent = Intent(baseContext,PenyediaLapanganList::class.java)
        progressDialog.dismiss()
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

        dialogInfoBinding.title.text = "Court Diupdate"
        dialogInfoBinding.content.text = "Data court anda berhasil diperbarui, silahkan cek pada menu detail lapangan"
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
        progressDialog.setTitle("Memperbarui Court")
        progressDialog.setMessage("Loading ... ")
        progressDialog.show()
    }
}