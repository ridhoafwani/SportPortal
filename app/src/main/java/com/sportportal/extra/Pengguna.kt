package com.sportportal.extra

import java.io.Serializable

data class Pengguna(
    val id : String,
    val name : String,
    val email: String,
    val image : String,
    val role: String,
): Serializable
