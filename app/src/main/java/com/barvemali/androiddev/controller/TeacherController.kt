package com.barvemali.androiddev.controller

import com.barvemali.androiddev.model.entity.Teacher
import com.barvemali.androiddev.model.service.TeacherService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TeacherController {
    val service = TeacherService()

    fun findTeacherById(id: Int): Teacher = runBlocking{
        lateinit var teacher: Teacher

        val job = launch ( Dispatchers.IO ){
            teacher = service.findTeacherById(id)
        }
        job.join()

        teacher
    }
}