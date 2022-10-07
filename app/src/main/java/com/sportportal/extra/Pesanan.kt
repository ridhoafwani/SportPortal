package com.sportportal.extra

import java.io.Serializable

data class Pesanan(
    val idPesanan: String,
    val uid : String,
    val courtId : String,
    val amount: String,
    val scheduleDate : String,
    val scheduleTime : String,
//    val orderTime : String,
    var xenditInvoiceid : String,
    val xenditPaymentId : String,
    val paid : Boolean,
    val courtName: String
): Serializable
