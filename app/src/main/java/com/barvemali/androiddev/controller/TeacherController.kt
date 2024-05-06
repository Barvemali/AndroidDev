package com.barvemali.androiddev.controller

import com.barvemali.androiddev.model.entity.Teacher
import com.barvemali.androiddev.model.service.TeacherService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TeacherController {
    val service = TeacherService()

    suspend fun findTeacherById(id: Int): Teacher {
        return service.findTeacherById(id)
    }
}