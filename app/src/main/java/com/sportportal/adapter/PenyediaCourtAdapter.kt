package com.sportportal.adapter

import android.app.Dialog
import com.sportportal.R
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.sportportal.activity.PenyediaEditCourt
import com.sportportal.activity.PenyediaLapanganList
import com.sportportal.databinding.DialogInfoBinding
import com.sportportal.databinding.ItemPenyediaCourtLayoutBinding
import com.sportportal.extra.Court
import com.sportportal.extra.cekLowestPrice
import java.io.Serializable


@Suppress("DEPRECATION")
class PenyediaCourtAdapter(val data : ArrayList<Court>, private val editable : Boolean = true) : RecyclerView.Adapter<PenyediaCourtAdapter.TransactionVH>() {

    private val storageRef = FirebaseStorage.getInstance().reference
    private var context: Context? = null


    inner class TransactionVH(val binding: ItemPenyediaCourtLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionVH {
        val binding =
            ItemPenyediaCourtLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        context = parent.context
        return TransactionVH(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: TransactionVH, position: Int) {

        val item = data[position]

        holder.binding.apply {

            courtTitle.text = item.nama
            shortDesc.text = item.desc

            storageRef.child("Lapangan/"+item.image).downloadUrl.addOnCompleteListener {
               if (it.isSuccessful){
                   val imageUrl = it.result!!
                   Glide.with(lapanganImage.context)
                       .load(imageUrl).apply( RequestOptions().override(300, 300))
                       .placeholder(R.drawable.sportal)
                       .into(lapanganImage)
               }
            }

            if (!editable){
                btnEdit.visibility = View.GONE
            }


            btnEdit.setOnClickListener {
                edit(item)
            }

            btnDelete.setOnClickListener {
                showConfirmDialog(item)
            }

            // on item click
            holder.itemView.setOnClickListener {
                onItemClickListener?.let { it(item) }
            }
        }
    }

    // on item click listener
    private var onItemClickListener: ((Court) -> Unit)? = null

    fun setOnItemClickListener(listener: (Court) -> Unit) {
        onItemClickListener = listener
    }

    private fun edit(court : Court){
        val bundle = Bundle().apply {
            putSerializable("court", court as Serializable)
        }

        val intent = Intent(context, PenyediaEditCourt::class.java)
        intent.putExtras(bundle)
        context?.startActivity(intent)

    }

    private fun delete(court: Court){
        val db = FirebaseFirestore.getInstance()
        db.collection("court").document(court.id)
            .delete()
            .addOnSuccessListener {

                Log.d(TAG, "DocumentSnapshot successfully deleted!")
                cekLowestPrice(court.idLapangan)
                showCustomDialog()
            }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }

    private fun showCustomDialog() {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        val dialogInfoBinding = DialogInfoBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(dialogInfoBinding.root)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        dialogInfoBinding.title.text = "Data Court Dihapus"
        dialogInfoBinding.content.text = "Data Court berhasil dihapus, silahkan cek pada menu Daftar Detail Lapangan"
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
        val intent = Intent(context, PenyediaLapanganList::class.java)
        context?.startActivity(intent)
    }

    private fun showConfirmDialog(court: Court) {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Yakin ingin menghapus Court ?")
        builder.setMessage("Court yang anda hapus tidak dapat dikembalikan secara otomatis. Aksi ini tidak akan mempengaruhi saldo dan Pesanan yang sebelumnya sudah dibuat. Jika hanya ingin merubah beberapa data, silahkan gunakan fitur Edit Court")
        builder.setPositiveButton("Hapus"
        ) { _, _ ->
            delete(court)
        }
        builder.setNegativeButton("Batal", null)
        builder.show()
    }
}
