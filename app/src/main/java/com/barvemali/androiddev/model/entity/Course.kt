package com.barvemali.androiddev.model.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Course(var id: String = "123", var cname: String = "yxc", var teacher: Int = 123, var manager: Int = 123)
