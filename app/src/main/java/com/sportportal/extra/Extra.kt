package com.sportportal.extra

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Build
import android.os.StrictMode
import android.util.Log
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.xendit.Xendit
import com.xendit.model.Invoice
import com.xendit.model.Payout
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow


fun TextInputEditText.DatePicker(
    context: Context,
    format: String,
    maxDate: Date? = null
) {
    isFocusableInTouchMode = false
    isClickable = true
    isFocusable = false

    val myCalendar = Calendar.getInstance()
    val datePickerOnDataSetListener =
        DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, monthOfYear)
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val sdf = SimpleDateFormat(format, Locale.UK)
            setText(sdf.format(myCalendar.time))
        }

    setOnClickListener {
        DatePickerDialog(
            context,
            datePickerOnDataSetListener,
            myCalendar
                .get(Calendar.YEAR),
            myCalendar.get(Calendar.MONTH),
            myCalendar.get(Calendar.DAY_OF_MONTH)
        ).run {
            maxDate?.time?.also { datePicker.minDate = it }
            show()
        }


    }
}

fun formatRupiah(number: Double): String? {
    val localeID = Locale("in", "ID")
    val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
    formatRupiah.maximumFractionDigits=0
    return formatRupiah.format(number)
}


fun syncpaymentStatus(){
    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)
    Xendit.Opt.apiKey = "xnd_development_5PGeUn6WAWZQYHvSUeOEPtiwfhLdJNEgd59z8m7AXq7PNKHcMReOtdgsaqeSI"
    var invoice : Array<Invoice>
    val pesanan = ArrayList<PaymentStatus>()
    val today = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        LocalDate.now()
    } else {
        TODO("VERSION.SDK_INT < O")
    }


    val db = FirebaseFirestore.getInstance()

    db.collection("pesan")
        .whereEqualTo("paid", false)
        .get()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    val scheduleDate = document.data["scheduleDate"] as String
                    if (today <= LocalDate.parse(
                            scheduleDate,
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        )
                    ) {
                        val pesananId = document.id
                        val xenditPaymentId = document.data["xenditPaymentId"] as String
                        val paid = document.data["paid"] as Boolean

                        //add data
                        pesanan.add(PaymentStatus(pesananId, xenditPaymentId, paid))
                    }

                }

                //Get Setteled Invoice from xendit
                try {
                    val params: MutableMap<String, Any> = HashMap()
                    params["statuses"] = "[\"SETTLED\",\"PAID\"]"
                    params["limit"] = 20
                    invoice = Invoice.getAll(params)
                    checkPaymentStatus(invoice, pesanan)

                } catch (e: Exception) {
                    println(e)
                }


            } else {
                Log.w(TAG, "Error getting documents.", task.exception)
            }
        }

}

fun checkPaymentStatus(invoice: Array<Invoice>, pesanan: ArrayList<PaymentStatus>) {

    for ( item in pesanan ){
        for ( inv in invoice ){
            if ( item.xenditPaymentId == inv.externalId ){
                val db = FirebaseFirestore.getInstance()

                val docRef = db.collection("pesan").document(item.pesananId)
                docRef
                    .update("paid", true)
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }

                val trDocRef = db.collection("transaksi").document(inv.id)
                trDocRef
                    .update("paid", true)
                    .addOnSuccessListener {
                        addSaldo(inv.amount.toString(),inv.description)
                        Log.d(TAG, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
            }
        }
    }

}

fun prettyCount(number: Number): String? {
    val suffix = charArrayOf(' ', 'k', 'M', 'B', 'T', 'P', 'E')
    val numValue = number.toLong()
    val value = floor(log10(numValue.toDouble())).toInt()
    val base = value / 3
    return if (value >= 3 && base < suffix.size) {
        DecimalFormat("#0").format(
            numValue / 10.0.pow((base * 3).toDouble())
        ) + suffix[base]
    } else {
        DecimalFormat("#,##0").format(numValue)
    }
}

fun syncPayoutStatus(){
    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
    StrictMode.setThreadPolicy(policy)
    Xendit.Opt.apiKey = "xnd_development_5PGeUn6WAWZQYHvSUeOEPtiwfhLdJNEgd59z8m7AXq7PNKHcMReOtdgsaqeSI"


    val db = FirebaseFirestore.getInstance()

    db.collection("penarikan")
        .whereEqualTo("isClaimed", false)
        .whereEqualTo("isCanceled", false)
        .get()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    val payoutId = document.id
                    val xenditPayoutId = document.data["xenditPayoutId"] as String
                    val amount = document.data["amount"] as String
                    val idPenarik = document.data["idPenarik"] as String

                    //Get Payout from xendit
                    try {
                        val payout = Payout.getPayout(xenditPayoutId)
                        if (payout.status!= "PENDING"){
                            if(payout.status == "COMPLETED" || payout.status == "CLAIMED"){

                                val docRef = db.collection("penarikan").document(payoutId)
                                docRef
                                    .update("isClaimed", true)
                                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                                    .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }

                            }
                            else{

                                val docRef = db.collection("penarikan").document(payoutId)
                                docRef
                                    .update("isCanceled", true)
                                    .addOnSuccessListener {
                                        addSaldo(amount,idPenarik)
                                        Log.d(TAG, "DocumentSnapshot successfully updated!") }
                                    .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }

                            }
                        }

                    } catch (e: Exception) {
                        println(e)
                    }

                }


            } else {
                Log.w(TAG, "Error getting documents.", task.exception)
            }
        }

}

