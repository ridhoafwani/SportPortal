package com.sportportal.extra

import java.io.Serializable

data class PesananOffline(
    val id: String,
    val courtId : String,
    val courtName : String,
    val amount: String,
    val scheduleDate : String,
    val scheduleTime : String,
    val lapName : String,
    val orderBy : String
): Serializable
