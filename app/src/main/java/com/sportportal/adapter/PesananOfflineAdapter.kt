package com.sportportal.adapter

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.sportportal.activity.PenyediaPesananOfflineLapanganList
import com.sportportal.databinding.DialogInfoBinding
import com.sportportal.databinding.ItemPesananOfflineLayoutBinding
import com.sportportal.extra.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
class PesananOfflineAdapter(val data : ArrayList<PesananOffline>) : RecyclerView.Adapter<PesananOfflineAdapter.TransactionVH>() {

    private var context: Context? = null

    inner class TransactionVH(val binding: ItemPesananOfflineLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionVH {
        val binding =
            ItemPesananOfflineLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        context = parent.context
        return TransactionVH(binding)
    }

    override fun getItemCount(): Int {
        return data.size
    }


    override fun onBindViewHolder(holder: TransactionVH, position: Int) {

        val formater = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern("EEE, d MMM yyy", Locale("id", "ID"))
        } else {
            TODO("VERSION.SDK_INT < O")
        }


        val item = data[position]
        holder.binding.apply {

            orderBy.text = item.orderBy
            val schedule = LocalDate.parse(item.scheduleDate,DateTimeFormatter.ofPattern("dd/MM/yyyy")).format(formater)
            pesananDate.text = schedule
            "Jam ${item.scheduleTime}.00".also { pesananTime.text = it }

            btnDelete.setOnClickListener {
                showConfirmDialog(item)
            }

            holder.itemView.setOnClickListener {
                onItemClickListener?.let { it(item) }
            }
        }
    }

    // on item click listener
    private var onItemClickListener: ((PesananOffline) -> Unit)? = null

    fun setOnItemClickListener(listener: (PesananOffline) -> Unit) {
        onItemClickListener = listener
    }

    private fun delete(pesanan_offline: PesananOffline){
        val db = FirebaseFirestore.getInstance()
        db.collection("pesanan_offline").document(pesanan_offline.id)
            .delete()
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "DocumentSnapshot successfully deleted!")
                showCustomDialog()

                Handler().postDelayed({
                    goToLapangan()
                }, 3000)
            }
            .addOnFailureListener { e -> Log.w(ContentValues.TAG, "Error deleting document", e) }
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

        dialogInfoBinding.title.text = "Pesanan Offline Dihapus"
        dialogInfoBinding.content.text = "Data Pesanan Offline berhasil dihapus, silahkan cek pada menu Daftar Pesanan Offline"
        dialogInfoBinding.btClose.text = "Pesanan Offline"

        dialogInfoBinding.btClose.setOnClickListener {
            goToLapangan()
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.attributes = lp
    }

    private fun goToLapangan(){
        val intent = Intent(context, PenyediaPesananOfflineLapanganList::class.java)
        context?.startActivity(intent)
    }

    private fun showConfirmDialog(pesanan_offline: PesananOffline) {
        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Yakin ingin menghapus Pesanan ?")
        builder.setMessage("Pesanan akan dihapus permanent. Aksi ini tidak akan mempengaruhi saldo dan Pesanan yang sebelumnya sudah dibuat.")
        builder.setPositiveButton("Hapus"
        ) { _, _ ->
            delete(pesanan_offline)
        }
        builder.setNegativeButton("Batal", null)
        builder.show()
    }
}
