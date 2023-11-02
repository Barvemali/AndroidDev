package com.barvemali.androiddev.model.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Course(var id: String, var cname: String, var teacher: Int, var manager: Int)
