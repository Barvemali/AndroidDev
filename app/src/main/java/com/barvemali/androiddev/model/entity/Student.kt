package com.barvemali.androiddev.model.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Student(val sname: String, var studentid: Int, val spassword: String)