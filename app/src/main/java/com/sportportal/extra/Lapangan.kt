package com.sportportal.extra

import java.io.Serializable

data class Lapangan(
    val id : String,
    val title : String,
    val address: String,
    val map : String,
    val category: String,
    val image : String,
    val penyedia : String,
    val lowest_price : String
): Serializable
