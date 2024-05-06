package com.barvemali.androiddev.model.entity

import com.squareup.moshi.JsonClass
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = false)
@Serializable
data class Apply(
    val id: Int?,
    val courseid: String,
    val studentid: Int,
    val reason: String,
    val isteacherpass: Boolean?,
    val ismanagerpass: Boolean?,
    val isfinish: Boolean,
    val teacherreason: String, val managerreason: String, var file: ByteArray?)
