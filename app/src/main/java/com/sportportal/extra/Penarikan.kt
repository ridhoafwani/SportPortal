package com.sportportal.extra

import com.xendit.model.Payout
import java.io.Serializable

data class Penarikan(
    val idPenarik: String,
    val namaPenarik : String,
    val amount: String,
    val isClaimed : Boolean,
    val isCanceled : Boolean,
    var time : String,
    val xenditPayoutId : String,
//    @Transient val  xenditPayout: Payout = Payout.getPayout(xenditPayoutId)
): Serializable
