package com.barvemali.androiddev.model.entity

import com.squareup.moshi.JsonClass
import java.io.File

@JsonClass(generateAdapter = true)
data class Apply(val id: Int,
                 val courseid: String,
                 val studentid: Int,
                 val reason: String,
                 val file: String?,
                 val isteacherpass: Boolean?,
                 val ismanagerpass: Boolean?,
                 val isfinish: Boolean, val teacherreason: String, val managerreason: String)