fun addSaldo(amount: String, uid:String){
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("users_extra").document(uid)
    docRef.get()
        .addOnSuccessListener { document ->
            if (document != null) {
                Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                val saldoString = document.data?.get("saldo") as String
                var saldo = saldoString.toInt()
                saldo += amount.toInt()

                docRef
                    .update("saldo", saldo.toString())
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }

            } else {
                Log.d(TAG, "No such document")
            }
        }
        .addOnFailureListener { exception ->
            Log.d(TAG, "get failed with ", exception)
        }
}

fun cutSaldo(amount: String, uid:String){
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("users_extra").document(uid)
    docRef.get()
        .addOnSuccessListener { document ->
            if (document != null) {
                Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                val saldoString = document.data?.get("saldo") as String
                var saldo = saldoString.toInt()
                saldo -= amount.toInt()

                docRef
                    .update("saldo", saldo.toString())
                    .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                    .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }

            } else {
                Log.d(TAG, "No such document")
            }
        }
        .addOnFailureListener { exception ->
            Log.d(TAG, "get failed with ", exception)
        }
}

fun cekLowestPrice(lapanganId : String){

    val db = FirebaseFirestore.getInstance()

    var lowestPrice = 10000000

    db.collection("court")
        .whereEqualTo("idLapangan", lapanganId)
        .get()
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result!!) {
                    for (i in 0 until 24) {
                        val price_in_hour = document.data[i.toString()] as String
                        var price = 10000000
                        if (price_in_hour != "" && price_in_hour != "0"){
                            price = price_in_hour.toInt()
                        }
                        if(lowestPrice > price){
                            lowestPrice = price
                        }
                    }
                }

                // update lowest price
                updateLowestPrice(lapanganId, lowestPrice)
            } else {
                updateLowestPrice(lapanganId, lowestPrice)
                Log.w(TAG, "Error getting documents.", task.exception)
            }
        }

}

fun updateLowestPrice(id : String, lowestPrice:Int){
    val db = FirebaseFirestore.getInstance()
    var lowest = "Belum Tersedia"
    if (lowestPrice != 10000000){
        lowest = lowestPrice.toString()
    }

    val docRef = db.collection("lapangan").document(id)
    docRef
        .update("lowest_price", lowest)
        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
        .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
}

//fun cekaja(){
//    val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
//    StrictMode.setThreadPolicy(policy)
//    Xendit.Opt.apiKey = "xnd_development_5PGeUn6WAWZQYHvSUeOEPtiwfhLdJNEgd59z8m7AXq7PNKHcMReOtdgsaqeSI"
//    val params: MutableMap<String, Any> = HashMap()
//    params["external_id"] = "xxx"
//    params["amount"] = 10000
//    params["email"] = "zikrurridhoafwani@gmail.com"
//
//    val payout = Payout.createPayout(params)
//    println(payout.id)
//
//}



