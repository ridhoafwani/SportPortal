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
import com.sportportal.databinding.ActivityPenyediaTambahCourtBinding
import com.sportportal.databinding.DialogInfoBinding
import com.sportportal.extra.Lapangan
import com.sportportal.extra.cekLowestPrice

@Suppress("DEPRECATION")
class PenyediaTambahCourt : PenyediaNavigationDrawer() {

    private lateinit var binding : ActivityPenyediaTambahCourtBinding
    private var db = FirebaseFirestore.getInstance()
    private lateinit var progressDialog : ProgressDialog
    private lateinit var idLapangan: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPenyediaTambahCourtBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Tambah Court"

        idLapangan = intent.getStringExtra("idLapangan") as String

        binding.btnIsiSemua.setOnClickListener {
            initForm(binding.priceAll.text.toString())
        }

        binding.btnAdd.setOnClickListener {
            showprogressdialog()

            val court_data: MutableMap<String, Any> = HashMap()
            court_data["idLapangan"] = idLapangan
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


            db.collection("court")
                .add(court_data)
                .addOnSuccessListener { documentReference ->
                    Log.d(
                        ContentValues.TAG,
                        "DocumentSnapshot added with ID: " + documentReference.id
                    )
                    cekLowestPrice(idLapangan)
                    showCustomDialog()
                }
                .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error adding document", e)
                }
        }
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

        dialogInfoBinding.title.text = "Court Ditambahkan"
        dialogInfoBinding.content.text = "Data court anda berhasil ditambahkan, silahkan cek pada menu detail lapangan"
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
        progressDialog.setTitle("Menambahkan Court")
        progressDialog.setMessage("Loading ... ")
        progressDialog.show()
    }

    private fun initForm(harga:String){
        binding.price00.setText(harga)
        binding.price01.setText(harga)
        binding.price02.setText(harga)
        binding.price03.setText(harga)
        binding.price04.setText(harga)
        binding.price05.setText(harga)
        binding.price06.setText(harga)
        binding.price07.setText(harga)
        binding.price08.setText(harga)
        binding.price09.setText(harga)
        binding.price10.setText(harga)
        binding.price11.setText(harga)
        binding.price12.setText(harga)
        binding.price13.setText(harga)
        binding.price14.setText(harga)
        binding.price15.setText(harga)
        binding.price16.setText(harga)
        binding.price17.setText(harga)
        binding.price18.setText(harga)
        binding.price19.setText(harga)
        binding.price20.setText(harga)
        binding.price21.setText(harga)
        binding.price22.setText(harga)
        binding.price23.setText(harga)
    }
}