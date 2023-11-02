package com.barvemali.androiddev.controller

import com.barvemali.androiddev.model.entity.Student
import com.barvemali.androiddev.model.service.LoginService
import com.barvemali.androiddev.model.service.TeacherService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

val studentService = LoginService()
val teacherService = TeacherService()

fun loginController(username : String, password : String): Boolean {
    return studentService.loginVerify(username, password)
}

fun getCurrentStudentProfile(): Student{
    return studentService.currentStudent
}

fun teacherLoginController(username : String, password : String): Boolean = runBlocking{
    var isVerified = false

    val job = launch( Dispatchers.IO){
        isVerified = teacherService.findTeacherById(username.toInt()).tpassword == password
    }
    job.join()

    isVerified
}

fun findStudentById(id: Int): Student{
    return studentService.findStudentById(id)
}