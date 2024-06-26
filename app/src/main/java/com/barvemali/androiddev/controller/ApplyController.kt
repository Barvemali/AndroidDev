package com.barvemali.androiddev.controller

import android.util.Log
import com.barvemali.androiddev.model.entity.Apply
import com.barvemali.androiddev.model.service.ApplyService
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ApplyController {
    val service = ApplyService()

    fun getApplies(): List<Apply> = service.findApplies()

    fun createApply(apply: Apply){
//        val string = "{\"id\":\"" + null +
//                "\",\"courseid\":\"" + apply.courseid +
//                "\",\"studentid\":\"" + apply.studentid.toString() +
//                "\",\"reason\":\"" + apply.reason +
//                "\",\"file\":\"" + apply.file +
//                "\",\"isteacherpass\":\"" + apply.isteacherpass.toString() +
//                "\",\"ismanagerpass\":\"" + apply.ismanagerpass.toString() +
//                "\",\"isfinish\":\"" + apply.isfinish.toString() +
//                "\",\"teacherreason\":\"" + apply.teacherreason +
//                "\",\"managerreason\":\"" + apply.managerreason + "\"}"
        val string = Json.encodeToString(apply)
        service.post(string)
    }

    fun teacherPass(id: Int, pass: Boolean, reason: String){
        Log.d("ApplyController", "yes")
        service.teacherPass(id, pass, reason)
    }

    fun managerPass(id: Int, pass: Boolean, reason: String){
        service.managerPass(id, pass, reason)
    }

    fun confirm(id: Int){
        service.confirm(id)
    }

    fun studentSearchByCourse(course: String): List<Apply> = service.studentSearchByCourse(course)

    fun teacherSearchByCourse(id: Int, course: String): List<Apply> = service.teacherSearchByCourse(id, course)

    fun searchByPerson(teacher: String, student: String): List<Apply> = service.searchByPerson(teacher, student)
}