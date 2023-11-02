package com.barvemali.androiddev.model.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Teacher(val tname: String, val teacherid: Int, val tpassword: String)