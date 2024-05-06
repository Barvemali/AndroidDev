package com.barvemali.androiddev.model.adaptor

import com.barvemali.androiddev.model.entity.Apply
import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson
import kotlinx.serialization.Serializable

@JsonClass(generateAdapter = true)
@Serializable
data class ApplyJson(val id: Int,
                 val courseid: String,
                 val studentid: Int,
                 val reason: String,
                 val isteacherpass: Boolean?,
                 val ismanagerpass: Boolean?,
                 val isfinish: Boolean,
                 val teacherreason: String, val managerreason: String, var file: String?)

class ApplyJsonAdaptor {
    @ToJson
    fun toJson(apply: Apply): ApplyJson? {
        return apply.id?.let {
            ApplyJson(
                id = it,
                courseid = apply.courseid,
                studentid = apply.studentid,
                reason = apply.reason,
                isteacherpass = apply.isteacherpass,
                ismanagerpass = apply.ismanagerpass,
                isfinish = apply.isfinish,
                teacherreason = apply.teacherreason,
                managerreason = apply.managerreason,
                file = apply.file.toString()
            )
        }
    }

    @FromJson
    fun fromJson(json: ApplyJson): Apply{
        return Apply(
            id = json.id,
            courseid = json.courseid,
            studentid = json.studentid,
            reason = json.reason,
            isteacherpass = json.isteacherpass,
            ismanagerpass = json.ismanagerpass,
            isfinish = json.isfinish,
            teacherreason = json.teacherreason,
            managerreason = json.managerreason,
            file = json.file?.toByteArray()
        )
    }
}