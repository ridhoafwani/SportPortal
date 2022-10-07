package com.sportportal.extra

import com.xendit.model.Invoice
import java.io.Serializable

data class Transaction(
    val idTransaction: String,
    val uid : String,
    val amount: String,
    val orderTime : String,
    val namaLapangan: String,
    val itemCount: String,
    val dp : Boolean,
    @Transient val  xenditInvoice: Invoice
): Serializable
