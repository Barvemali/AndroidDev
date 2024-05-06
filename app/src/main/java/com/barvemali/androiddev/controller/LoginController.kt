package com.barvemali.androiddev.controller

import com.barvemali.androiddev.model.entity.Student
import com.barvemali.androiddev.model.service.LoginService
import com.barvemali.androiddev.model.service.TeacherService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

val studentService = LoginService()
val teacherService = TeacherService()

fun loginController(username : String, password : String): Boolean = runBlocking {
    return@runBlocking studentService.loginVerify(username, password)
}

fun getCurrentStudentProfile(): Student{
    return studentService.currentStudent
}

fun teacherLoginController(username : String, password : String): Boolean{
    var isVerified = false
    val scope = CoroutineScope(Job() + Dispatchers.Main)
    if (username == "" || password == ""){
        return false
    }
    try {
        scope.launch {
            isVerified = async { teacherService.findTeacherById(username.toInt()).tpassword == password }.await()
        }
    } catch (e: Exception){
        return false
    }
    return isVerified
}

fun findStudentById(id: Int): Student{
    return studentService.findStudentById(id)
}