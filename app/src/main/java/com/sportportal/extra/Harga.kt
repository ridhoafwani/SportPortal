package com.sportportal.extra

import java.io.Serializable

data class Harga(
    val jam : String,
    val harga : String,
    var isSelected: Boolean = false,
    var isUnTime : Boolean = false,
    var date : String = "",
    var shouldClear : Boolean = false
): Serializable
