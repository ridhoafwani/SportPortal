package com.sportportal.activity

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.sportportal.R
import com.sportportal.databinding.ActivityAdminEditRoleBinding
import com.sportportal.databinding.DialogInfoBinding
import com.sportportal.extra.Pengguna

@Suppress("DEPRECATION")
class AdminEditRole : AdminNavigationDrawer() {

    private lateinit var binding : ActivityAdminEditRoleBinding
    private lateinit var user : Pengguna
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminEditRoleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Role Pengguna"

        user = intent.getSerializableExtra("pengguna") as Pengguna
        initView()

        binding.btnSave.setOnClickListener {
            val chipId = binding.roleGroup.checkedChipId
            var role = ""
            when (chipId){
                binding.admin.id ->{
                    role = "admin"
                }

                binding.penyedia.id ->{
                    role = "penyedia"
                }

                binding.customer.id ->{
                    role = "customer"
                }
            }

            if(role != ""){
                updateRole(role)
            }
            else{
                Snackbar.make(
                    binding.root,
                    "Pilih Salah Satu Role",
                    Snackbar.LENGTH_SHORT
                )
                    .apply {

                        show()
                    }
            }
        }

    }

    private fun initView(){
        binding.name.text = user.name
        binding.email.text = user.email
        binding.role.text = user.role

        Glide.with(binding.image.context)
            .load(user.image)
            .fitCenter()
            .placeholder(R.drawable.profile_man)
            .into(binding.image)
    }

    private fun updateRole(role:String){
        val washingtonRef = db.collection("users_extra").document(user.id)
        washingtonRef
            .update("role", role)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
                showCustomDialog()
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
    }

    private fun moveToPenngguna(){
        val intent = Intent(baseContext,AdminPengguna::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
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

        dialogInfoBinding.title.text = "Role Diupdate"
        dialogInfoBinding.content.text = "Data Pengguna berhasil diperbarui, silahkan cek pada menu Pengguna"
        dialogInfoBinding.btClose.text = "Pengguna"

        dialogInfoBinding.btClose.setOnClickListener {
            moveToPenngguna()
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.attributes = lp
        Handler().postDelayed({
            dialog.dismiss()
            moveToPenngguna()
        }, 3000)
    }
}