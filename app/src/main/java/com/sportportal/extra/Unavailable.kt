package com.sportportal.extra

data class Unavailable(
    var jam : String,
    var id : String = "",
    var untime : Boolean = false,
    var isSelected: Boolean = false
)
