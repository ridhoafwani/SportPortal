package com.sportportal.extra

import java.io.Serializable

data class PesananRead(
    val idItemPesanan: String,
    val courtId : String,
    val amount: String,
    val scheduleDate : String,
    val scheduleTime : String,
    var courtName : String
): Serializable
